package org.example.quickchat.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleUserInfoResponse {
    String sub;
    String name;
    @JsonProperty("given_name")
    String givenName;
    @JsonProperty("family_name")
    String familyName;
    String picture;
    String email;
    @JsonProperty("email_verified")
    boolean emailVerified;
}
