package org.example.quickchat.dto.response.auth;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.domain.entity.Member;

@Getter
@Setter
public class AuthResponse {
    String token;
    Member member;
}
