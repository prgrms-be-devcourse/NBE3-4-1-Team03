package com.app.backend.global.annotation;

import com.app.backend.global.config.QuerydslConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

/**
 * PackageName : com.app.backend.global.annotation
 * FileName    : RepositoryTest
 * Author      : 강찬우
 * Date        : 25. 1. 15.
 * Description :
 */
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@DataJpaTest(showSql = false,
             includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RepositoryTest {
}
