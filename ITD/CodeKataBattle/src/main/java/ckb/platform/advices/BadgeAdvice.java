package ckb.platform.advices;

import ckb.platform.exceptions.BadgeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BadgeAdvice {

    @ResponseBody
    @ExceptionHandler(BadgeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String badgeNotFoundHandler(BadgeNotFoundException ex) {
        return ex.getMessage();
    }
}
