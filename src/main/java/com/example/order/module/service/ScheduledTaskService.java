package com.example.order.module.service;

import com.example.order.module.model.metadata.ScheduleMetadata;
import com.example.order.module.model.response.EmployeeEntityResponse;
import com.example.order.module.model.response.EmployeeListResponse;
import com.example.order.module.model.response.PersonalOfferResponse;
import com.example.order.module.rest.RestConsumer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskService {
    private final JavaMailSender javaMailSender;
    private final ScheduleMetadata scheduleMetadata;
    private final OrderService orderService;
    private final RestConsumer restConsumer;
    private final TemplateEngine templateEngine;

    @Scheduled(cron = "0 0 19 * * ?")
    public void sendPersonalOffer() throws MessagingException {
        EmployeeListResponse allEmployees = restConsumer.getAllEmployees();
        List<EmployeeEntityResponse> employeeResponseList = allEmployees.getEmployeeResponseList();

        for (EmployeeEntityResponse employeeEntityResponse : employeeResponseList) {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

            PersonalOfferResponse personalOffer = orderService.getPersonalOffer(employeeEntityResponse.getId());
            Context context = new Context();
            context.setVariable("personalOffer", personalOffer);
            String emailContent = templateEngine.process("emailPersonalOffer", context);

            messageHelper.setFrom(scheduleMetadata.getUsername() + "@mail.ru");
            messageHelper.setTo(employeeEntityResponse.getEmail());
            messageHelper.setSubject(scheduleMetadata.getSubject());
            messageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);

        }

        log.info("Email was sent");
    }
}
