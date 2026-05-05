package com.osrsflip.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final NimbusJwtEncoder encoder;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    public JwtService(NimbusJwtEncoder jwtEncoder) {
        this.encoder = jwtEncoder;
    }

    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("osrs-ge-flipper")
                .issuedAt(now)
                .expiresAt(now.plusMillis(expirationMs))
                .subject(username)
                .claim("role", role)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
