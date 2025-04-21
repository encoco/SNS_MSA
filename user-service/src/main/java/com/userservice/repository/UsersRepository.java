package com.userservice.repository;

import com.userservice.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepository extends JpaRepository<UsersEntity, Integer> {
    public boolean existsByUsername(String username);

    public boolean existsByNickname(String nickname);

    public UsersEntity findByUsername(String username);

    public UsersEntity findByNickname(String nickname);

    public List<UsersEntity> findByNicknameContaining(String searchTerm);

    @Query(value = "SELECT nickname FROM users WHERE id = :userId", nativeQuery = true)
    public String findNicknameById(@Param("userId") Integer userId);

    List<UsersEntity> findByIdIn(List<Long> userIds);
}


