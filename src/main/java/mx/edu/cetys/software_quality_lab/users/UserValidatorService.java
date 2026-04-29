package mx.edu.cetys.software_quality_lab.users;

import mx.edu.cetys.software_quality_lab.users.exceptions.DuplicateUsernameException;
import org.springframework.stereotype.Service;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;

@Service
public class UserValidatorService {

    public static final Character UNDERSCORE = '_';
    private static final String VALID_ACENTOS = "áéíóúñÁÉÍÓÚÑ";
    private static final String VALID_LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";

    private static UserRepository userRepository = null;

    public UserValidatorService(EmailValidatorService emailValidatorService, UserRepository userRepository) {
        UserValidatorService.userRepository = userRepository;
    }

    public static boolean isValidUser(String username, String firstName, String lastName, int age, String phone, String email) {

        EmailValidatorService emailValidator = new EmailValidatorService();

        if (!validUsername(username)) {
            System.out.println("Invalid username");
            return false;
        }

        if (!isValidName(firstName)) {
            System.out.println("Invalid first name");
            return false;
        }

        if (!isValidName(lastName)) {
            System.out.println("Invalid last name");
            return false;
        }

        if (age <= 12 || age > 120) return false;

        if (!isValidPhone(phone)) return false;

        if (!emailValidator.isValid(email)) {
            System.out.println("Invalid email");
            return false;
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username '" + username + "' is already taken.");
        }

        return true;
    }

    private static boolean validUsername(String username) {
        if (username == null) return false;
        if (username.length() < 5 || username.length() > 20) return false;
        if (username.charAt(0) == UNDERSCORE || username.charAt(username.length() - 1) == UNDERSCORE) return false;

        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!isLowerCaseLetter(c) && !isDigitChar(c) && c != UNDERSCORE) return false;
        }

        return true;
    }

    private static boolean isValidName(String name) {
        if (name == null) return false;
        if (name.length() < 2 || name.length() > 50) return false;

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!isValidLetter(c) && !isValidAcento(c)) return false;
        }
        return true;
    }

    private static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        if (phone.length() != 10) return false;
        for (int i = 0; i < phone.length(); i++) {
            if (!isDigitChar(phone.charAt(i))) return false;
        }
        return true;
    }

    private static boolean isLowerCaseLetter(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static boolean isValidLetter(char c) {
        return VALID_LETTERS.indexOf(c) >= 0;
    }

    private static boolean isValidAcento(char c) {
        return VALID_ACENTOS.indexOf(c) >= 0;
    }

    private static boolean isDigitChar(char c) {
        return Character.isDigit(c);
    }
}