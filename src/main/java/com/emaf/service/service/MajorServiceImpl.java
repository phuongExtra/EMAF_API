package com.emaf.service.service;

import com.emaf.service.entity.EventRoomSchedule;
import com.emaf.service.entity.Major;
import com.emaf.service.entity.Room;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.model.event.EventRoomData;
import com.emaf.service.repository.EventRoomScheduleRepository;
import com.emaf.service.repository.MajorRepository;
import com.emaf.service.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MajorServiceImpl
 *
 * @author: PhuongLN
 * @since: 2022/10/09
 */
@Service
public class MajorServiceImpl implements MajorService {

    @Autowired
    private MajorRepository majorRepository;


    @Override
    public List<Major> getAllMajor() {
        return majorRepository.findAll();
    }
}
