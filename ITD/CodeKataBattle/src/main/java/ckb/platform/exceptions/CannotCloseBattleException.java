package ckb.platform.exceptions;

public class CannotCloseBattleException extends RuntimeException{
    public CannotCloseBattleException(Long id) {
        super("Could not close battle " + id);
    }
}
