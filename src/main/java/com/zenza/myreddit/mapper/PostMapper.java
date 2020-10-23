package com.zenza.myreddit.mapper;

import com.zenza.myreddit.dto.PostRequest;
import com.zenza.myreddit.dto.PostResponse;
import com.zenza.myreddit.model.Post;
import com.zenza.myreddit.model.Subreddit;
import com.zenza.myreddit.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    PostResponse mapToDto(Post post);
}
