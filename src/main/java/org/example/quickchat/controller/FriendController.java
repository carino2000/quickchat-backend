package org.example.quickchat.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.quickchat.domain.entity.FriendLink;
import org.example.quickchat.domain.entity.Member;
import org.example.quickchat.dto.request.NewFriendRequest;
import org.example.quickchat.dto.response.friend.MemberListResponse;
import org.example.quickchat.dto.response.friend.MyFriendList;
import org.example.quickchat.repository.FriendLinkRepository;
import org.example.quickchat.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {
    final MemberRepository memberRepository;
    final FriendLinkRepository friendLinkRepository;

    // 친구 검색(이름 or 이메일)
    @GetMapping
    public ResponseEntity<?> friendSearch(@RequestParam @Parameter(description = "검색 키워드", example = "배지훈") String keyword,
                                          @RequestParam Long memberId) {
        List<Member> members = memberRepository.findAll();
        MemberListResponse resp = new MemberListResponse(members.stream().filter(m -> {
            return !m.getId().equals(memberId) && (m.getName().contains(keyword) || m.getEmail().contains(keyword));
        }).toList());

        return ResponseEntity.ok().body(resp);
    }

    // 친구 추가
    @PostMapping("/{memberId}")
    public ResponseEntity<?> addFriend(@PathVariable @Parameter(description = "요청자 아이디", example = "1234") Long memberId,
                                       @RequestBody @Valid NewFriendRequest nfr,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult);
        }
        List<FriendLink> myFriends = friendLinkRepository.findAll().stream().filter(f -> f.getSource().getId().equals(memberId)).toList();
        Optional<Member> target = memberRepository.findById(nfr.getTargetId());

        if (target.isPresent()) {
            // 신규등록 케이스
            if (!myFriends.stream().anyMatch(f -> f.getTarget().getId().equals(nfr.getTargetId()))) {
                FriendLink friendLink = new FriendLink();
                friendLink.setSource(memberRepository.findById(memberId).get());
                friendLink.setTarget(target.get());
                friendLinkRepository.save(friendLink);
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("이미 친구로 등록된 회원입니다.");
            }
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("대상 회원이 존재하지 않습니다.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("친구 등록 완료");
    }

    // 내 친구 목록 가져오기
    @GetMapping("/{memberId}")
    public ResponseEntity<?> getMyFriends(@PathVariable Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("잘못된 memberId 입니다");
        }
        List<Member> friendList = friendLinkRepository.findAll().stream()
                .filter(f -> f.getSource().equals(member.get()))
                .map(f -> f.getTarget())
                .toList();
        return ResponseEntity.ok().body(new MyFriendList(friendList));
    }

}
