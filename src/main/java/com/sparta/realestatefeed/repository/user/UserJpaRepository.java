package com.sparta.realestatefeed.repository.user;

import com.sparta.realestatefeed.entity.User;

import java.util.List;

public interface UserJpaRepository {

    List<User> getTopFollowerUser();
}
