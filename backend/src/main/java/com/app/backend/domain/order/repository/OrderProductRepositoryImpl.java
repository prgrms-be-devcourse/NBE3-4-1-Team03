package com.app.backend.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * PackageName : com.app.backend.domain.order.repository
 * FileName    : OrderProductRepositoryImpl
 * Author      : 강찬우
 * Date        : 25. 1. 15.
 * Description :
 */
@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}
