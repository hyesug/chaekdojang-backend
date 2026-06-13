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
    private final UserAuthProviderRepository authProviderRepository;

    public record RegistrationResult(User user, boolean isNew) {}

    public RegistrationResult getOrRegister(AuthProvider provider, Map<String, Object> attributes) {
        OAuthUserInfo info = OAuthUserInfo.of(provider, attributes);

        // 1. 이미 이 소셜 로그인 수단으로 가입된 계정이 있는지 확인
        Optional<UserAuthProvider> existingAuth =
                authProviderRepository.findByProviderAndProviderUserId(provider, info.providerId());
        if (existingAuth.isPresent()) {
            return new RegistrationResult(existingAuth.get().getUser(), false);
        }

        // 2. 같은 이메일로 다른 소셜 로그인을 한 계정이 있는지 확인 → 있으면 연결
        User user;
        boolean isNew = false;
        if (info.email() != null) {
            Optional<User> existingByEmail = userRepository.findByEmail(info.email());
            if (existingByEmail.isPresent()) {
                user = existingByEmail.get();
            } else {
                user = createNewUser(info);
                isNew = true;
            }
        } else {
            user = createNewUser(info);
            isNew = true;
        }

        // 3. 소셜 로그인 수단 연결
        authProviderRepository.save(
                UserAuthProvider.of(user, provider, info.providerId(), info.email(), info.profileImage())
        );

        return new RegistrationResult(user, isNew);
    }

    private User createNewUser(OAuthUserInfo info) {
        String nickname = makeUniqueNickname(info.nickname());
        return userRepository.save(
                User.create(info.email(), nickname, info.profileImage())
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
