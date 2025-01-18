package com.app.backend.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * PackageName : com.app.backend.domain.order.repository
 * FileName    : PaymentRepositoryImpl
 * Author      : loadingKKamo21
 * Date        : 25. 1. 17.
 * Description :
 */
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}
