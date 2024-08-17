package net.gb.knox.nudge.scheduler.task;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import net.gb.knox.nudge.model.Nudge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class SendEmailTask {

    private final SendGrid sendGrid;

    @Autowired
    public SendEmailTask(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    public Runnable run(Jwt principal, Nudge nudge, TaskResult taskResult) {
        return () -> {
            // TODO: Move to application.yml
            var templateId = "d-2f7cd598b1614d26b768f40dab6b10c4";

            // TODO: Handle missing claims
            String givenName = principal.getClaim("given_name");
            String email = principal.getClaim("email");

            var from = new Email("noreply@em6109.knox.gb.net");
            var to = new Email(email);

            var mail = new Mail();
            mail.setFrom(from);
            mail.setTemplateId(templateId);

            var personalization = new Personalization();
            personalization.addTo(to);

            var nudgeData = Map.of("title", nudge.getTitle(), "description", nudge.getDescription(),
                    "due", nudge.getDue().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));

            personalization.addDynamicTemplateData("name", givenName);
            personalization.addDynamicTemplateData("nudge", nudgeData);

            mail.addPersonalization(personalization);

            var request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            try {
                request.setBody(mail.build());
                var response = sendGrid.api(request);

                // TODO: Handle failure condition
                taskResult.onSuccess();

                System.out.println("Status code: " + response.getStatusCode());
                System.out.println("Body: " + response.getBody());
                System.out.println("Headers: " + response.getHeaders());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
