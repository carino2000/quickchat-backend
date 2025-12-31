package org.example.quickchat.repository;

import org.example.quickchat.domain.entity.FriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendLinkRepository extends JpaRepository<FriendLink,Long> {
}
