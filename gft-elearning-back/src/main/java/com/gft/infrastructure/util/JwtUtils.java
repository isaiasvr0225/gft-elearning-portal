package com.gft.infrastructure.util;

import com.gft.infrastructure.repositories.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public @Component class JwtUtils {

    private final UserRepository userRepository;

    //@Value("${jwt.private_key}")
    private final String jwtPK = "8fbf9d19cd0c600ddb785dcc87e731dbd69b5f909620d21443638a8bf39f52c9";

    //@Value("${jwt.user.generator_name}")
    private final String jwtUserGenName = "carryDevGen";

    public String createToken(Authentication authentication) {
        long nowMillis = System.currentTimeMillis();
        Date expiryDate = new Date(nowMillis + 24 * 60 * 60 * 1000); // 24 horas

        Algorithm algorithm = Algorithm.HMAC256(this.jwtPK);

        // Obtener el identificador (email o teléfono) desde Authentication
        String identifier = authentication.getPrincipal().toString();

        // Buscar en la base de datos
        var userEntity = userRepository.findUserByEmailOrPhoneNumber(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Obtener roles
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.jwtUserGenName)
                .withSubject(userEntity.getEmail() != null ? userEntity.getEmail() : userEntity.getPhoneNumber()) // Usa email si está disponible
                .withClaim("authorities", authorities)
                .withIssuedAt(Instant.now())
                .withExpiresAt(expiryDate)
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }



    public DecodedJWT verifyToken(String token) {

        try {

            Algorithm algorithm = Algorithm.HMAC256(this.jwtPK);

            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(this.jwtUserGenName)
                    .build();

            return jwtVerifier.verify(token);

        }catch (JWTVerificationException exception){

            throw new JWTVerificationException("Invalid token, cannot continue.");

        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }


}
