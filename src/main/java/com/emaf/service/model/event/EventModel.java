package com.emaf.service.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * EventModel
 *
 * @author: VuongVT2
 * @since: 2022/10/10
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventModel {
    private Date date;
}
