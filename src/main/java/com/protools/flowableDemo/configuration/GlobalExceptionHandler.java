package com.protools.flowableDemo.configuration;

import com.protools.flowableDemo.model.exceptions.FileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({FileNotFoundException.class})
    public ResponseEntity<String> fileUploadNotFoundException(final HttpServletRequest req, final FileNotFoundException exception) {
        log.error("Uploaded file not Found : ",exception);
        return new ResponseEntity<>("Error uploading file, you should provide a valid file", HttpStatus.BAD_REQUEST);
    }
}
