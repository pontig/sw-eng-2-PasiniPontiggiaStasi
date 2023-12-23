package ckb.platform.advices;

import ckb.platform.exceptions.TournamentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TournamentAdvice {

    @ResponseBody
    @ExceptionHandler(TournamentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tournamentNotFoundHandler(TournamentNotFoundException ex) {
        return ex.getMessage();
    }
}
