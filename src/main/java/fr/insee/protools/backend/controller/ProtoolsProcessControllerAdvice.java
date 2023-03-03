package fr.insee.protools.backend.controller;

import fr.insee.protools.backend.service.exception.TaskNotFoundException;
import fr.insee.protools.backend.service.context.exception.BadContextIOException;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.context.exception.BadContextNotXMLException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class ProtoolsProcessControllerAdvice {



    @ExceptionHandler(BadContextIOException.class)
    public ResponseEntity<BadContextIOException> exceptionContextIOHandler(final HttpServletRequest req, final BadContextIOException exception) {
        log.error("exceptionContextIOHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ BadContextNotXMLException.class })
    public ResponseEntity<String> exceptionContextNotXMLHandler(final HttpServletRequest req, final BadContextNotXMLException exception) {
        log.error("exceptionContextNotXMLHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({ BadContextIncorrectException.class })
    public ResponseEntity<String> exceptionContextIncorrectHandler(final HttpServletRequest req, final BadContextIncorrectException exception) {
        log.error("exceptionContextIncorrectHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ TaskNotFoundException.class })
    public ResponseEntity<String> exeptionTaskNotFoundHandler(final HttpServletRequest req, final TaskNotFoundException exception) {
        log.error("exceptionContextIncorrectHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

}
