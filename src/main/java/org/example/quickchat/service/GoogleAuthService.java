package org.example.quickchat.service;

import lombok.RequiredArgsConstructor;
import org.example.quickchat.domain.entity.Member;
import org.example.quickchat.dto.response.auth.GoogleTokenResponse;
import org.example.quickchat.dto.response.auth.GoogleUserInfoResponse;
import org.example.quickchat.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    final MemberRepository memberRepository;
    @Value("${googleClientSecret}")
    private String googleClientSecret;
    @Value("${googleClientId}")
    private String googleClientId;

    public GoogleTokenResponse exchangeToken(String code, String clientUri) {
        Map<String, Object> body = Map.of("client_id", googleClientId,
                "client_secret", googleClientSecret,
                "grant_type", "authorization_code",
                "redirect_uri", "http://" + clientUri + ".nip.io:3000/callback/google",
                "code", code);

        return RestClient.create().post()
                .uri("https://oauth2.googleapis.com/token")
                .body(body)
                .retrieve()
                .body(GoogleTokenResponse.class);
    }

    public GoogleUserInfoResponse getUserInfo(String token) {
        return RestClient.create().get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(GoogleUserInfoResponse.class);
    }

    public Member upsertGoogleMember(GoogleUserInfoResponse gur) {
        List<Member> members = memberRepository.findAll();
        Optional<Member> target = members.stream().filter(m -> m.getProvider().equals("GOOGLE") && m.getProviderId().equals(gur.getSub())).findFirst();

        // target이 있으면 그냥 get 하고, 없으면 return을 get한다
        return target.orElseGet(() -> {
            return memberRepository
                    .save(new Member("GOOGLE", gur.getSub(), gur.getName(), gur.getEmail(), gur.getPicture()));
        });
    }
}
