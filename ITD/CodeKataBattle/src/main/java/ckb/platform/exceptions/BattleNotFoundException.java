package ckb.platform.exceptions;

public class BattleNotFoundException extends RuntimeException{
    public BattleNotFoundException(Long id) {
        super("Could not find battle " + id);
    }
}
