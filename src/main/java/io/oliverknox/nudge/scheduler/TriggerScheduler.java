package io.oliverknox.nudge.scheduler;

import lombok.Getter;
import io.oliverknox.nudge.model.Nudge;
import io.oliverknox.nudge.model.Trigger;
import io.oliverknox.nudge.scheduler.task.SendEmailTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class TriggerScheduler {

    private final TaskScheduler taskScheduler;
    private final SendEmailTask sendEmailTask;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Getter
    private final Map<Long, ScheduledFuture<?>> tasks; // TODO: Persist task IDs and reschedule tasks post construct

    @Autowired
    public TriggerScheduler(TaskScheduler taskScheduler, SendEmailTask sendEmailTask) {
        this.taskScheduler = taskScheduler;
        this.sendEmailTask = sendEmailTask;
        this.tasks = new ConcurrentHashMap<>();
    }

    public void scheduleTask(Jwt principal, Nudge nudge, Trigger trigger) {
        logger.info("scheduleTask(principal: {}, nudge: {}, trigger: {}): enter", principal.getSubject(), nudge, trigger);

        LocalDate scheduledDate = switch (trigger.getPeriod()) {
            case DAY -> nudge.getDue().minusDays(trigger.getSpan());
            case WEEK -> nudge.getDue().minusWeeks(trigger.getSpan());
            case MONTH -> nudge.getDue().minusMonths(trigger.getSpan());
        };

        var task = sendEmailTask.build(principal, nudge, () -> tasks.remove(trigger.getId()));
        var scheduledTask = taskScheduler.schedule(task, scheduledDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        tasks.put(trigger.getId(), scheduledTask);

        logger.info("scheduleTask(): exit");
    }

    public void cancelTask(Long id) {
        logger.info("cancelTask(id: {}): enter", id);

        if (tasks.containsKey(id)) {
            var task = tasks.remove(id);
            task.cancel(false);
            logger.info("cancelTask(): remove and cancel");
        }

        logger.info("cancelTask(): exit");
    }
}
