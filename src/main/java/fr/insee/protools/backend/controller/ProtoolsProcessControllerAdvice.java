package fr.insee.protools.backend.controller;

import fr.insee.protools.backend.service.context.exception.BadContextIOException;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.context.exception.BadContextNotJSONException;
import fr.insee.protools.backend.service.exception.IncorrectSUException;
import fr.insee.protools.backend.service.exception.ProcessDefinitionNotFoundException;
import fr.insee.protools.backend.service.exception.TaskNotFoundException;
import fr.insee.protools.backend.service.exception.VariableClassCastException;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient4xxException;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient5xxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class ProtoolsProcessControllerAdvice {



    @ExceptionHandler(BadContextIOException.class)
    public ResponseEntity<BadContextIOException> exceptionContextIOHandler(/*final HttpServletRequest req,*/ final BadContextIOException exception) {
        log.error("exceptionContextIOHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ BadContextNotJSONException.class })
    public ResponseEntity<String> exceptionContextNotXMLHandler(/*final HttpServletRequest req,*/ final BadContextNotJSONException exception) {
        log.error("exceptionContextNotXMLHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({ BadContextIncorrectException.class })
    public ResponseEntity<String> exceptionContextIncorrectHandler(/*final HttpServletRequest req,*/ final BadContextIncorrectException exception) {
        log.error("exceptionContextIncorrectHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ TaskNotFoundException.class })
    public ResponseEntity<String> exeptionTaskNotFoundHandler(/*final HttpServletRequest req, */final TaskNotFoundException exception) {
        log.error("exceptionContextIncorrectHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ ProcessDefinitionNotFoundException.class })
    public ResponseEntity<String> exeptionProcessDefinitionNotFoundHandler(/*final HttpServletRequest req, */final ProcessDefinitionNotFoundException exception) {
        log.error("exeptionProcessDefinitionNotFoundHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ WebClient4xxException.class })
    public ResponseEntity<String> exceptionWebClient4xxHandler(/*final HttpServletRequest req, */final WebClient4xxException exception) {
        log.error("exceptionWebClient4xxHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), exception.getErrorCode());
    }

    @ExceptionHandler({ WebClient5xxException.class })
    public ResponseEntity<String> exceptionWebClient5xxHandler(/*final HttpServletRequest req, */final WebClient5xxException exception) {
        log.error("exceptionWebClient5xxHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ IncorrectSUException.class })
    public ResponseEntity<String> exceptionIncorrectSUHandler(/*final HttpServletRequest req, */final IncorrectSUException exception) {
        log.error("exceptionIncorrectSUHandler  : "+String.format("%s - remSU=[%s]",exception.getMessage(),exception.getRemSU()));
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ VariableClassCastException.class })
    public ResponseEntity<String> exceptionVariableClassCastHandler(/*final HttpServletRequest req, */final VariableClassCastException exception) {
        log.error("exceptionVariableClassCastHandler  : "+exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
