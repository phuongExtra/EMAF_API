package com.emaf.service.service;

import com.emaf.service.entity.Equipment;
import com.emaf.service.model.EquipmentForm;
import com.emaf.service.model.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * EquipmentService
 *
 * @author: VuongVT2
 * @since: 2022/10/09
 */
public interface EquipmentService {
    PagedResponse<Equipment> getAllEquipment(String search, Pageable pageable);

    List<Equipment> getAllEquipmentAvailable(String borrowTime, String returnTime);

    int numberOfEquipmentAvailable(String equipmentId, String borrowTime, String returnTime);

    String createEquipment(String userId, EquipmentForm equipmentForm);

    Equipment getEquipmentById(String equipmentId);

    boolean deleteById(String equipmentId);

    String uploadImage(String userId, MultipartFile image, String equipmentId);

    String updateEquipment(String userId, EquipmentForm equipmentForm);
}
