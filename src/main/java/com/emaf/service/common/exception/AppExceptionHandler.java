package com.emaf.service.common.exception;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.logging.AppLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AppExceptionHandler
 *
 * @author khal
 * @since 2021/10/02
 */
@ControllerAdvice
public class AppExceptionHandler {

    private MessageSource messageSource;
    private Message message;

    /**
     * Controller with message source (dependency injection)
     *
     * @param messageSource
     */
    @Autowired
    public AppExceptionHandler(final MessageSource messageSource,
                               final Message message) {
        this.messageSource = messageSource;
        this.message = message;
    }

    @ExceptionHandler
    @ResponseBody
    public ErrorMessageModel handleException(final Exception e, final HttpServletRequest request, final HttpServletResponse response) {
        ErrorMessageModel errorMessageDS = new ErrorMessageModel();
        if (e instanceof MethodArgumentNotValidException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            List<String> msgLst = ((MethodArgumentNotValidException) e).getBindingResult()
                    .getAllErrors().stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            errorMessageDS = buildMessage(msgLst, HttpServletResponse.SC_BAD_REQUEST);
        } else if (e instanceof ConstraintViolationException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) e).getConstraintViolations();
            List<String> msgLst = new ArrayList<>();
            Iterator<ConstraintViolation<?>> it = violations.iterator();
            while (it.hasNext()) {
                ConstraintViolation<?> violation = it.next();
                msgLst.add(violation.getMessage());
            }
            errorMessageDS = buildMessage(msgLst, HttpServletResponse.SC_BAD_REQUEST);
        } else if (e instanceof MissingServletRequestParameterException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessageDS = buildMessage(message.getErrorBadRequest(), null, HttpServletResponse.SC_BAD_REQUEST);
        } else if (e instanceof BadCredentialsException || e instanceof UsernameNotFoundException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            errorMessageDS = buildMessage(e.getMessage(), null, HttpServletResponse.SC_UNAUTHORIZED);
        } else if (e instanceof ExistenceException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessageDS = buildMessage(e.getMessage(), null, HttpServletResponse.SC_BAD_REQUEST);
        } else if (e instanceof ServerErrorException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessageDS = buildMessage(e.getMessage(), null, HttpServletResponse.SC_BAD_REQUEST);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            errorMessageDS = buildMessage(message.getErrorProceedError(), null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        AppLogger.errorLog(errorMessageDS.getMessage(), e);

        return errorMessageDS;
    }

    /**
     * Build list message from message properties value
     *
     * @param messageLst
     * @return
     */
    private ErrorMessageModel buildMessage(final List<String> messageLst, final int status) {
        ErrorMessageModel errorMessageModel = new ErrorMessageModel();

        StringBuilder sb = new StringBuilder("");
        for (String message : messageLst)
            sb.append(buildMessage(message, null, status)).append("\n");
        errorMessageModel.setMessage(sb.toString());

        return errorMessageModel;
    }

    /**
     * Build message from message properties value
     *
     * @param message
     * @param args
     * @return
     */
    private ErrorMessageModel buildMessage(final String message, final Object[] args, final int status) {
        ErrorMessageModel errorMessageModel = null;

        if (!StringUtils.isEmpty(message)) {
            String[] msgArr = message.split(",");
            if (Objects.nonNull(msgArr) && msgArr.length != 0) {
                String msgType = msgArr[0].split(":")[1];
                String msgId = msgArr[1].split(":")[1];
                String msg = msgArr[2].split(":")[1];

                if (Objects.nonNull(args) && args.length != 0) {
                    for (int argIndex = 0; argIndex < args.length; argIndex++) {
                        String argName = "{arg" + argIndex + "}";
                        msg = msg.replace(argName, String.valueOf(args[argIndex]));
                    }
                }

                errorMessageModel = new ErrorMessageModel(msgType, msgId, msg, String.valueOf(status));
            }
        }

        return errorMessageModel;
    }
}
