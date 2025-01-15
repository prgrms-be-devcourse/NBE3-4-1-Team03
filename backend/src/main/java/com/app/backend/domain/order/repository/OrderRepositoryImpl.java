package com.app.backend.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * PackageName : com.app.backend.domain.order.repository
 * FileName    : OrderRepositoryImpl
 * Author      : 강찬우
 * Date        : 25. 1. 14.
 * Description :
 */
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}
