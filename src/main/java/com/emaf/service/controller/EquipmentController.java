package com.emaf.service.controller;

import com.emaf.service.common.constant.AppConstant;
import com.emaf.service.entity.Equipment;
import com.emaf.service.model.EquipmentForm;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.repository.EquipmentRepository;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EquipmentController
 *
 * @author: VuongVT2
 * @since: 2022/10/08
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/equipment")
public class EquipmentController {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private EquipmentService equipmentService;

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @GetMapping(value = "/all-equipment", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<Equipment> getAllEquipment(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                    @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                    @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("equipmentName").ascending());
        return equipmentService.getAllEquipment(search, pageable);
    }

    @GetMapping(value = "/all-equipment-available", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Equipment> getAllEquipmentAvailable(@RequestParam(name = "borrowTime") String borrowTime,
                                                    @RequestParam(name = "returnTime") String returnTime) {


        return equipmentService.getAllEquipmentAvailable(borrowTime, returnTime);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createEquipment(@RequestBody @Valid EquipmentForm equipmentForm,
                                  HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return equipmentService.createEquipment(userId, equipmentForm);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateEquipment(@RequestBody @Valid EquipmentForm equipmentForm,
                                  HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return equipmentService.updateEquipment(userId, equipmentForm);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @PostMapping(value = "/upload-image", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadImage(@RequestParam(value = "image") @NotNull MultipartFile image,
                              @RequestParam(value = "equipmentId") String equipmentId,
                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return equipmentService.uploadImage(userId, image, equipmentId);
    }

    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public Equipment getEquipmentById(@RequestParam(name = "equipmentId") String equipmentId) {
        return equipmentService.getEquipmentById(equipmentId);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteById(@RequestParam(name = "equipmentId") String equipmentId) {
        return equipmentService.deleteById(equipmentId);
    }

}
