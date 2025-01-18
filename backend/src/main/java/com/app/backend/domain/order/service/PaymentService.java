package com.app.backend.domain.order.service;

import com.app.backend.domain.order.dto.request.PaymentRequest;
import com.app.backend.domain.order.dto.response.PaymentResponse;
import com.app.backend.domain.order.entity.Order;
import com.app.backend.domain.order.entity.Payment;
import com.app.backend.domain.order.entity.PaymentMethod;
import com.app.backend.domain.order.entity.PaymentStatus;
import com.app.backend.domain.order.exception.OrderException;
import com.app.backend.domain.order.exception.PaymentException;
import com.app.backend.domain.order.repository.OrderRepository;
import com.app.backend.domain.order.repository.PaymentRepository;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.exception.UserException;
import com.app.backend.domain.user.repository.UserRepository;
import com.app.backend.global.error.exception.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageName : com.app.backend.domain.order.service
 * FileName    : PaymentService
 * Author      : 강찬우
 * Date        : 25. 1. 18.
 * Description :
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository    userRepository;
    private final OrderRepository   orderRepository;

    /**
     * 결제 저장
     *
     * @param userId         - 회원 ID
     * @param orderId        - 주문 ID
     * @param paymentRequest - 결제 정보 DTO
     * @return 결제 ID
     */
    @Transactional
    public long savePayment(final long userId, final long orderId, final PaymentRequest paymentRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        Order order =
                orderRepository.findById(orderId).orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

        if (paymentRepository.existsByPaymentUid(paymentRequest.getPaymentUid()))
            throw new PaymentException(ErrorCode.PAYMENT_UID_CONFLICT);

        Payment payment = Payment.of(order, user, paymentRequest.getPaymentUid(),
                                     PaymentMethod.valueOf(paymentRequest.getMethod()),
                                     paymentRequest.getPaidAmount());

        return paymentRepository.save(payment).getId();
    }

    /**
     * 결제 ID로 결제 정보 단건 조회
     *
     * @param paymentId - 결제 ID
     * @return 결제 정보 응답(PaymentResponse)
     */
    public PaymentResponse getPaymentById(final long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                                           .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
        return PaymentResponse.of(payment);
    }

    /**
     * 결제 UID로 결제 정보 단건 조회
     *
     * @param paymentUid - 결제 UID
     * @return 결제 정보 응답(PaymentResponse)
     */
    public PaymentResponse getPaymentByPaymentUid(final String paymentUid) {
        Payment payment = paymentRepository.findByPaymentUid(paymentUid)
                                           .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
        return PaymentResponse.of(payment);
    }

    /**
     * 주문 ID로 결제 정보 단건 조회
     *
     * @param orderId - 주문 ID
     * @return 결제 정보 응답(PaymentResponse)
     */
    public PaymentResponse getPaymentByOrderId(final long orderId) {
        Payment payment = paymentRepository.findByOrder_Id(orderId)
                                           .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
        return PaymentResponse.of(payment);
    }

    /**
     * 결제 ID, 주문 ID로 결제 정보 단건 조회
     *
     * @param id      - 결제 ID
     * @param orderId - 주문 ID
     * @return 결제 정보 응답(PaymentResponse)
     */
    public PaymentResponse getPaymentByIdAndOrderId(final long id, final long orderId) {
        Payment payment = paymentRepository.findById(id)
                                           .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getOrder().getId().equals(orderId))
            return PaymentResponse.of(payment);

        throw new PaymentException(ErrorCode.PAYMENT_ORDER_MISMATCH);
    }

    /**
     * 회원 ID로 결제 정보 다건 조회
     *
     * @param userId - 회원 ID
     * @return 결제 정보 응답(PaymentResponse) 목록
     */
    public List<PaymentResponse> getPaymentsByUserId(final long userId) {
        if (userRepository.findById(userId).isEmpty())
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        List<Payment> payments = paymentRepository.findByCustomer_Id(userId);
        return payments.stream().map(PaymentResponse::of).toList();
    }

    /**
     * 회원 ID, 결제 상태로 결제 정보 다건 조회
     *
     * @param userId        - 회원 ID
     * @param paymentStatus - 결제 상태 문자열: SUCCESS, FAILED
     * @return 결제 정보 응답(PaymentResponse) 목록
     */
    public List<PaymentResponse> getPaymentsByUserIdAndPaymentStatus(final long userId, final String paymentStatus) {
        if (userRepository.findById(userId).isEmpty())
            throw new UserException(ErrorCode.USER_NOT_FOUND);

        validPaymentStatus(paymentStatus);

        List<Payment> payments = paymentRepository.findByCustomer_IdAndStatus(userId,
                                                                              PaymentStatus.valueOf(paymentStatus));
        return payments.stream().map(PaymentResponse::of).toList();
    }

    /**
     * 모든 결제 정보 조회(List)
     *
     * @return 결제 정보 응답(PaymentResponse) 목록
     */
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(PaymentResponse::of).toList();
    }

    /**
     * 모든 결제 정보 조회(Page)
     *
     * @param pageable - 페이징 객체
     * @return 결제 정보 응답(PaymentResponse) 페이징 객체
     */
    public Page<PaymentResponse> getAllPayments(final Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentResponse::of);
    }

    /**
     * 결제 UID 검증
     *
     * @param paymentUid - 결제 UID
     * @return 결제 UID에 해당하는 결제 엔티티 존재 여부
     */
    public boolean existsByPaymentUid(final String paymentUid) {
        return paymentRepository.existsByPaymentUid(paymentUid);
    }

    /**
     * 결제 상태 변경
     *
     * @param paymentId     - 결제 ID
     * @param paymentStatus - 변경할 결제 상태 문자열: SUCCESS, FAILED
     */
    @Transactional
    public void updatePaymentStatus(final long paymentId, final String paymentStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                                           .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        validPaymentStatus(paymentStatus);

        payment.updatePaymentStatus(PaymentStatus.valueOf(paymentStatus));
    }

    /**
     * 결제 상태 변경
     *
     * @param paymentId     - 결제 ID
     * @param userId        - 회원 ID
     * @param paymentStatus - 변경할 결제 상태 문자열: SUCCESS, FAILED
     */
    @Transactional
    public void updatePaymentStatusByUserId(final long paymentId, final long userId, final String paymentStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                                           .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getCustomer().getId().equals(userId)) {
            validPaymentStatus(paymentStatus);

            payment.updatePaymentStatus(PaymentStatus.valueOf(paymentStatus));

            return;
        }

        throw new PaymentException(ErrorCode.PAYMENT_BUYER_MISMATCH);
    }

    /**
     * 결제 삭제
     *
     * @param paymentId - 결제 ID
     */
    @Transactional
    public void deletePaymentById(final long paymentId) {
        if (paymentRepository.existsById(paymentId)) {
            paymentRepository.deleteById(paymentId);
            return;
        }
        throw new PaymentException(ErrorCode.PAYMENT_NOT_FOUND);
    }

    /**
     * 결제 삭제
     *
     * @param paymentUid - 결제 UID
     */
    @Transactional
    public void deletePaymentByPaymentUid(final String paymentUid) {
        if (paymentRepository.existsByPaymentUid(paymentUid)) {
            paymentRepository.deleteByPaymentUid(paymentUid);
            return;
        }
        throw new PaymentException(ErrorCode.PAYMENT_NOT_FOUND);
    }

    /**
     * 결제 UID 생성
     *
     * @return 생성된 결제 UID
     */
    public String getNewPaymentUid() {
        String paymentUid;
        do {
            paymentUid = UUID.randomUUID().toString().replace("-", "");
        } while (paymentRepository.existsByPaymentUid(paymentUid));
        return paymentUid;
    }

    //==================== 내부 메서드 ====================//

    /**
     * 결제 상태 문자열 검증
     *
     * @param paymentStatus - 결제 상태 문자열
     * @return 결제 상태 enum 존재 여부
     */
    private boolean validPaymentStatus(final String paymentStatus) {
        if (Arrays.stream(PaymentStatus.values()).anyMatch(ps -> ps.name().equals(paymentStatus)))
            return true;
        throw new PaymentException(ErrorCode.INVALID_PAYMENT_STATUS);
    }

}
