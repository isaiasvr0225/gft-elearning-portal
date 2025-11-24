package com.gft.application.user;

import com.gft.domain.City;
import com.gft.domain.User;
import com.gft.domain.exceptions.DocumentTypeException;
import com.gft.domain.exceptions.RoleException;
import com.gft.domain.exceptions.UserException;
import com.gft.infrastructure.dto.LoginUsingNITDTO;
import com.gft.infrastructure.dto.user.ChangePasswordRequestDTO;
import com.gft.infrastructure.dto.user.UserLoginDTO;
import com.gft.infrastructure.dto.user.UserRequestDTO;
import com.gft.infrastructure.dto.user.UserResponseDTO;
import com.gft.infrastructure.repositories.CityRepository;
import com.gft.infrastructure.repositories.DocumentTypeRepository;
import com.gft.infrastructure.repositories.RoleRepository;
import com.gft.infrastructure.repositories.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.Normalizer;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public @Service class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final UserDetailServiceImpl userDetailService;

    private final RoleRepository roleRepository;

    private final DocumentTypeRepository documentTypeRepository;

    private final CityRepository cityRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponseDTO> findAllByRoleName(String roleName, Pageable pageable) {
        Page<User> userListPage = userRepository.findAllByRoleName(roleName, pageable);
        return userListPage.map(user -> UserResponseDTO.builder()
                .documentNumber(user.getDocumentNumber())
                .documentType(user.getDocumentType().getName())
                .city(user.getCity().getName())
                .address(user.getAddress())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .profileImageLink(user.getProfileImageLink())
                .build());
    }

    @Override
    public UserResponseDTO login(UserLoginDTO userLoginDTO) {

        var user = userRepository.findUserByEmailOrPhoneNumber(userLoginDTO.user(), userLoginDTO.user())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        return UserResponseDTO.builder()
                .documentNumber(user.getDocumentNumber())
                .documentType(user.getDocumentType().getName())
                .city(user.getCity() == null ? "" : user.getCity().getName())
                .address(user.getAddress())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .profileImageLink(user.getProfileImageLink())
                .jwtToken(this.userDetailService.loginUser(userLoginDTO))
                .build();

    }

    @Override
    public UserResponseDTO loginUsingNIT(LoginUsingNITDTO loginUsingNITDTO) {

        var user = userRepository.findById(loginUsingNITDTO.nit())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var userLoginDTO = UserLoginDTO.builder()
                .user(user.getEmail())
                .password(loginUsingNITDTO.password())
                .build();


        return UserResponseDTO.builder()
                .documentNumber(user.getDocumentNumber())
                .documentType(user.getDocumentType().getName())
                .city(user.getCity().getName())
                .address(user.getAddress())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .profileImageLink(user.getProfileImageLink())
                .jwtToken(this.userDetailService.loginUser(userLoginDTO))
                .build();

    }

    /**
     * This method is used to find all users using pagination, also it is an asynchronous method
     * @param pageable pageable
     * @return CompletableFuture<Page<ClientDto>>
     */
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<Page<UserResponseDTO>> findAll(Pageable pageable) {
        Page<User> userEntity = this.userRepository.findAll(pageable);

        //logger.info("userEntity: " + userEntity);

        if (userEntity.isEmpty()) {
            //logger.error("Internal server error");
            throw new UserException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Page<UserResponseDTO> userResponseDTOPage = userEntity.map((user) -> UserResponseDTO.builder()
                .documentNumber(user.getDocumentNumber())
                .documentType(user.getDocumentType().getName())
                .city(user.getCity().getName())
                .address(user.getAddress())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .profileImageLink(user.getProfileImageLink())
                .build());

        //logger.info("userResponseDTOPage: " + userResponseDTOPage);

        return CompletableFuture.completedFuture(userResponseDTOPage);
    }


    /**
     * This method is used to find a user by his id, also it is an asynchronous method
     * @param id id
     * @return CompletableFuture<ClientDto>
     */
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<UserResponseDTO> findById(Long id) {
        User userEntity = this.userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        //logger.info("userEntity: " + userEntity);

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .documentNumber(userEntity.getDocumentNumber())
                .documentType(userEntity.getDocumentType().getName())
                .city(userEntity.getCity().getName())
                .address(userEntity.getAddress())
                .fullName(userEntity.getFullName())
                .phoneNumber(userEntity.getPhoneNumber())
                .email(userEntity.getEmail())
                .role(userEntity.getRole().getName())
                .profileImageLink(userEntity.getProfileImageLink())
                .build();

        //logger.info("userResponseDTO: " + userResponseDTO);

        return CompletableFuture.completedFuture(userResponseDTO);
    }

    /**
     * This method is used to find a user by his id, also it is an asynchronous method
     * @param phoneNumber phone number
     * @return CompletableFuture<ClientDto>
     */
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<UserResponseDTO> findUserByPhoneNumber(String phoneNumber) {

        User userEntity = this.userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        //logger.info("userEntity: " + userEntity);

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .documentNumber(userEntity.getDocumentNumber())
                .documentType(userEntity.getDocumentType().getName())
                .city(userEntity.getCity().getName())
                .address(userEntity.getAddress())
                .fullName(userEntity.getFullName())
                .phoneNumber(userEntity.getPhoneNumber())
                .email(userEntity.getEmail())
                .role(userEntity.getRole().getName())
                .profileImageLink(userEntity.getProfileImageLink())
                .build();

        //logger.info("userResponseDTO: " + userResponseDTO);

        return CompletableFuture.completedFuture(userResponseDTO);
    }

    /**
     * This method is used to find a user by his id, also it is an asynchronous method
     * @param phoneNumber phone number
     * @return CompletableFuture<ClientDto>
     */
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<UserResponseDTO> findUserByEmail(String phoneNumber) {

        User userEntity = this.userRepository.findUserByEmail(phoneNumber)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        //logger.info("userEntity: " + userEntity);

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .documentNumber(userEntity.getDocumentNumber())
                .documentType(userEntity.getDocumentType().getName())
                .city(userEntity.getCity().getName())
                .address(userEntity.getAddress())
                .fullName(userEntity.getFullName())
                .phoneNumber(userEntity.getPhoneNumber())
                .email(userEntity.getEmail())
                .role(userEntity.getRole().getName())
                .profileImageLink(userEntity.getProfileImageLink())
                .build();

        //logger.info("userResponseDTO: " + userResponseDTO);

        return CompletableFuture.completedFuture(userResponseDTO);
    }


    /**
     * This method is used to find a user by his id, also it is an asynchronous method
     * @param phoneNumber phoneNumber
     * @return CompletableFuture<ClientDto>
     */
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<HttpStatus> findUserByPhoneNumberIfExists(String phoneNumber) {

        this.userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));


        return CompletableFuture.completedFuture(HttpStatus.FOUND);
    }


    /**
     * This method is used to save a client, also it is an asynchronous method
     * @param userRequestDTO UserRequestDTO
     * @return CompletableFuture<HttpStatus>
     */

    @Async("asyncExecutor")
    @Override
    public CompletableFuture<HttpStatus> save(@NotNull(message = "User must not be null") UserRequestDTO userRequestDTO, String role, Boolean isEnabled) {

        var userRole = this.roleRepository
                .findRoleByName(role)
                .orElseThrow(() -> new RoleException("Role not found", HttpStatus.NOT_FOUND));

        var documentType = this.documentTypeRepository
                .findDocumentTypeByName(userRequestDTO.documentType())
                .orElseThrow(() -> new DocumentTypeException("Document type not found", HttpStatus.NOT_FOUND));

        String cityNormalized = normalizeText(userRequestDTO.city());

        var city = cityRepository.findCityByName(cityNormalized != null ? cityNormalized : "Ninguna")
                .orElseGet(() -> {
                    return cityRepository.save(new City(cityNormalized)); // Guarda la nueva ciudad y la retorna
                });

        User userEntity = User.builder()
                .documentNumber(userRequestDTO.documentNumber())
                .documentType(documentType)
                .city(city)
                .address(userRequestDTO.address())
                .role(userRole)
                .fullName(userRequestDTO.fullName())
                .phoneNumber(userRequestDTO.phoneNumber())
                .email(userRequestDTO.email())
                .password(new BCryptPasswordEncoder().encode(userRequestDTO.password()))
                .profileImageLink("")
                .isEnabled(isEnabled)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialsNoExpired(true)
                .build();

        this.userRepository.save(userEntity);
        return CompletableFuture.completedFuture(HttpStatus.CREATED);
    }


    /**
     * This method is used to delete a client, also it is an asynchronous method
     * @param documentNumber Identification to delete a specific user
     * @return CompletableFuture<Void>
     */
    @Async("asyncExecutor")
    @Override
    public CompletableFuture<HttpStatus> delete(Long documentNumber) {
        if (documentNumber == null) {
            throw new UserException("Document number must not be null", HttpStatus.BAD_REQUEST);
        }

        this.userRepository.deleteById(documentNumber);

        return CompletableFuture.completedFuture(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequestDTO request, Principal principal) {
        // Obtener usuario autenticado
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new UserException("La contraseña actual es incorrecta", HttpStatus.BAD_REQUEST);
        }

        // Actualizar la contraseña
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    public static String normalizeText(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Eliminar tildes y normalizar caracteres
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", ""); // Elimina marcas diacríticas

        // Convertir la primera letra en mayúscula y el resto en minúscula
        return normalized.substring(0, 1).toUpperCase() + normalized.substring(1).toLowerCase();
    }
}
