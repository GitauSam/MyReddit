package com.zenza.myreddit.repository;

import com.zenza.myreddit.model.Comment;
import com.zenza.myreddit.model.Post;
import com.zenza.myreddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}