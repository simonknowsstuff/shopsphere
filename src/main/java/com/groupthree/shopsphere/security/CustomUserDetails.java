package com.groupthree.shopsphere.security;

import com.groupthree.shopsphere.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
	private final User user;

	public CustomUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (user == null || user.getRole() == null) {
			return Collections.emptyList();
		}
		return user.getRole().stream()
			.map(role -> new SimpleGrantedAuthority(role))
			.toList();
	}

	@Override
	public String getPassword() {
		return user != null ? user.getPassword() : null;
	}

	@Override
	public String getUsername() {
		return user != null ? user.getEmail() : null;
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
		return true;
	}
}
