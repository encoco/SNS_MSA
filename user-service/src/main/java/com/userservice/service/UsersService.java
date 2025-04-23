package com.userservice.service;

import com.userservice.dto.SearchDTO;
import com.userservice.dto.UsersDTO;
import com.userservice.dto.UsersInfoDTO;
import com.userservice.entity.UsersEntity;
import com.userservice.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UsersDTO user) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        UsersEntity entity = UsersEntity.toEntity(user);
        // 암호화된 비밀번호와 함께 사용자 정보 저장
        UsersEntity savedEntity = usersRepository.save(entity);
    }

    public List<SearchDTO> searchUsers(String searchTerm) {
        List<UsersEntity> entity = usersRepository.findByNicknameContaining(searchTerm);
        List<SearchDTO> dto = SearchDTO.toSearchDTO(entity);

        return dto;
    }

    public SearchDTO userInfo(int userId) {
        UsersEntity entity = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        SearchDTO dto = SearchDTO.toDTO(entity);

        return dto;
    }

    @Transactional
    public UsersInfoDTO updateUserProfile(UsersDTO profile) {
        try {
            if(usersRepository.existsByNickname(profile.getNickname())){
                return null;
            }

            UsersEntity entity = usersRepository.findByNickname(profile.getOriginal());
            entity.setNickname(profile.getNickname());
            entity.setState_message(profile.getState_message());
//            if (profile.getImgpath() != null) {
//                entity.setProfile_img(bservice.uploadFile(profile.getImgpath(), "userProfile"));
//            }
            usersRepository.save(entity);
            return UsersInfoDTO.toInfoDTO(entity);

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Transactional
    public boolean updatePassword(UsersDTO dto, int userId) {
        Optional<UsersEntity> userOptional = usersRepository.findById(userId);
        return userOptional.map(user -> {
            if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(dto.getChangePassword()));
                usersRepository.save(user);
                return true;
            } else {
                return false;
            }
        }).orElseGet(() -> {
            System.out.println("사용자를 찾을 수 없음");
            return false;
        });
    }

    public void DeleteUser(int userIdFromToken) {
        usersRepository.deleteById(userIdFromToken);
    }

    public UsersInfoDTO findUserInfo(int userId) {
        UsersEntity entity = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return UsersInfoDTO.builder()
                .id(entity.getId())
                .nickname(entity.getNickname())
                .img(entity.getProfile_img())
                .build();
    }

    public Map<Integer, String> getNicknames(List<Integer> userIds) {
        return usersRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UsersEntity::getId, UsersEntity::getNickname));
    }

    public Map<Integer, String> getProfileImages(List<Integer> userIds) {
        return usersRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        UsersEntity::getId,
                        user -> user.getProfile_img() == null ? "" : user.getProfile_img()
                ));
    }

    public String getNicknameById(int userId) {
        return usersRepository.findById(userId)
                .map(UsersEntity::getNickname)
                .orElse("");
    }

    public String getProfileImgById(int userId) {
        return usersRepository.findById(userId)
                .map(UsersEntity::getProfile_img)
                .orElse("");
    }

    public List<com.common.dto.UsersInfoDTO> getUserInfoList(List<Long> userIds) {
        List<UsersEntity> users = usersRepository.findByIdIn(userIds);
        return users.stream()
                .map(user -> com.common.dto.UsersInfoDTO.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .profile_img(user.getProfile_img())
                        .build())
                .collect(Collectors.toList());
    }
}

