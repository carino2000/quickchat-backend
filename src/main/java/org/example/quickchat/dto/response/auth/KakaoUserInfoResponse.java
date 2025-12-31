package org.example.quickchat.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfoResponse {
    String nickname;
    @JsonProperty("thumbnail_image")
    String profileImage;
}
