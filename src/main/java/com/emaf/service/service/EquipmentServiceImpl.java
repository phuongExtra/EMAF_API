package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.logging.AppLogger;
import com.emaf.service.common.utils.IDGenerator;
import com.emaf.service.component.S3Component;
import com.emaf.service.entity.Equipment;
import com.emaf.service.enumeration.EEquipmentStatus;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.model.EquipmentForm;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.repository.EquipmentRepository;
import com.emaf.service.repository.EventEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EquipmentServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/10/09
 */
@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EventEquipmentRepository eventEquipmentRepository;

    @Autowired
    private S3Component s3Component;

    @Autowired
    private Message message;


    @Override
    public PagedResponse<Equipment> getAllEquipment(final String search, final Pageable pageable) {
        Page<Equipment> equipments = equipmentRepository.getAllEquipment(search, pageable);

        return new PagedResponse<>(equipments.getContent(), equipments.getNumber(), equipments.getSize(), equipments.getTotalElements(), equipments.getTotalPages());
    }

    @Override
    public List<Equipment> getAllEquipmentAvailable(String borrowTime, String returnTime) {
        return equipmentRepository.getAllEquipmentAvailable(borrowTime, returnTime, Arrays.asList(EEventStatus.APPROVED.name(), EEventStatus.RUNNING.name()))
                .stream()
                .filter(equipment -> {
                    int numberOfEquipment = numberOfEquipmentAvailable(equipment, borrowTime, returnTime);
                    equipment.setQuantityAvailable(numberOfEquipment);
                    return numberOfEquipment > 0;
                }).collect(Collectors.toList());
    }

    // lấy ra số lượng còn lại có thể cho mượn của 1 equRoipment
    @Override
    public int numberOfEquipmentAvailable(String equipmentId, String borrowTime, String returnTime) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        return numberOfEquipmentAvailable(equipment, borrowTime, returnTime);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public String createEquipment(final String userId, final EquipmentForm equipmentForm) {
        Equipment equipment = Equipment.builder()
                .id(IDGenerator.generateID(equipmentRepository, 10))
                .equipmentName(equipmentForm.getEquipmentName())
                .status(EEquipmentStatus.AVAILABLE)
                .createdBy(userId)
                .updatedBy(userId)
                .quantity(equipmentForm.getQuantity())
                .build();
        if (StringUtils.hasLength(equipmentForm.getNote())) {
            equipment.setNote(equipmentForm.getNote());
        }
        equipmentRepository.save(equipment);
        return equipment.getId();
    }

    @Override
    public Equipment getEquipmentById(final String equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean deleteById(final String equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        equipmentRepository.delete(equipment);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public String uploadImage(final String userId, final MultipartFile image, final String equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        try {
            if (StringUtils.hasLength(equipment.getImage())) {
                String fileName = equipment.getImage().substring(equipment.getImage().lastIndexOf("/") + 1);
                s3Component.delete("images/" + equipmentId + "/image", fileName);
            }
            String imageUpload = s3Component.upload("images/" + equipmentId + "/image", image);
            equipment.setImage(imageUpload);
            equipment.setUpdatedBy(userId);
            equipmentRepository.save(equipment);
            return imageUpload;
        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public String updateEquipment(final String userId, final EquipmentForm equipmentForm) {
        if (!StringUtils.hasLength(equipmentForm.getEquipmentId())) {
            throw new ServerErrorException(message.getWarnNoData() + " equipmentId null");
        }
        Equipment equipment = equipmentRepository.findById(equipmentForm.getEquipmentId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " equipment null"));
        equipment.setNote(equipmentForm.getNote());
        equipment.setEquipmentName(equipmentForm.getEquipmentName());
        equipment.setUpdatedBy(userId);
        equipment.setQuantity(equipmentForm.getQuantity());
        equipment.setStatus(EEquipmentStatus.valueOf(equipmentForm.getStatus()));
        equipmentRepository.save(equipment);
        return equipment.getId();
    }

    public int numberOfEquipmentAvailable(Equipment equipment, String borrowTime, String returnTime) {
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        if (Long.parseLong(returnTime) <= Long.parseLong(timeNow)) {
            return 0;
        }

        Integer numberOfEquipmentInEvent = eventEquipmentRepository.getSumQuantityEquipmentInEvent(equipment.getId(), borrowTime, returnTime, Arrays.asList(EEventStatus.APPROVED, EEventStatus.RUNNING)).orElse(0);
        int quantity = equipment.getQuantity();
        return quantity - numberOfEquipmentInEvent;
    }
}
