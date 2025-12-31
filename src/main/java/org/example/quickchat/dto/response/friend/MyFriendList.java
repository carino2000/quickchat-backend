package org.example.quickchat.dto.response.friend;

import lombok.Getter;
import lombok.Setter;
import org.example.quickchat.domain.entity.Member;

import java.util.List;

@Getter
@Setter
public class MyFriendList {
    List<Member> myFriends;

    public MyFriendList() {}

    public MyFriendList(List<Member> myFriends) {
        this.myFriends = myFriends;
    }
}
