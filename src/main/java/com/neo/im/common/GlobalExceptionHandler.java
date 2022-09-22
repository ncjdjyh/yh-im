package com.neo.im.common;

import cn.hutool.core.date.DateUtil;
import com.neo.im.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * 全局异常处理
 *
 * @author yuanhao.jiang
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final int BIZ_EXCEPTION_STATUS = 10000;

    private void printErrorMessage(Exception e) {
        log.error("发生业务异常 原因是:", e);
        log.info("时间是:{}", DateUtil.formatLocalDateTime(LocalDateTime.now()));
    }

    @ExceptionHandler(value = BizException.class)
    public ResponseEntity<?> handleBizException(BizException e) {
        printErrorMessage(e);
        return ResponseEntity.status(BIZ_EXCEPTION_STATUS).body(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        printErrorMessage(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("系统错误");
    }
}