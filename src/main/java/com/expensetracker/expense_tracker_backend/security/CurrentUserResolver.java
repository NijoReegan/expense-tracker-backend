package com.expensetracker.expense_tracker_backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.UnauthorizedException;
import com.expensetracker.expense_tracker_backend.repository.UserRepository;

@Component
public class CurrentUserResolver {

    private final UserRepository userRepository;

    public CurrentUserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Resolves the authenticated user from the SecurityContext. The user is derived
     * from the validated JWT, never from a client-supplied identifier.
     */
    public User requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new UnauthorizedException("Authentication is required.");
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Authentication is required."));
    }
}