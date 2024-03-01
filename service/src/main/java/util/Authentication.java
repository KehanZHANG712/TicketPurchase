package util;

import datasource.UserMapper;
import domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class Authentication {

    public static int getUserPowerFromSession(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        return currentUser.getPower();
    }

    public static String hashPassword(String password) {
        Pbkdf2PasswordEncoder encoder = getEncoder();
        return encoder.encode(password);
    }

    public static Pbkdf2PasswordEncoder getEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    public static boolean checkPasswordMatchesHash(String password, String hash) {
        return getEncoder().matches(password, hash);
    }
}
