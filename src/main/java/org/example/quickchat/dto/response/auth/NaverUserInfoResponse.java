package org.example.quickchat.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NaverUserInfoResponse {
    String id;
    @JsonProperty("profile_image")
    String profileImage;
    String email;
    String name;
}
