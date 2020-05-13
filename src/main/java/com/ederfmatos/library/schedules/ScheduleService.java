package com.ederfmatos.library.schedules;

import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.service.EmailService;
import com.ederfmatos.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class ScheduleService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Autowired
    private LoanService loanService;

    @Autowired
    private EmailService emailService;

    @Value("${application.mail.late}")
    private String message;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans() {
        List<Loan> lateLoans = loanService.getAllLateLoans();

        List<String> emails = lateLoans.stream().map(Loan::getCustomerEmail).collect(Collectors.toList());

        emailService.sendMails(message, emails);
    }

}
