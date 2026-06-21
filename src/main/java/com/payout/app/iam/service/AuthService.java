package com.payout.app.iam.service;

import com.payout.app.iam.dto.*;
import com.payout.app.iam.entity.Company;
import com.payout.app.iam.entity.User;
import com.payout.app.iam.entity.UserRole;
import com.payout.app.iam.repository.CompanyRepository;
import com.payout.app.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // создаём компанию
        Company company = Company.builder()
                .name(request.getCompanyName())
                .bin(request.getBin())
                .build();
        companyRepository.save(company);

        // создаём первого admin юзера
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ADMIN)
                .company(company)
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, toUserResponse(user));
    }

    @Transactional
    public AuthResponse registerContractor(RegisterContractorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CONTRACTOR)
                .selfEmployed(request.isSelfEmployed())
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, toUserResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, toUserResponse(user));
    }

    private UserResponse toUserResponse(User user) {
        CompanyResponse companyResponse = null;
        if (user.getCompany() != null) {
            companyResponse = CompanyResponse.builder()
                    .id(user.getCompany().getId())
                    .name(user.getCompany().getName())
                    .bin(user.getCompany().getBin())
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .selfEmployed(user.isSelfEmployed())
                .company(companyResponse)
                .build();
    }
}