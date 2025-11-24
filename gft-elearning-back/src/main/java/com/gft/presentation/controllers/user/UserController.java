package com.gft.presentation.controllers.user;

import com.gft.application.user.UserService;
import com.gft.infrastructure.dto.LoginUsingNITDTO;
import com.gft.infrastructure.dto.user.ChangePasswordRequestDTO;
import com.gft.infrastructure.dto.user.UserLoginDTO;
import com.gft.infrastructure.dto.user.UserResponseDTO;
import com.gft.infrastructure.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * @package : com.gft.presentation.controllers.user
 * @name : UserController.java
 * @date : 2025-11
 * @author : Isaias Villarreal
 * @version : 1.0.0
 */

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public @RestController class UserController {

    private final UserRepository userRepository;

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<?> countUsers() {
        return  ResponseEntity.ok(this.userRepository.count());
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserLoginDTO userLoginDTO){

        return new ResponseEntity<>(this.userService.login(userLoginDTO), HttpStatus.OK);

    }

    @PreAuthorize("permitAll()")
    @PostMapping("/login/using-nit")
    public ResponseEntity<UserResponseDTO> loginUsingNIT(@RequestBody LoginUsingNITDTO loginUsingNITDTO){

        return new ResponseEntity<>(this.userService.loginUsingNIT(loginUsingNITDTO), HttpStatus.OK);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/role/document/{documentNumber}")
    public ResponseEntity<String> getUserLoggedRoleById(@PathVariable(name = "documentNumber") Long documentNumber) {

        return new ResponseEntity<>(this.userRepository.findUserLoggedRoleById(documentNumber), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/role/email/{email}")
    public ResponseEntity<String> getUserLoggedRoleByEmail(@PathVariable(name = "email") String email) {

        return new ResponseEntity<>(this.userRepository.findUserLoggedRoleByEmail(email), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAll(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(this.userService.findAll(pageable).join());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-phone/{phoneNumber}")
    public ResponseEntity<UserResponseDTO> findUserByPhoneNumber(@PathVariable(name = "phoneNumber") String phoneNumber) {
        return ResponseEntity.ok(this.userService.findUserByPhoneNumber(phoneNumber).join());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponseDTO> findUserByEmail(@PathVariable(name = "email") String email) {
        return ResponseEntity.ok(this.userService.findUserByEmail(email).join());
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/find/{phoneNumber}")
    public ResponseEntity<HttpStatus> findUserByPhoneNumberIfExists(@PathVariable(name = "phoneNumber") String phoneNumber) {

        return ResponseEntity.ok(this.userService.findUserByPhoneNumberIfExists(phoneNumber).join());

    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{documentNumber}")
    public HttpStatus delete(@PathVariable(name = "documentNumber") Long documentNumber) {
        return this.userService.delete(documentNumber).join();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/doesAlreadyExist")
    public ResponseEntity<?> doesAlreadyExist(@RequestParam Long document) {

        boolean doesExist = this.userRepository.existsByDocumentNumber(document);

        return ResponseEntity.ok(doesExist);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/doesAlreadyPhoneExist")
    public ResponseEntity<?> doesAlreadyPhoneExist(@RequestParam String phone) {
        boolean doesExist = this.userRepository.existsByPhoneNumber(phone);

        return ResponseEntity.ok(doesExist);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/doesAlreadyEmailExist")
    public ResponseEntity<?> doesAlreadyEmailExist(@RequestParam String email) {

        boolean doesExist = this.userRepository.existsByEmail(email);

        return ResponseEntity.ok(doesExist);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request, Principal principal) {

        return this.userService.changePassword(request, principal);

    }


}
