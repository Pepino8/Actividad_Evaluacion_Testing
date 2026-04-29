package mx.edu.cetys.software_quality_lab.users;

import mx.edu.cetys.software_quality_lab.users.exceptions.DuplicateUsernameException;
import mx.edu.cetys.software_quality_lab.users.exceptions.InvalidUserDataException;
import mx.edu.cetys.software_quality_lab.users.exceptions.UserNotFoundException;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    // EmailValidatorService debe ser mockeado — en pruebas unitarias no probamos dependencias externas
    @Mock
    EmailValidatorService emailValidatorService;

    @InjectMocks
    UserService userService;

    private User buildMockSavedUser(Long id, String username, String firstName, String lastName, String phone, String email, Integer age) {
        var user = new User(username, firstName, lastName, phone, email, age);
        user.setId(id);
        return user;
    }

    // ─── Caso exitoso ─────────────────────────────────────────────────────────

    @Test
    void shouldRegisterUserSuccessfully() {
        // TODO: arrange — construir un UserRequest válido, mockear emailValidatorService.isValid para que regrese true,
        //       mockear userRepository.existsByUsername para que regrese false,
        //       mockear userRepository.save para que regrese un User con id
        // TODO: act — llamar a userService.registerUser(request)
        // TODO: assert — verificar id, username, status == "ACTIVE"; confirmar que save fue llamado una vez
        var request = new UserController.UserRequest("juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmail.com", 25);

        when(emailValidatorService.isValid(anyString())).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(
                buildMockSavedUser(1L, "juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmail.com", 25)
        );

        var response = userService.registerUser(request);
        assertEquals("juan_pro", response.username());
        assertEquals("ACTIVE", response.status());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // TODO: arrange — mockear userRepository.findById para que regrese un Optional<User> con datos
        // TODO: act — llamar a userService.getUserById(1L)
        // TODO: assert — verificar que los campos del response coincidan con el mock
        var mockUser = buildMockSavedUser(1L, "juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmail.com", 25);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));

        var response = userService.getUserById(1L);

        assertEquals("juan_pro", response.username());
        assertEquals("Juan", response.firstName());
        assertEquals("ACTIVE", response.status());
    }

    @Test
    void shouldSuspendActiveUserSuccessfully() {
        // TODO: arrange — mockear findById con un usuario ACTIVE
        // TODO: act — llamar a userService.suspendUser(id)
        // TODO: assert — verificar que el status regresado sea "SUSPENDED"; confirmar que save fue llamado
        var mockUser = buildMockSavedUser(1L, "juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmal.com", 25);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(any())).thenReturn(mockUser);
        var response = userService.suspendUser(1L);
        assertEquals("SUSPENDED", response.status());
        verify(userRepository, times(1)).save(any());
    }

    // ─── Validaciones de Username ─────────────────────────────────────────────

    @Test
    void shouldThrowWhenUsernameTooShort() {
        // TODO: construir request con username de 4 caracteres
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juan", "Juan", "Manuel", "6623792671", "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenUsernameTooLong() {
        // TODO: construir request con username de 21 caracteres
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juanusernameme_gaultralargo", "Juan", "Manuel", "6623792671", "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenUsernameHasInvalidChars() {
        // TODO: username con mayúsculas o caracteres especiales, ej. "User@Name"
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("User@Name", "Juan", "Manuel", "6623792671", "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenUsernameStartsWithUnderscore() {
        // TODO: username "_nombrevalido"
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("_nombrevalido", "Juan", "Manuel", "6623792671",  "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenUsernameEndsWithUnderscore() {
        // TODO: username "nombrevalido_"
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("nombrevalido_", "Juan", "Manuel", "6623792671",  "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    // ─── Validaciones de Nombre ───────────────────────────────────────────────

    @Test
    void shouldThrowWhenFirstNameTooShort() {
        // TODO: firstName de 1 carácter
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("nombrevalido", "J", "Manuel", "6623792671",  "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenFirstNameContainsNumbers() {
        // TODO: firstName como "Juan5"
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("nombrevalido", "Juan5", "Manuel", "6623792671",  "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenLastNameTooShort() {
        // TODO: lastName de 1 carácter
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juan_pro", "Juan", "M", "6623792671",  "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenLastNameContainsNumbers() {
        // TODO: lastName como "Perez2"
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juan_pro", "Juan", "Perez2", "6623792671", "j4an#gmal.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    // ─── Validaciones de Age ─────────────────────────────────────────────────

    @Test
    void shouldThrowWhenAgeIsExactlyTwelve() {
        // TODO: age = 12 — caso límite (boundary): debe ser MAYOR a 12, no mayor o igual
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmal.com", 12);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenAgeIsBelowTwelve() {
        // TODO: age = 5
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmal.com", 5);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenAgeExceedsMaximum() {
        // TODO: age = 121 — excede el máximo permitido de 120
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmal.com", 121);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    // ─── Validaciones de Phone ───────────────────────────────────────────────

    @Test
    void shouldThrowWhenPhoneHasWrongLength() {
        // TODO: phone con 9 u 11 dígitos
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juan_pro", "Juan", "Manuel", "662379267", "j4an#gmal.com", 12);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    @Test
    void shouldThrowWhenPhoneContainsLetters() {
        // TODO: phone como "123456789a"
        // TODO: assertThrows InvalidUserDataException
        var request = new UserController.UserRequest("juan_pro", "Juan", "Manuel", "123456789a", "j4an#gmal.com", 12);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
    }

    // ─── Validación de Email ──────────────────────────────────────────────────

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        // TODO: mockear emailValidatorService.isValid(anyString()) para que regrese false
        // TODO: assertThrows InvalidUserDataException
        // TODO: verificar que emailValidatorService.isValid fue llamado (verify)
        var request = new UserController.UserRequest("juan_pro", "Juan", "Perez", "6623792671", "j4an#gmail.com", 25);
        when(emailValidatorService.isValid(anyString())).thenReturn(false);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(request));
        verify(emailValidatorService, times(1)).isValid(anyString());
    }

    // ─── Unicidad de Username ─────────────────────────────────────────────────

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        // TODO: mockear emailValidatorService.isValid para que regrese true
        // TODO: mockear userRepository.existsByUsername para que regrese true
        // TODO: assertThrows DuplicateUsernameException
        // TODO: verificar que userRepository.save NUNCA fue llamado (verify never)
        var request = new UserController.UserRequest("juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmail.com", 25);
        when(emailValidatorService.isValid(anyString())).thenReturn(true);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        assertThrows(DuplicateUsernameException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any());
    }

    // ─── Not found ───────────────────────────────────────────────────────────

    @Test
    void shouldThrowWhenUserNotFound() {
        // TODO: mockear userRepository.findById para que regrese Optional.empty()
        // TODO: assertThrows UserNotFoundException
        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void shouldThrowWhenSuspendingAlreadySuspendedUser() {
        // TODO: mockear findById con un usuario SUSPENDED
        // TODO: assertThrows InvalidUserDataException
        var user = new User("juan_pro", "Juan", "Manuel", "6623792671", "j4an#gmal.com", 25);
        user.setStatus(UserStatus.SUSPENDED);
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(user));
        assertThrows(InvalidUserDataException.class, () -> userService.suspendUser(1L));
    }
}
