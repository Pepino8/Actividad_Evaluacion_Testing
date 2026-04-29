package mx.edu.cetys.software_quality_lab.validators;

import org.springframework.stereotype.Service;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;

@Service
public class userValidatorService {

    /**
     * Registrar un nuevo usuario aplicando todas las reglas de negocio.
     *
     * Reglas a implementar (lanzar InvalidUserDataException a menos que se indique):
     *  1. Username  — entre 5 y 20 caracteres, solo letras minúsculas, dígitos y guion bajo (_),
     *                 NO debe comenzar ni terminar con guion bajo X
     *  2. First name — entre 2 y 50 caracteres, solo letras (se permiten acentos: á, é, ñ, etc.) X
     *  3. Last name  — entre 2 y 50 caracteres, solo letras (se permiten acentos) X
     *  4. Age        — debe ser mayor a 12 y menor o igual a 120 X
     *  5. Phone      — exactamente 10 dígitos, sin letras ni símbolos
     *  6. Email      — delegar a emailValidatorService.isValid(email);
     *                  lanzar InvalidUserDataException si regresa false
     *  7. Unicidad del username — si userRepository.existsByUsername regresa true,
     *                             lanzar DuplicateUsernameException
     */

    public static final Character UNDERSCORE = '_';
    private static final String VALID_ACENTOS = "áéíóúñ";
    private static final String VALID_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String VOWELS = "aeiou";
    private static final String NUMBERS = "0123456789";

    private final EmailValidatorService emailValidatorService;

    public userValidatorService(EmailValidatorService emailValidatorService) {
        this.emailValidatorService = emailValidatorService;
    }


    public boolean isValidUser(String username, String firstName, String lastName, int age, String phone, String email) {

        EmailValidatorService emailValidator = new EmailValidatorService();

        //check username
        if(!validUsername(username)) return false;

        //check first name
        if(!isValidFirstName(firstName)) return false;

        //check last name
        if(!isValidFirstName(lastName)) return false;

        //check age
        if(age <= 12 || age >= 120) return false;

        //check phone number
        if(!isValidNumber(phone)) return false;

        //check email to isValidEmail
        if(!emailValidator.isValid(email)) return false;
        return true;
    }

    private boolean validUsername(String username) {
        if(username == null) return false;
        for(int i = 0; i < username.length(); i++) {
            if(username.length() > 20 || username.length() < 5) return false;

            if(username.charAt(0) == UNDERSCORE || username.charAt(username.length() - 1) == UNDERSCORE) return false;

            if(!isVowel(username.charAt(i)) || !isDigit(username.charAt(i)) || username.charAt(i) != UNDERSCORE) return false;
        }

        return true;
    }

    private boolean isValidFirstName(String firstName) {
        if(firstName == null) return false;
        if(firstName.length() < 2 || firstName.length() > 50) return false;

        for(int i = 0; i < firstName.length(); i++) {
            if(!isValidLetter(firstName.charAt(i)) || !isValidAcentos(firstName.charAt(i))) return false;
        }
        return true;
    }

    private boolean isValidNumber(String phone){
        if(phone == null) return false;
        if(phone.length() != 10) return false;
        for(int i = 0; i < phone.length(); i++) {
            if(!isValidNumber(phone.charAt(i))) return false;
        }
        return true;
    }

    private boolean isVowel(char c) {
        return VOWELS.indexOf(c) >= 0;
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isValidLetter(char c) {
        return VALID_LETTERS.indexOf(c) >= 0;
    }

    private boolean isValidAcentos(char c) {
        return VALID_ACENTOS.indexOf(c) >= 0;
    }

    private boolean isValidNumber(char c) {
        return NUMBERS.indexOf(c) >= 0;
    }
}
