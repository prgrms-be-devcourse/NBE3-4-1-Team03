package com.app.backend.global.annotation;

import com.app.backend.global.config.CustomWithSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomWithSecurityContextFactory.class)
public @interface CustomWithMockAdmin {

    long id() default 1L;

    String username() default "user@mail.com";

    String password() default "password";

    String name() default "user";

    String role() default "ROLE_ADMIN";

}