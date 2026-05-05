package com.osrsflip.service;

import com.osrsflip.model.dto.AuthResponse;
import com.osrsflip.model.dto.LoginRequest;
import com.osrsflip.model.dto.RegisterRequest;
import com.osrsflip.model.entity.User;
import com.osrsflip.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    // --- register ---

    @Test
    void register_success_returnsTokenAndUsername() {
        when(userRepo.existsByUsername("evan")).thenReturn(false);
        when(userRepo.existsByEmail("evan@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken("evan", "USER")).thenReturn("jwt.token");

        AuthResponse res = userService.register(
                new RegisterRequest("evan", "evan@test.com", "password123"));

        assertThat(res.token()).isEqualTo("jwt.token");
        assertThat(res.username()).isEqualTo("evan");
        assertThat(res.role()).isEqualTo("USER");
    }

    @Test
    void register_usernameTaken_throwsConflict() {
        when(userRepo.existsByUsername("evan")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(
                new RegisterRequest("evan", "evan@test.com", "password123")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void register_emailTaken_throwsConflict() {
        when(userRepo.existsByUsername("evan")).thenReturn(false);
        when(userRepo.existsByEmail("evan@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(
                new RegisterRequest("evan", "evan@test.com", "password123")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void register_passwordIsHashed_notStoredAsPlaintext() {
        when(userRepo.existsByUsername(any())).thenReturn(false);
        when(userRepo.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$hashed");
        when(jwtService.generateToken(any(), any())).thenReturn("token");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepo.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        userService.register(new RegisterRequest("evan", "evan@test.com", "password123"));

        assertThat(captor.getValue().getPassword())
                .isEqualTo("$2a$hashed")
                .isNotEqualTo("password123");
    }

    // --- login ---

    @Test
    void login_success_returnsTokenAndUsername() {
        User user = buildUser("evan", "hashed", "USER");
        when(userRepo.findByUsername("evan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken("evan", "USER")).thenReturn("jwt.token");

        AuthResponse res = userService.login(new LoginRequest("evan", "password123"));

        assertThat(res.token()).isEqualTo("jwt.token");
        assertThat(res.username()).isEqualTo("evan");
    }

    @Test
    void login_unknownUser_throwsUnauthorized() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(new LoginRequest("ghost", "password123")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        User user = buildUser("evan", "hashed", "USER");
        when(userRepo.findByUsername("evan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(new LoginRequest("evan", "wrongpassword")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void login_sameErrorMessageForBothFailureModes_preventsUsernameEnumeration() {
        User user = buildUser("evan", "hashed", "USER");
        when(userRepo.findByUsername("evan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        ResponseStatusException wrongPassword = catchThrowableOfType(
                () -> userService.login(new LoginRequest("evan", "wrong")),
                ResponseStatusException.class);

        ResponseStatusException unknownUser = catchThrowableOfType(
                () -> userService.login(new LoginRequest("ghost", "anything")),
                ResponseStatusException.class);

        assertThat(wrongPassword.getReason())
                .isEqualTo(unknownUser.getReason());
    }

    // --- helpers ---

    private User buildUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}
