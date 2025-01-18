package com.app.backend.domain.order.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * PackageName : com.app.backend.domain.order.util
 * FileName    : OrderUtil
 * Author      : loadingKKamo21
 * Date        : 25. 1. 15.
 * Description :
 */
public class OrderUtil {

    private static final String            CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int               LENGTH     = 10;
    private static final Random            RANDOM     = new SecureRandom();
    private static final DateTimeFormatter FORMATTER  = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 주문 번호 문자열 생성
     * 메서드 호출 시점과 랜덤 문자열을 사용하여 주문 번호 생성
     *
     * @return 주문 번호
     */
    public static String generateOrderNumber() {
        String date         = LocalDateTime.now().format(FORMATTER);
        String randomString = generateRandomString(LENGTH);
        return date + randomString;
    }

    /**
     * 랜덤 문자열 생성
     * 대문자, 숫자만 사용하여 랜덤한 문자열 생성
     *
     * @param length - 생성할 문자열 길이
     * @return 생성된 랜덤 문자열
     */
    private static String generateRandomString(final int length) {
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(idx));
        }
        return randomString.toString();
    }

}
