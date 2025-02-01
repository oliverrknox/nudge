package io.oliverknox.nudge.scheduler.task;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.oliverknox.nudge.exception.SendGridException;
import io.oliverknox.nudge.model.Nudge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Component
public class SendEmailTask {

    private final SendGrid sendGrid;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public SendEmailTask(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    // TODO: Refactor logic into private methods
    public Runnable build(Jwt principal, Nudge nudge, TaskResult taskResult) {
        return () -> {
            // TODO: Move to application.yml
            var templateId = "d-2f7cd598b1614d26b768f40dab6b10c4";

            Optional<String> givenName = Optional.ofNullable(principal.getClaim("given_name"));
            Optional<String> email = Optional.ofNullable(principal.getClaim("email"));

            if (email.isEmpty()) {
                var errorMessage = "Principal's claims are missing email";
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            if (givenName.isEmpty()) {
                logger.warn("Principal's claims are missing given name");
            }

            var from = new Email("noreply@em6109.oliverknox.io");
            var to = new Email(email.get());

            var mail = new Mail();
            mail.setFrom(from);
            mail.setTemplateId(templateId);

            var personalization = new Personalization();
            personalization.addTo(to);

            var nudgeData = Map.of("title", nudge.getTitle(), "description", nudge.getDescription(),
                    "due", nudge.getDue().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));

            personalization.addDynamicTemplateData("name", givenName.orElse("User"));
            personalization.addDynamicTemplateData("nudge", nudgeData);

            mail.addPersonalization(personalization);

            var request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            try {
                request.setBody(mail.build());
                var response = sendGrid.api(request);
                var statusCode = response.getStatusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    taskResult.onSuccess();
                    logger.info("Email sent successfully with status: {}, headers: {}, and body: {}",
                            response.getStatusCode(), response.getHeaders(), response.getBody());
                } else {
                    var errorMessage = String.format("Email failed to send with status: %s, headers: %s, and body: %s",
                            response.getStatusCode(), response.getHeaders(), response.getBody());
                    throw new SendGridException(errorMessage);
                }
            } catch (Exception e) {
                logger.error("{}: {}", e.getClass().getName(), e.getMessage());
                throw new RuntimeException(e);
            }
        };
    }
}
