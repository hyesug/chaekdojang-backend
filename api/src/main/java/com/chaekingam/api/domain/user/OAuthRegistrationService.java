package com.chaekingam.api.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthRegistrationService {

    private final UserRepository userRepository;

    public User getOrRegister(AuthProvider provider, Map<String, Object> attributes) {
        OAuthUserInfo info = OAuthUserInfo.of(provider, attributes);

        return userRepository.findByProviderIdAndProvider(info.providerId(), provider)
                .orElseGet(() -> registerNewUser(info));
    }

    private User registerNewUser(OAuthUserInfo info) {
        if (info.email() != null) {
            Optional<User> existing = userRepository.findByEmail(info.email());
            if (existing.isPresent()) return existing.get();
        }

        String nickname = makeUniqueNickname(info.nickname());
        String email = info.email() != null
                ? info.email()
                : info.providerId() + "@" + info.provider().name().toLowerCase() + ".social";

        return userRepository.save(
                User.createOAuth(email, info.providerId(), nickname, info.profileImage(), info.provider())
        );
    }

    private String makeUniqueNickname(String base) {
        String candidate = (base != null && !base.isBlank()) ? base : "독자";
        if (!userRepository.existsByNickname(candidate)) return candidate;
        for (int i = 0; i < 5; i++) {
            String s = candidate + "_" + UUID.randomUUID().toString().substring(0, 4);
            if (!userRepository.existsByNickname(s)) return s;
        }
        return candidate + "_" + System.currentTimeMillis();
    }
}
