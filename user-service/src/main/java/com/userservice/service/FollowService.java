package com.userservice.service;

import com.userservice.entity.FollowEntity;
import com.userservice.kafka.AlarmProducer;
import com.userservice.repository.FollowRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final AlarmProducer alarmProducer;
    @Transactional
    public void follow(int followerId, int followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            followRepository.save(FollowEntity.builder()
                    .followerId(followerId)
                    .followingId(followingId)
                    .build());

            alarmProducer.sendFollowAlarm(followerId, followingId);
        }
    }

    public void unfollow(int followerId, int followingId) {
        followRepository.findAll().stream()
                .filter(f -> f.getFollowerId() == followerId && f.getFollowingId() == followingId)
                .findFirst()
                .ifPresent(followRepository::delete);
    }

    public boolean exists(int followerId, int followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public List<Integer> getFollowingIds(int userId) {
        return followRepository.findByFollowerId(userId).stream()
                .map(FollowEntity::getFollowingId)
                .toList();
    }

    public int countFollowers(int userId) {
        return followRepository.countByFollowerId(userId);
    }

    public int countFollowing(int userId) {
        return followRepository.countByFollowingId(userId);
    }
}
