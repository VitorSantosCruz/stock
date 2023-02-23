package br.com.vcruz.stock.exception;

/**
 *
 * @author vcruz
 */
public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }
}
