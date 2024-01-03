package ckb.platform.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
        super("Could not find user, wrong email or password");
    }
}
