package com.springboot.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.springboot.domain.User;
import com.springboot.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

	private final UserRepository userRepository;
	
	@Override
	public User loadUserByUsername(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException((email)));
	}

}
