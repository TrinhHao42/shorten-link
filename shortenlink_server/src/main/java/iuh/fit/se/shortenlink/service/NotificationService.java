package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.dto.notification.ShareLinkMessage;
import iuh.fit.se.shortenlink.dto.notification.ShareLinkRequest;
import iuh.fit.se.shortenlink.entity.Notification;
import iuh.fit.se.shortenlink.entity.User;
import iuh.fit.se.shortenlink.entity.enums.NotificationType;
import iuh.fit.se.shortenlink.integration.rabbitmq.NotificationPublisher;
import iuh.fit.se.shortenlink.repository.NotificationRepository;
import iuh.fit.se.shortenlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationPublisher notificationPublisher;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final String mailFrom;

    public NotificationService(NotificationPublisher notificationPublisher,
                               NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               JavaMailSender mailSender,
                               @Value("${app.mail.from}") String mailFrom) {
        this.notificationPublisher = notificationPublisher;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
    }

    public void shareLink(Long senderId, ShareLinkRequest request) {
        ShareLinkMessage payload = ShareLinkMessage.builder()
                .type(NotificationType.SHARE_LINK.name())
                .senderId(senderId)
                .receiverEmail(request.getReceiverEmail())
                .link(request.getShortLink())
                .message(request.getMessage())
                .build();

        notificationPublisher.publishShareLink(payload);
    }

    public void consumeShareLink(ShareLinkMessage message) {
        User sender = userRepository.findById(message.senderId()).orElse(null);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(mailFrom);
        mail.setTo(message.receiverEmail());
        mail.setSubject("A link has been shared with you");
        String body = "Link: " + message.link() + "\n\n" + (message.message() == null ? "" : message.message());
        mail.setText(body);
        mailSender.send(mail);

        Notification notification = Notification.builder()
                .user(sender)
                .type(NotificationType.SHARE_LINK)
                .message(body)
                .build();

        notificationRepository.save(notification);
    }
}
