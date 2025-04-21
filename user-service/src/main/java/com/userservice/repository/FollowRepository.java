package com.userservice.repository;

import com.userservice.entity.FollowEntity;
import com.userservice.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<FollowEntity, Integer> {
    boolean existsByFollowerIdAndFollowingId(int followerId, int followingId);
    List<FollowEntity> findByFollowerId(int followerId);
    FollowEntity findByFollowerIdAndFollowingId(int myId, int userId);

    int countByFollowerId(int followerId);

    int countByFollowingId(int userId);
}

