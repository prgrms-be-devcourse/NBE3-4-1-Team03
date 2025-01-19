package com.app.backend.domain.order.util;

import com.app.backend.domain.order.dto.response.OrderProductResponse;
import com.app.backend.global.constant.MailMessageConstant;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;

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

    /**
     * 주문 완료 메일 본문 생성
     *
     * @param mailInfo - 메일로 전송할 정보 DTO
     * @return 주문 완료 메일 본문
     */
    public static String getOrderCompleteMailText(final MailInfo mailInfo) {
        return """
               주문이 완료되었습니다.
                                   
               주문번호: %s                    
               배송상태: %s                    
                                   
               회원 이름: %s
               배송 주소: %s
               주문 품목:
               %s
               """.formatted(mailInfo.orderNumber,
                             mailInfo.isShipped ? MailMessageConstant.MAIL_MESSAGE_ORDER_SHIPPED
                                                : MailMessageConstant.MAIL_MESSAGE_ORDER_DELIVERY_PENDING,
                             mailInfo.name,
                             mailInfo.address,
                             createOrderProductsString(mailInfo.orderProducts));
    }

    /**
     * 주문 배송 업데이트 본문 생성
     *
     * @param orderNumber - 주문 번호
     * @return 주문 배송 업데이트 본문
     */
    public static String getDeliveryStatusUpdateMailText(final String orderNumber) {
        return """
               배송이 시작되었습니다.
               주문번호: %s
               """.formatted(orderNumber);
    }

    /**
     * 주문 취소 메일 본문 생성
     *
     * @param orderNumber - 주문 번호
     * @return 주문 취소 메일 본문
     */
    public static String getOrderCancelMailText(final String orderNumber) {
        return """
               주문이 정상적으로 취소되었습니다.
               주문번호: %s
               """.formatted(orderNumber);
    }

    //==================== 내부 메서드 ====================//

    /**
     * 주문 제품 정보 문자열 생성
     *
     * @param responses - 주문 제품 정보 목록
     * @return 주문 제품 정보 문자열
     */
    private static String createOrderProductsString(final List<OrderProductResponse> responses) {
        String template = """
                           - 제품명: %s
                             - 제품가격: %s 원
                             - 구매수량: %s 개
                             - 합계가격: %s 원
                          """;

        StringBuilder sb = new StringBuilder();
        for (OrderProductResponse product : responses)
            sb.append(template.formatted(product.getName(),
                                         product.getPrice(),
                                         product.getAmount(),
                                         product.getTotalPrice()));

        return sb.toString();
    }

    @Builder
    @AllArgsConstructor
    public static class MailInfo {
        private boolean                    isShipped;
        private String                     name;
        private String                     address;
        private String                     orderNumber;
        private List<OrderProductResponse> orderProducts;
    }

}
