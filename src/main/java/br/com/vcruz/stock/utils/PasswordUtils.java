package br.com.vcruz.stock.utils;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class PasswordUtils {

    public static boolean validate(String password) {
        // Senha de 8 ou mais caracteres com pelo menos um dígito, pelo menos um
        // letra minúscula, pelo menos uma letra maiúscula, pelo menos uma
        // caractere especial sem espaços em branco
        final String PASSWORD_REGEX
                = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        final Pattern PASSWORD_PATTERN
                = Pattern.compile(PASSWORD_REGEX);

        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static String encryptPassword(String password) throws NoSuchAlgorithmException {
        return HashUtils.encryptThisString(password);
    }
}
