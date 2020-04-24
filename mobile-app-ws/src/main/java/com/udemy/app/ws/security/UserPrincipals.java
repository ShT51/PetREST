package com.udemy.app.ws.security;

import com.udemy.app.ws.io.entity.AuthorityEntity;
import com.udemy.app.ws.io.entity.RoleEntity;
import com.udemy.app.ws.io.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class UserPrincipals implements UserDetails {
    private static final long serialVersionUID = 4048655481609051186L;

    private UserEntity userEntity;
    private String userId;

    public UserPrincipals(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.userId = userEntity.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();
        Collection<AuthorityEntity> authorityEntities = new HashSet<>();

        // Get User Roles
        Collection<RoleEntity> roles = userEntity.getRoles();
        if (roles == null) return  grantedAuthorities;

        // Add all User Authorities and Roles
        roles.forEach((role -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            authorityEntities.addAll(role.getAuthorities());
        }));

        authorityEntities.forEach((authorityEntity -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
        }));

        return grantedAuthorities;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getPassword() {
        return userEntity.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
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
        return true;
    }

    @Override
    public boolean isEnabled() {
        // to enable email verification function
        return userEntity.getEmailVerificationStatus();

        // to disable email verification function
        //return true;
    }
}
