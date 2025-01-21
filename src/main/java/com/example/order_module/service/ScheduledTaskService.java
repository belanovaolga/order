package com.example.order_module.service;

import com.example.order_module.model.response.PersonalOfferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskService {
    private final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    private final MailSender mailSender;
    private final OrderService orderService;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Scheduled(fixedRateString = "${spring.scheduling}")
    public void sendPersonalOffer() {
        PersonalOfferResponse personalOffer = orderService.getPersonalOffer(1L);

        simpleMailMessage.setFrom(fromEmail + "@mail.ru");
        simpleMailMessage.setTo("olga.bel.a.nova@mail.ru");
        simpleMailMessage.setSubject("Personal offer");
        simpleMailMessage.setText("Hello! It's personal offer for you: /n" +
                personalOffer.toString());

        mailSender.send(simpleMailMessage);

        log.info("Email was sent");
    }
}
