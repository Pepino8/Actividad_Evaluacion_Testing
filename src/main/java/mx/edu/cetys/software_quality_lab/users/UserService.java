package mx.edu.cetys.software_quality_lab.users;

import mx.edu.cetys.software_quality_lab.pets.exceptions.InvalidPetDataException;
import mx.edu.cetys.software_quality_lab.pets.exceptions.PetNotFoundException;
import mx.edu.cetys.software_quality_lab.users.exceptions.InvalidUserDataException;
import mx.edu.cetys.software_quality_lab.users.exceptions.UserNotFoundException;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mx.edu.cetys.software_quality_lab.users.UserValidatorService;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final EmailValidatorService emailValidatorService;

    public UserService(UserRepository userRepository, EmailValidatorService emailValidatorService) {
        this.userRepository = userRepository;
        this.emailValidatorService = emailValidatorService;
    }

    /**
     * Registrar un nuevo usuario aplicando todas las reglas de negocio.
     *
     * Reglas a implementar (lanzar InvalidUserDataException a menos que se indique):
     *  1. Username  — entre 5 y 20 caracteres, solo letras minúsculas, dígitos y guion bajo (_),
     *                 NO debe comenzar ni terminar con guion bajo
     *  2. First name — entre 2 y 50 caracteres, solo letras (se permiten acentos: á, é, ñ, etc.)
     *  3. Last name  — entre 2 y 50 caracteres, solo letras (se permiten acentos)
     *  4. Age        — debe ser mayor a 12 y menor o igual a 120
     *  5. Phone      — exactamente 10 dígitos, sin letras ni símbolos
     *  6. Email      — delegar a emailValidatorService.isValid(email);
     *                  lanzar InvalidUserDataException si regresa false
     *  7. Unicidad del username — si userRepository.existsByUsername regresa true,
     *                             lanzar DuplicateUsernameException
     */
    UserController.UserResponse registerUser(UserController.UserRequest request) {
        log.info("Iniciando registro de usuario, username={}", request.username());
        // TODO: implementar las reglas 1-7, luego guardar en BD y mapear la respuesta
        if(UserValidatorService.isValidUser(request.username(), request.firstName(), request.lastName(), request.age(), request.phone(), request.email())){
            throw new InvalidUserDataException("User request not valid");
        }
        var savedUser = userRepository.save(new User(request.username(), request.firstName(), request.lastName(), request.phone(), request.email(), request.age()));
        return mapToResponse(savedUser);
    }

    /**
     * Buscar un usuario por ID.
     * Lanzar UserNotFoundException (HTTP 404) si el usuario no existe.
     */
    UserController.UserResponse getUserById(Long id) {
        log.info("Buscando usuario por ID, id={}", id);
        log.info("Getting pet by id {}", id);
        // Validar ssi petId es correcto (numerico, mayor a 0) else fail with 400
        if(id == null || id <= 0){
             throw new InvalidUserDataException("Invalid user id");
        }

        var user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        return mapToResponse(user.get());
    }

    /**
     * Suspender un usuario ACTIVO.
     * Lanzar UserNotFoundException si el usuario no existe.
     * Lanzar InvalidUserDataException si el usuario ya está SUSPENDED.
     */
    UserController.UserResponse suspendUser(Long id) {
        log.info("Suspendiendo usuario, id={}", id);
        // TODO: buscar usuario, validar status, cambiar a SUSPENDED, guardar, mapear y regresar

        var user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        if(user.get().getStatus() == UserStatus.SUSPENDED){
            throw new InvalidUserDataException("User with id " + id + " is already suspended");
        }
        user.get().setStatus(UserStatus.SUSPENDED);
        userRepository.save(user.get());
        return mapToResponse(user.get());
    }

    private UserController.UserResponse mapToResponse(User user) {
        return new UserController.UserResponse(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail(), user.getAge(), user.getStatus().toString());
    }
}
