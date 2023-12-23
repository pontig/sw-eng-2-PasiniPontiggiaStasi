package ckb.platform.exceptions;

public class EducatorNotFoundException extends RuntimeException{
    public EducatorNotFoundException(Long id) {
        super("Could not find educator " + id);
    }
}
