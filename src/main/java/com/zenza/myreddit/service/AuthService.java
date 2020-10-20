package com.zenza.myreddit.service;

import com.zenza.myreddit.dto.AuthenticationResponse;
import com.zenza.myreddit.dto.LoginRequest;
import com.zenza.myreddit.dto.RegisterRequest;
import com.zenza.myreddit.exceptions.SpringRedditException;
import com.zenza.myreddit.model.NotificationEmail;
import com.zenza.myreddit.model.User;
import com.zenza.myreddit.model.VerificationToken;
import com.zenza.myreddit.repository.UserRepository;
import com.zenza.myreddit.repository.VerificationTokenRepository;
import com.zenza.myreddit.security.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signUp(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
//        mailService.sendEmail(new NotificationEmail(
//                "Please Activate your Account.",
//                user.getEmail(),
//                "Thank you for signing up to myreddit, " +
//                        "please click on the below url to activate your account:\n" +
//                "http://localhost:15000/api/auth/accountVerification/" + token
//        ));
        log.info("Url to be sent: http://localhost:15000/api/auth/accountVerification/" + token);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() ->
                new SpringRedditException("Invalid Token")
        );
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                    new SpringRedditException("User not found with name - " + username)
                );

        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse signIn(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse
                .builder()
                .authenticationToken(token)
//                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
//                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }
}
