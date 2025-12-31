package org.example.quickchat.dto.wrapper;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.dto.response.auth.NaverUserInfoResponse;

@Getter
@Setter
public class NaverUserInfoWrapper {
    NaverUserInfoResponse response;
}
