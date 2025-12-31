package org.example.quickchat.dto.wrapper;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.dto.response.auth.KakaoUserInfoResponse;

@Getter
@Setter
public class KakaoUserInfoWrapper {
    Long id;
    KakaoUserInfoResponse properties;
}
