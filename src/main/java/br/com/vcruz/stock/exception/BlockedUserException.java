package br.com.vcruz.stock.exception;

/**
 *
 * @author vcruz
 */
public class BlockedUserException extends RuntimeException {

    public BlockedUserException(String message) {
        super(message);
    }
}
