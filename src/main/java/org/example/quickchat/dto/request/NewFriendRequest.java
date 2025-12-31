package org.example.quickchat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewFriendRequest {
    @Positive
    Long targetId;
}
