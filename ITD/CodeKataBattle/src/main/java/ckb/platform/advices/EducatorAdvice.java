package ckb.platform.advices;


import ckb.platform.exceptions.EducatorNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EducatorAdvice {

    @ResponseBody
    @ExceptionHandler(EducatorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String educatorNotFoundHandler(EducatorNotFoundException ex) {
        return ex.getMessage();
    }
}
