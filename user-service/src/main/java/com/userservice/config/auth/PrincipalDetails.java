package com.userservice.config.auth;

import com.userservice.dto.UsersDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//시큐리티 로그인 시 오브젝트 = Authentication 타입 객체 안에 User 정보가 있어야 함.
// User 오브젝트 타입 -> UserDetails 타입 객체

// security Session => Authentication => UserDetails(PrincipalDetails
public class PrincipalDetails implements UserDetails, OAuth2User {

    private UsersDTO user;

    public PrincipalDetails(UsersDTO user) {
        this.user = user;
    }

    //해당 user 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    public UsersDTO getUsersDTO() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return null;
    }


}
