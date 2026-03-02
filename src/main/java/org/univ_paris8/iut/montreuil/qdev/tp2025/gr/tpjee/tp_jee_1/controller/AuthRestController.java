package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.LoginDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.TokenDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.security.JwtUtil;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "Login and get a JWT token")
    public ResponseEntity<TokenDTO> authenticateUser(@Valid @RequestBody LoginDTO loginRequest) {
        log.info("Tentative de login pour l'utilisateur: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.security.CustomUserDetails userDetails = (org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.security.CustomUserDetails) authentication
                .getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        TokenDTO responseDto = new TokenDTO(
                jwt,
                tokenProvider.getJwtExpirationMs(),
                userDetails.getId(),
                roles);

        log.info("Login réussi pour l'utilisateur: {}", loginRequest.getEmail());
        return ResponseEntity.ok(responseDto);
    }
}
