package com.app.backend.global.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * PackageName : com.app.backend.global.util
 * FileName    : MailUtil
 * Author      : loadingKKamo21
 * Date        : 25. 1. 18.
 * Description :
 */
@Component
@EnableAsync
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender mailSender;
    private final TaskScheduler  taskScheduler;

    @Value("${email.from}")
    private String from;

    /**
     * 메일 전송
     *
     * @param to      - 이메일 주소
     * @param subject - 메일 제목
     * @param text    - 메일 내용
     */
    @Async("mailTaskExecutor")
    public void sendMail(final String to, final String subject, final String text) {
        SimpleMailMessage smm = createMailMessage(to);
        smm.setSubject(subject);
        smm.setText(text);

        mailSender.send(smm);
    }

    /**
     * 예약 메일 전송
     *
     * @param to            - 이메일 주소
     * @param subject       - 메일 제목
     * @param text          - 메일 내용
     * @param scheduledTime - 예약 시간
     */
    public void sendMailAtScheduledTime(final String to, final String subject, final String text,
                                        final LocalDateTime scheduledTime) {
        taskScheduler.schedule(() -> {
            SimpleMailMessage smm = createMailMessage(to);
            smm.setSubject(subject);
            smm.setText(text);

            mailSender.send(smm);
        }, Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    //==================== 내부 메서드 ====================//

    /**
     * SimpleMailMessage 생성
     *
     * @param to - 이메일 주소
     * @return SimpleMailMessage 객체
     */
    private SimpleMailMessage createMailMessage(final String to) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(from);
        smm.setTo(to);
        return smm;
    }

}
