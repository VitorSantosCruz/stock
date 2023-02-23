package br.com.vcruz.stock.exception;

/**
 *
 * @author vcruz
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
