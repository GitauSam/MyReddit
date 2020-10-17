package com.zenza.myreddit.repository;

import com.zenza.myreddit.model.Post;
import com.zenza.myreddit.model.User;
import com.zenza.myreddit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}