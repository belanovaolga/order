package com.example.order.module.service;

import com.example.order.module.exception.EmailException;
import com.example.order.module.model.metadata.ScheduleMetadata;
import com.example.order.module.model.request.PersonalOfferData;
import com.example.order.module.model.request.PersonalOfferListRequest;
import com.example.order.module.model.response.*;
import com.example.order.module.rest.ClientEmployee;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskService {
    private final JavaMailSender javaMailSender;
    private final ScheduleMetadata scheduleMetadata;
    private final OrderService orderService;
    private final ClientEmployee clientEmployee;
    private final TemplateEngine templateEngine;

    @Scheduled(cron = "${schedule.send.time}")
    public void createAndSendPersonalOffer() {
        EmployeeListResponse allEmployees = clientEmployee.getAllEmployees();
        List<EmployeeEntityResponse> employeeResponseList = allEmployees.getEmployeeResponseList();
        Map<Long, String> employeeIdEmailMap = employeeResponseList.stream()
                .collect(Collectors.toMap(EmployeeEntityResponse::getId, EmployeeEntityResponse::getEmail));

        Set<Long> employeeIdList = employeeIdEmailMap.keySet();
        List<OrderResponse> orderResponseList = orderService.getAllOrders().getOrderList();

        List<PersonalOfferData> personalOfferDataList = employeeIdList.stream()
                .flatMap(
                        employeeId -> {
                            List<Long> productIdList = new ArrayList<>();
                            return orderResponseList.stream().filter(x -> x.getCustomerId().equals(employeeId))
                                    .filter(x -> x.getOrderDate().isAfter(LocalDateTime.now().minusMonths(1)))
                                    .map(orderResponse -> {
                                        Long productId = orderResponse.getProductId();
                                        productIdList.add(productId);
                                        return productId;
                                    })
                                    .map(productId -> PersonalOfferData.builder().employeeId(employeeId).productIdList(productIdList).build());
                        }
                ).toList();

        PersonalOfferListResponse personalOfferList = orderService.getPersonalOfferList(PersonalOfferListRequest.builder().personalOfferDataList(personalOfferDataList).build());

        personalOfferList.getPersonalOfferForEmployeeList()
                .forEach(personalOfferForEmployee ->
                        sendPersonalOffer(employeeIdEmailMap.get(personalOfferForEmployee.getEmployeeId()), personalOfferForEmployee.getPersonalOfferResponse()));

        log.info("Email was sent");
    }

    private void sendPersonalOffer(String email, PersonalOfferResponse personalOfferResponse) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

            Context context = new Context();
            context.setVariable("personalOffer", personalOfferResponse);
            String emailContent = templateEngine.process("emailPersonalOffer", context);

            messageHelper.setFrom(scheduleMetadata.getUsername() + "@mail.ru");
            messageHelper.setTo(email);
            messageHelper.setSubject(scheduleMetadata.getSubject());
            messageHelper.setText(emailContent, true);

        } catch (MessagingException messagingException) {
            throw new EmailException("Couldn't send email", 500);
        }

        javaMailSender.send(mimeMessage);
    }
}
