package com.gft.application.user;

import com.gft.infrastructure.dto.user.UserLoginDTO;
import com.gft.infrastructure.repositories.UserRepository;
import com.gft.infrastructure.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public @Service class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        var userEntity = userRepository.findUserByEmailOrPhoneNumber(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info("User found: {}", userEntity.getFullName());

        List<SimpleGrantedAuthority> grantedAuthorityList = new ArrayList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_".concat(userEntity.getRole().getName())));

        return new User(
                userEntity.getEmail(), // Usa el email como username
                userEntity.getPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialsNoExpired(),
                userEntity.isAccountNoLocked(),
                grantedAuthorityList
        );
    }

    public String loginUser(UserLoginDTO userLoginDTO) {
        String user = userLoginDTO.user();
        String password = userLoginDTO.password();

        Authentication authentication = this.authenticate(user, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtils.createToken(authentication);
    }

    private Authentication authenticate(String identifier, String password) {
        UserDetails userDetails = this.loadUserByUsername(identifier);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect Password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), password, userDetails.getAuthorities());
    }

}

