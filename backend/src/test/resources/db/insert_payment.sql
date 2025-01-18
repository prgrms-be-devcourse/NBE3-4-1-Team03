INSERT INTO `Payments` (`payment_id`, `order_id`, `user_id`, `payment_uid`, `payment_method`, `payment_paid_amount`,
                        `payment_status`, `created_date`, `modified_date`)
VALUES (1, 1, 1, 'paymentuid_1', 'CREDIT_CARD', 10000.00, 'SUCCESS', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (2, 2, 2, 'paymentuid_2', 'CREDIT_CARD', 10000.00, 'SUCCESS', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (3, 3, 3, 'paymentuid_3', 'CREDIT_CARD', 10000.00, 'SUCCESS', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (4, 4, 4, 'paymentuid_4', 'CREDIT_CARD', 10000.00, 'SUCCESS', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (5, 5, 5, 'paymentuid_5', 'CREDIT_CARD', 10000.00, 'SUCCESS', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (6, 6, 1, 'paymentuid_6', 'CREDIT_CARD', 10000.00, 'SUCCESS', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (7, 7, 2, 'paymentuid_7', 'CREDIT_CARD', 10000.00, 'FAILED', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (8, 8, 3, 'paymentuid_8', 'CREDIT_CARD', 10000.00, 'FAILED', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (9, 9, 4, 'paymentuid_9', 'CREDIT_CARD', 10000.00, 'FAILED', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
       (10, 10, 5, 'paymentuid_10', 'CREDIT_CARD', 10000.00, 'FAILED', '2025-01-01 00:00:00', '2025-01-01 00:00:00');