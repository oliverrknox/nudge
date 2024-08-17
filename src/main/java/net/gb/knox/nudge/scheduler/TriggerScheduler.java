package net.gb.knox.nudge.scheduler;

import lombok.Getter;
import net.gb.knox.nudge.model.Nudge;
import net.gb.knox.nudge.model.Trigger;
import net.gb.knox.nudge.scheduler.task.SendEmailTask;
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

    @Getter
    private final Map<Long, ScheduledFuture<?>> tasks; // TODO: Persist task IDs and reschedule tasks post construct

    @Autowired
    public TriggerScheduler(TaskScheduler taskScheduler, SendEmailTask sendEmailTask) {
        this.taskScheduler = taskScheduler;
        this.sendEmailTask = sendEmailTask;
        this.tasks = new ConcurrentHashMap<>();
    }

    public void scheduleTask(Jwt principal, Nudge nudge, Trigger trigger) {
        LocalDate scheduledDate = switch (trigger.getPeriod()) {
            case DAY -> nudge.getDue().minusDays(trigger.getSpan());
            case WEEK -> nudge.getDue().minusWeeks(trigger.getSpan());
            case MONTH -> nudge.getDue().minusMonths(trigger.getSpan());
        };

        var task = sendEmailTask.run(principal, nudge, () -> tasks.remove(trigger.getId()));
        var scheduledTask = taskScheduler.schedule(task, scheduledDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        tasks.put(trigger.getId(), scheduledTask);
    }

    public void cancelTask(Long id) {
        if (tasks.containsKey(id)) {
            var task = tasks.remove(id);
            task.cancel(false);
        }
    }
}
