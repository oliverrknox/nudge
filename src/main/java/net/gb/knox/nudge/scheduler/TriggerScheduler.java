package net.gb.knox.nudge.scheduler;

import lombok.Getter;
import net.gb.knox.nudge.model.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class TriggerScheduler {

    private final TaskScheduler taskScheduler;

    @Getter
    private final Map<Long, ScheduledFuture<?>> tasks; // TODO: Persist task IDs and reschedule tasks post construct

    @Autowired
    public TriggerScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.tasks = new ConcurrentHashMap<>();
    }

    public void scheduleTask(LocalDate due, Trigger trigger) {
        Runnable task = () -> {
            System.out.println("Running trigger: " + trigger.getId());
            tasks.remove(trigger.getId());
        };

        LocalDate scheduledDate = switch (trigger.getPeriod()) {
            case DAY -> due.minusDays(trigger.getSpan());
            case WEEK -> due.minusWeeks(trigger.getSpan());
            case MONTH -> due.minusMonths(trigger.getSpan());
        };

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
