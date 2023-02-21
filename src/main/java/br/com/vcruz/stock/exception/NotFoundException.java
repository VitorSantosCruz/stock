package br.com.vcruz.stock.exception;

/**
 *
 * @author vcruz
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
