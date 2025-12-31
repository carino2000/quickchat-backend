package org.example.quickchat.service;

import lombok.RequiredArgsConstructor;
import org.example.quickchat.domain.entity.Member;
import org.example.quickchat.dto.response.auth.NaverTokenResponse;
import org.example.quickchat.dto.response.auth.NaverUserInfoResponse;
import org.example.quickchat.dto.wrapper.NaverUserInfoWrapper;
import org.example.quickchat.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverAuthService {
    final MemberRepository memberRepository;
    @Value("${naverClientSecret}")
    private String naverClientSecret;
    @Value("${naverClientId}")
    private String naverClientId;

    public NaverTokenResponse exchangeToken(String code) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>(); // 앞으로 map 안되면 이걸로 해보기..? -> 그냥 만날 이걸로 보내자
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("client_id", naverClientId);
        body.add("state", "quickchat");
        body.add("client_secret", naverClientSecret);


        return RestClient.create().post()
                .uri("https://nid.naver.com/oauth2.0/token")
                .body(body)
                .retrieve()
                .body(NaverTokenResponse.class);
    }

    public NaverUserInfoResponse getUserInfo(String token) {
        return RestClient.create().get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(NaverUserInfoWrapper.class).getResponse();
    }

    public Member upsertNaverMember(NaverUserInfoResponse nui) {
        List<Member> members = memberRepository.findAll();
        Optional<Member> target = members.stream().filter(m -> m.getProvider().equals("NAVER") && m.getProviderId().equals(nui.getId())).findFirst();

        // target이 있으면 그냥 get 하고, 없으면 return을 get한다
        return target.orElseGet(() -> {
            return memberRepository
                    .save(new Member("NAVER", nui.getId(), nui.getName(), nui.getEmail(), nui.getProfileImage()));
        });
    }
}
