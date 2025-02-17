package fr.house.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import fr.house.Beans.ResponseAPI;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionsHandler {

    @ExceptionHandler(DeviceException.class)
    public ResponseEntity<ResponseAPI> handleDeviceException(Exception exception, WebRequest request) {
        LocalDateTime time = LocalDateTime.now();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String type = "Device Error";
        List<String> errors = Collections.singletonList(exception.getMessage());

        ResponseAPI body = new ResponseAPI(time, status, type, errors);

        body.getErrors_msg().forEach(
                error -> log.error(body.getType() + " : \n\t" + error)
        );

        return new ResponseEntity<>(body, status);

    }


    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ResponseAPI> handleDatabaseException(Exception exception, WebRequest request) {
        LocalDateTime time = LocalDateTime.now();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String type = "Database Error";
        List<String> errors = Collections.singletonList(exception.getMessage());

        ResponseAPI body = new ResponseAPI(time, status, type, errors);

        body.getErrors_msg().forEach(
                error -> log.error(body.getType() + " : \n\t" + error)
        );

        return new ResponseEntity<>(body, status);

    }



}
