package org.example.quickchat.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.quickchat.domain.entity.Member;
import org.example.quickchat.dto.response.auth.*;
import org.example.quickchat.dto.wrapper.KakaoUserInfoWrapper;
import org.example.quickchat.repository.MemberRepository;
import org.example.quickchat.service.GoogleAuthService;
import org.example.quickchat.service.KakaoAuthService;
import org.example.quickchat.service.NaverAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    final GoogleAuthService googleAuthService;
    final NaverAuthService naverAuthService;
    final KakaoAuthService kakaoAuthService;

    final MemberRepository memberRepository;

    @GetMapping("/google")
    public ResponseEntity<AuthResponse> googleAuth(@RequestParam String code, HttpServletRequest req) {
        GoogleTokenResponse gtr = googleAuthService.exchangeToken(code, req.getRemoteAddr());
        GoogleUserInfoResponse gur = googleAuthService.getUserInfo((gtr.getAccessToken()));

        Member member = googleAuthService.upsertGoogleMember(gur);

        Algorithm algorithm = Algorithm.HMAC256("f891c04f488d6c66cd4a5e4d9c8c0615");
        String token = JWT.create()
                .withSubject(String.valueOf(member.getId()))
                .withIssuedAt(new Date())
                .withIssuer("quickchat")
                .sign(algorithm);

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setMember(member);

        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/naver")
    public ResponseEntity<?> naverAuth(@RequestParam String code) {
        NaverTokenResponse ntr = naverAuthService.exchangeToken(code);
        NaverUserInfoResponse nui = naverAuthService.getUserInfo((ntr.getAccessToken()));

        Member member = naverAuthService.upsertNaverMember(nui);

        Algorithm algorithm = Algorithm.HMAC256("f891c04f488d6c66cd4a5e4d9c8c0615");
        String token = JWT.create()
                .withSubject(String.valueOf(member.getId()))
                .withIssuedAt(new Date())
                .withIssuer("quickchat")
                .sign(algorithm);

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setMember(member);

        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoAuth(@RequestParam String code) {
        KakaoTokenResponse ktr = kakaoAuthService.exchangeToken(code);
        KakaoUserInfoWrapper kuw = kakaoAuthService.getUserInfo((ktr.getAccessToken()));
        Member member = kakaoAuthService.upsertKakaoMember(kuw);

        Algorithm algorithm = Algorithm.HMAC256("f891c04f488d6c66cd4a5e4d9c8c0615");
        String token = JWT.create()
                .withSubject(String.valueOf(member.getId()))
                .withIssuedAt(new Date())
                .withIssuer("quickchat")
                .sign(algorithm);

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setMember(member);

        return ResponseEntity.ok().body(resp);
    }

    @GetMapping("/secret")
    public ResponseEntity<?> secret(@RequestParam Long id) {
        Member member = memberRepository.findById(id).orElseGet(null);
        Algorithm algorithm = Algorithm.HMAC256("f891c04f488d6c66cd4a5e4d9c8c0615");
        String token = JWT.create()
                .withSubject(String.valueOf(member.getId()))
                .withIssuedAt(new Date())
                .withIssuer("quickchat")
                .sign(algorithm);

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setMember(member);

        return ResponseEntity.ok().body(resp);
    }

}
