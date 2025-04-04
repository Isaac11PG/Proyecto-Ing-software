package com.holamundo.HOLASPRING6CV3.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.holamundo.HOLASPRING6CV3.models.UserModel;
import com.holamundo.HOLASPRING6CV3.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;
import java.util.List;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserModel userModel = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    // Aquí ya no necesitas mapear las autoridades manualmente,
    // porque tu CustomUserDetails lo hace solito en getAuthorities()
    return new CustomUserDetails(userModel);
}

}
