package com.example.bookstore.security;

import com.example.bookstore.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class BookstoreUserDetails implements UserDetails {
    private final User user;

    public BookstoreUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getRole().getName().toLowerCase();
        System.out.println("Mapped role to authority: " + role);
        // Đảm bảo vai trò không có tiền tố ROLE_
        if (role.startsWith("ROLE_")) {
            role = role.substring(5); // Loại bỏ tiền tố ROLE_ nếu có
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getIsActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !user.getIsDeleted();
    }

    public User getUser() {
        return user;
    }
}