package com.partha.expensemanager.service;

import com.partha.expensemanager.dto.ExpenseDTO;
import com.partha.expensemanager.entity.ProfileEntity;
import com.partha.expensemanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {


    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${expense.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            String body = "Hi " + profile.getFullName() + ",<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Expense Manager.<br><br>"
                    + "<a href='" + frontendUrl + "' "
                    + "style='display:inline-block; padding:12px 24px; background-color:#4CAF50; "
                    + "color:white; text-decoration:none; border-radius:5px; font-weight:bold;'>"
                    + "Open Expense Manager</a>"
                    + "<br><br>"
                    + "Keeping your records updated helps you track your spending habits and manage your finances better."
                    + "<br><br>"
                    + "Best regards,<br>"
                    + "<b>Expense Manager Team</b>";
            emailService.sendEmail(profile.getEmail(), "Daily reminder: add your income and expenses", body);
        }
        log.info("Job finished: sendDailyIncomeExpenseReminder()");
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile : profiles) {
            List<ExpenseDTO> todaysExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
            if (!todaysExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();

                table.append("<table style='border-collapse:collapse;width:100%;'>")
                        .append("<tr style='background-color:#f2f2f2;'>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>#</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Date</th>")
                        .append("</tr>");

                int i = 1;
                // Table Rows
                for (ExpenseDTO expense : todaysExpenses) {
                    table.append("<tr>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(i++)
                            .append("</td>")

                            .append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expense.getName())
                            .append("</td>")

                            .append("<td style='border:1px solid #ddd;padding:8px;'>₹")
                            .append(expense.getAmount())
                            .append("</td>")

                            .append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A")
                            .append("</td>");
                }
                table.append("</table>");
                String body = "Hi "+ profile.getFullName()+",<br/><br/> Here is a summary of your expenses for today:<br/><br/>"+table+"<br/><br/>Best regards,<br/>Expense manager team";
                emailService.sendEmail(profile.getEmail(), "Your daily expense summary", body);
            }
        }
        log.info("Job finished: sendDailyExpenseSummary()");
    }
}
