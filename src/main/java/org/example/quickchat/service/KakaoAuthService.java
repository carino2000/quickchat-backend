package org.example.quickchat.service;

import lombok.RequiredArgsConstructor;
import org.example.quickchat.domain.entity.Member;
import org.example.quickchat.dto.response.auth.KakaoTokenResponse;
import org.example.quickchat.dto.response.auth.KakaoUserInfoResponse;
import org.example.quickchat.dto.response.auth.NaverTokenResponse;
import org.example.quickchat.dto.response.auth.NaverUserInfoResponse;
import org.example.quickchat.dto.wrapper.KakaoUserInfoWrapper;
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
public class KakaoAuthService {
    final MemberRepository memberRepository;
    @Value("${kakaoClientId}")
    private String kakaoClientId;

    public KakaoTokenResponse exchangeToken(String code) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>(); // 앞으로 map 안되면 이걸로 해보기..? -> 그냥 만날 이걸로 보내자
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", "http://192.168.0.17.nip.io:3000/callback/kakao");
        body.add("code", code);


        return RestClient.create().post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(body)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoWrapper getUserInfo(String token) {
        return RestClient.create().get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(KakaoUserInfoWrapper.class);
    }

    public Member upsertKakaoMember(KakaoUserInfoWrapper kuw) {
        List<Member> members = memberRepository.findAll();
        Optional<Member> target = members.stream().filter(m -> m.getProvider().equals("KAKAO") && m.getProviderId().equals(String.valueOf(kuw.getId()))).findFirst();

        // target이 있으면 그냥 get 하고, 없으면 return을 get한다
        return target.orElseGet(() -> {
            return memberRepository
                    .save(new Member("KAKAO",
                            String.valueOf(kuw.getId()),
                            kuw.getProperties().getNickname(),
                            "kakao_" + kuw.getId() + "@kakaomail.com",
                            kuw.getProperties().getProfileImage()));
        });
    }
}
