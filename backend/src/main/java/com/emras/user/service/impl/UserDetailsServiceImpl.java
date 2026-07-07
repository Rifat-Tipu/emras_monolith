package com.emras.user.service.impl;
import com.emras.shared.constant.ErrorMessages;
import com.emras.shared.exception.ResourceNotFoundException;
import com.emras.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("UserDetailsService: user not found — email={}", email);
                    return new UsernameNotFoundException(ErrorMessages.USER_NOT_FOUND);
                });
    }
}