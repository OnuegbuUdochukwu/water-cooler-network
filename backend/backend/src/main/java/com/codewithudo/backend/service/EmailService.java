package com.codewithudo.backend.service;

import com.codewithudo.backend.entity.Notification;
import com.codewithudo.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.notifications.email.enabled:true}")
    private boolean emailEnabled;
    
    @Value("${app.notifications.email.from-name:Water Cooler Network}")
    private String fromName;
    
    /**
     * Send simple text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            return;
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        mailSender.send(message);
    }
    
    /**
     * Send HTML email using template
     */
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!emailEnabled) {
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            Context context = new Context();
            variables.forEach(context::setVariable);
            
            String htmlContent = templateEngine.process(templateName, context);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    /**
     * Send notification email
     */
    public void sendNotificationEmail(User user, Notification notification) {
        if (!emailEnabled || user.getEmail() == null) {
            return;
        }
        
        Map<String, Object> variables = Map.of(
            "userName", user.getName(),
            "notificationTitle", notification.getTitle(),
            "notificationMessage", notification.getMessage(),
            "notificationType", notification.getType().toString(),
            "actionUrl", notification.getActionUrl() != null ? notification.getActionUrl() : "",
            "priority", notification.getPriority().toString()
        );
        
        String templateName = getTemplateNameForNotificationType(notification.getType());
        sendTemplateEmail(user.getEmail(), notification.getTitle(), templateName, variables);
    }
    
    /**
     * Send match notification email
     */
    public void sendMatchNotificationEmail(User user, String matchName, String matchReason) {
        if (!emailEnabled || user.getEmail() == null) {
            return;
        }
        
        Map<String, Object> variables = Map.of(
            "userName", user.getName(),
            "matchName", matchName,
            "matchReason", matchReason,
            "actionUrl", "/matches"
        );
        
        sendTemplateEmail(user.getEmail(), "New Match Found!", "match-notification", variables);
    }
    
    /**
     * Send meeting reminder email
     */
    public void sendMeetingReminderEmail(User user, String meetingTitle, String meetingTime, String participantName) {
        if (!emailEnabled || user.getEmail() == null) {
            return;
        }
        
        Map<String, Object> variables = Map.of(
            "userName", user.getName(),
            "meetingTitle", meetingTitle,
            "meetingTime", meetingTime,
            "participantName", participantName,
            "actionUrl", "/meetings"
        );
        
        sendTemplateEmail(user.getEmail(), "Meeting Reminder", "meeting-reminder", variables);
    }
    
    /**
     * Send badge earned email
     */
    public void sendBadgeEarnedEmail(User user, String badgeName, String badgeDescription) {
        if (!emailEnabled || user.getEmail() == null) {
            return;
        }
        
        Map<String, Object> variables = Map.of(
            "userName", user.getName(),
            "badgeName", badgeName,
            "badgeDescription", badgeDescription,
            "actionUrl", "/profile"
        );
        
        sendTemplateEmail(user.getEmail(), "Badge Earned!", "badge-earned", variables);
    }
    
    /**
     * Send corporate announcement email
     */
    public void sendCorporateAnnouncementEmail(User user, String announcementTitle, String announcementContent, String companyName) {
        if (!emailEnabled || user.getEmail() == null) {
            return;
        }
        
        Map<String, Object> variables = Map.of(
            "userName", user.getName(),
            "announcementTitle", announcementTitle,
            "announcementContent", announcementContent,
            "companyName", companyName,
            "actionUrl", "/corporate/announcements"
        );
        
        sendTemplateEmail(user.getEmail(), announcementTitle, "corporate-announcement", variables);
    }
    
    /**
     * Get template name based on notification type
     */
    private String getTemplateNameForNotificationType(Notification.NotificationType type) {
        return switch (type) {
            case MATCH_FOUND -> "match-notification";
            case MEETING_REMINDER -> "meeting-reminder";
            case BADGE_EARNED -> "badge-earned";
            case SYSTEM_ANNOUNCEMENT -> "system-announcement";
            case LOUNGE_INVITATION -> "lounge-invitation";
            default -> "general-notification";
        };
    }
    
    /**
     * Check if email service is enabled
     */
    public boolean isEmailEnabled() {
        return emailEnabled;
    }
}
