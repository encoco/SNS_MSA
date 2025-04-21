package com.userservice.config.auth;


import com.userservice.dto.UsersDTO;
import com.userservice.entity.UsersEntity;
import com.userservice.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersrepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //registrationId 로 어디서 로그인했는지 ex)google,kakao, naver
        //구글로그인 버튼 클릭 -> 구글 로그인 -> 로그인 완료 -> code리턴(OAUth-client 라이브러리) -> AccessToken 요청 == userRequest정보
        // -> 회원 프로필 받아야함 (loadUser 메서드) -> 구글로부터 회원 프로필을 받아줌.
        //System.out.println("userattribute  : " + super.loadUser(userRequest).getAttributes());
        //System.out.println("userclient  : " + userRequest.getClientRegistration());
        String username = "";
        UsersDTO user = new UsersDTO();
        String authPW = "oauthsecuritypasswor";
        user.setPassword(passwordEncoder.encode(authPW));
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            Map<String, Object> resp = super.loadUser(userRequest).getAttributes();
            username = userRequest.getClientRegistration().getRegistrationId() + "_" + resp.get("sub");
            user.setUsername(username);
            user.setEmail((String) resp.get("email"));
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            Map<String, Object> respId = super.loadUser(userRequest).getAttributes();
            Map<String, Object> resp = (Map<String, Object>) super.loadUser(userRequest).getAttributes().get("kakao_account");
            username = userRequest.getClientRegistration().getRegistrationId() + "_" + respId.get("id");
            user.setUsername(username);
            user.setEmail((String) resp.get("email"));
            user.setPhone((String) resp.get("phone_number"));

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            Map<String, Object> resp = (Map<String, Object>) super.loadUser(userRequest).getAttributes().get("response");
            username = userRequest.getClientRegistration().getRegistrationId() + "_" + resp.get("id");
            user.setUsername(username);
            user.setEmail((String) resp.get("email"));
            user.setPhone((String) resp.get("mobile"));
        }
        UsersEntity findMember = usersrepository.findByUsername(username);
        String nickname;
        boolean isUnique = false;
        if (findMember == null) { // 찾은 멤버가 없다면
            do {
                nickname = UUID.randomUUID().toString();
                nickname = nickname.replace("-", "").substring(0, 12);
                System.out.println("asd");
                if (!usersrepository.existsByNickname(nickname)) {
                    isUnique = true;
                }
            } while (!isUnique);

            // DTO를 엔티티로 변환
            findMember = UsersEntity.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .nickname(nickname)
                    .role(user.getRole() + "_SNS")
                    .build();
            // 새로운 사용자를 저장
            usersrepository.save(findMember);
            System.out.println("회원가입 성공");
        }
        user = UsersDTO.toDTO(findMember);
        return new PrincipalDetails(user);
    }
}
