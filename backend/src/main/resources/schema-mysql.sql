DROP TABLE IF EXISTS `Payments` CASCADE;
DROP TABLE IF EXISTS `Order_Products` CASCADE;
DROP TABLE IF EXISTS `Orders` CASCADE;
DROP TABLE IF EXISTS `Products` CASCADE;
DROP TABLE IF EXISTS `Users` CASCADE;

CREATE TABLE `Users`
(
    `user_id`       BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '회원 고유 식별 ID / AUTO_INCREMENT 사용',
    `user_email`    VARCHAR(255) UNIQUE NOT NULL COMMENT '유니크 속성, 로그인 아이디로 사용',
    `user_password` VARCHAR(255)        NOT NULL COMMENT '암호화 인코딩 적용',
    `user_name`     VARCHAR(255)        NOT NULL COMMENT '1 < 회원 이름 길이 <= 10',
    `user_address`  VARCHAR(255)        NOT NULL COMMENT '배송 주소(상세 주소 포함)',
    `user_phone`    VARCHAR(255)        NOT NULL COMMENT '전화번호 양식 준수, "-" 없어야 함',
    `user_status`   VARCHAR(255)        NOT NULL COMMENT '[ACTIVATED, DELETED]',
    `user_role`     VARCHAR(255)        NOT NULL COMMENT '[ROLE_ADMIN, ROLE_USER]',
    `created_date`  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자 / 수정 불가',
    `modified_date` DATETIME            NOT NULL COMMENT '수정일자',
    CONSTRAINT `pk_users` PRIMARY KEY (`user_id`)
);

CREATE TABLE `Products`
(
    `product_id`          BIGINT UNSIGNED         NOT NULL AUTO_INCREMENT COMMENT '제품 고유 식별 ID / AUTO_INCREMENT 사용',
    `product_name`        VARCHAR(255) UNIQUE NOT NULL COMMENT '유니크 속성',
    `product_description` VARCHAR(255)        NOT NULL COMMENT '제품 상세 설명',
    `product_image`       VARCHAR(255)                 DEFAULT NULL COMMENT '제품 사진',
    `product_price`       DECIMAL(10, 2) UNSIGNED NOT NULL COMMENT '100원 <= 제품 가격',
    `product_stock`       INT UNSIGNED            NOT NULL DEFAULT 0 COMMENT '재고 수량, 음수 불가',
    `product_status`      BOOLEAN             NOT NULL COMMENT '제품 상태: 판매 가능(true), 판매 불가(false)',
    `created_date`        DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자 / 수정 불가',
    `modified_date`       DATETIME            NOT NULL COMMENT '수정일자',
    CONSTRAINT `pk_products` PRIMARY KEY (`product_id`)
);

CREATE TABLE `Orders`
(
    `order_id`           BIGINT UNSIGNED         NOT NULL AUTO_INCREMENT COMMENT '주문 고유 식별 ID / AUTO_INCREMENT 사용',
    `user_id`            BIGINT UNSIGNED         NOT NULL COMMENT '주문 회원 고유 식별 ID, 외래키',
    `order_number`       VARCHAR(255) UNIQUE NOT NULL COMMENT '주문번호, 유니크 속성',
    `order_total_amount` INT UNSIGNED            NOT NULl COMMENT '전체 주문 수량, 음수 불가',
    `order_total_price`  DECIMAL(10, 2) UNSIGNED NOT NULL COMMENT '전체 주문 금액, 음수 불가',
    `order_address`      VARCHAR(255)        NOT NULL COMMENT '주문 주소(상세 주소 포함)',
    `order_status`       VARCHAR(255)        NOT NULL COMMENT '주문 상태: [Ordered, Shipped, Delivered, Cancelled]',
    `created_date`       DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자 / 수정 불가',
    `modified_date`      DATETIME            NOT NULL COMMENT '수정일자',
    CONSTRAINT `pk_orders` PRIMARY KEY (`order_id`),
    CONSTRAINT `fk_orders_users` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE
);

CREATE TABLE `Order_Products`
(
    `order_product_id`          BIGINT UNSIGNED         NOT NULL AUTO_INCREMENT COMMENT '주문 상품 고유 식별 ID / AUTO_INCREMENT 사용',
    `order_id`                  BIGINT UNSIGNED         NOT NULL COMMENT '주문 고유 식별 ID, 외래키',
    `product_id`                BIGINT UNSIGNED         NOT NULL COMMENT '제품 고유 식별 ID, 외래키',
    `order_product_amount`      INT UNSIGNED            NOT NULL COMMENT '제품 수량, 음수 불가',
    `order_product_price`       DECIMAL(10, 2) UNSIGNED NOT NULL COMMENT '제품 가격, 음수 불가',
    `order_product_total_price` DECIMAL(10, 2) UNSIGNED NOT NULL COMMENT '제품 가격 총합, 음수 불가',
    `created_date`              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자 / 수정 불가',
    `modified_date`             DATETIME NOT NULL COMMENT '수정일자',
    CONSTRAINT `pk_order_products` PRIMARY KEY (`order_product_id`, `order_id`, `product_id`),
    CONSTRAINT `fk_order_products_orders` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_products_products` FOREIGN KEY (`product_id`) REFERENCES `Products` (`product_id`) ON DELETE CASCADE
);

CREATE TABLE `Payments`
(
    `payment_id`          BIGINT UNSIGNED         NOT NULL AUTO_INCREMENT COMMENT '결제 고유 식별 ID / AUTO_INCREMENT 사용',
    `order_id`            BIGINT UNSIGNED         NOT NULL COMMENT '주문 고유 식별 ID, 외래키',
    `user_id`             BIGINT UNSIGNED         NOT NULL COMMENT '회원 고유 식별 ID, 외래키',
    `payment_method`      VARCHAR(255) NOT NULL COMMENT '결제 수단: [CREDIT_CARD]',
    `payment_paid_amount` DECIMAL(10, 2) UNSIGNED NOT NULL COMMENT '결제 금액, 음수 불가',
    `payment_status`      VARCHAR(255) NOT NULL COMMENT '결제 상태: [SUCCESS, FAILED]',
    `created_date`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자 / 수정 불가',
    `modified_date`       DATETIME     NOT NULL COMMENT '수정일자',
    CONSTRAINT `pk_payments` PRIMARY KEY (`payment_id`),
    CONSTRAINT `fk_payments_orders` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_payments_users` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE
);