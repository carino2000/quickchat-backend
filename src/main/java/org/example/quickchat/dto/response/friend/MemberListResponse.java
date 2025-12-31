package org.example.quickchat.dto.response.friend;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.domain.entity.Member;

import java.util.List;

@Getter
@Setter
public class MemberListResponse {
    List<Member> members;

    public MemberListResponse() {}

    public MemberListResponse(List<Member> members) {
        this.members = members;
    }
}
