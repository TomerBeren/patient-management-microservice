package com.pm.authservice.controller;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.LoginResponseDTO;
import com.pm.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Authentication-related REST endpoints.
 *
 * <p>This controller exposes two routes:
 * <ul>
 *   <li><code>POST /login</code> – authenticates a user and returns a JWT.</li>
 *   <li><code>GET /validate</code> – validates an incoming JWT.</li>
 * </ul>
 */
@RestController
public class AuthController {

    /** Service layer that contains the authentication/token logic. */
    private final AuthService authService;

    /** Constructor injection keeps the class immutable and test-friendly. */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticate user credentials and issue a token.
     *
     * @param loginRequestDTO the user’s credentials (username / password)
     * @return <ul>
     *   <li><code>200 OK</code> + body <code>{"token": "…"}</code> when successful</li>
     *   <li><code>401 Unauthorized</code> when credentials are wrong</li>
     * </ul>
     */
    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        // Delegate the actual authentication to the service layer
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

        // Credentials invalid → 401
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Credentials valid → 200 and return the token
        String token = tokenOptional.get();
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    /**
     * Validate an incoming JWT.
     *
     * @param authHeader HTTP <code>Authorization</code> header with the format
     *                   <code>"Bearer &lt;token&gt;"</code>
     * @return <ul>
     *   <li><code>200 OK</code> if the token is valid</li>
     *   <li><code>401 Unauthorized</code> if the header is missing, malformed,
     *       or the token fails validation</li>
     * </ul>
     */
    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {

        // Header must be present and start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Strip the prefix and validate the raw token
        boolean valid = authService.validateToken(authHeader.substring(7));

        return valid
                ? ResponseEntity.ok().build()                   // token good
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();  // token bad
    }
}
