package ckb.platform.advices;

import ckb.platform.exceptions.BattleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BattleAdvice {

    @ResponseBody
    @ExceptionHandler(BattleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String battleNotFoundHandler(BattleNotFoundException ex) {
        return ex.getMessage();
    }
}
