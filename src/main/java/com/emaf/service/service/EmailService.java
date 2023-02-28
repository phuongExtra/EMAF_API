package com.emaf.service.service;

import com.emaf.service.model.email.EmailMessageConfig;

import javax.mail.MessagingException;
import java.util.List;

/**
 * EmailService
 *
 * @author: VuongVT2
 * @since: 2022/05/13
 */
public interface EmailService {
    void sendEmail(List<EmailMessageConfig> configs) throws MessagingException;
}
