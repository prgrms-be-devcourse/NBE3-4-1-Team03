package com.app.backend.global.config;

import com.app.backend.domain.user.entity.User;
import com.app.backend.global.annotation.CustomWithMockAdmin;
import com.app.backend.global.security.user.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class CustomWithSecurityContextFactory implements WithSecurityContextFactory<CustomWithMockAdmin> {

    @Override
    public SecurityContext createSecurityContext(CustomWithMockAdmin annotation) {
        CustomUserDetails userDetails = new CustomUserDetails(User.builder()
                .id(annotation.id())
                .email(annotation.username())
                .password(annotation.password())
                .name(annotation.name())
                .role(annotation.role())
                .build());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        return securityContext;
    }

}