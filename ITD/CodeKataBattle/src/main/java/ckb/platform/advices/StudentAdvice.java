package ckb.platform.advices;

import ckb.platform.exceptions.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice // enables the class to be shared across multiple controller classes
public class StudentAdvice {

    @ResponseBody // signals that this advice is rendered straight into the response body
    @ExceptionHandler(StudentNotFoundException.class) // configures the advice to only respond if an StudentNotFoundException is thrown
    @ResponseStatus(HttpStatus.NOT_FOUND) // says to issue an HttpStatus.NOT_FOUND, i.e. an HTTP 404
    String studentNotFoundHandler(StudentNotFoundException ex) {
        return ex.getMessage();
    }
}
