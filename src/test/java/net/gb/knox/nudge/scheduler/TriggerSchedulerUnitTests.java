package net.gb.knox.nudge.scheduler;

import net.gb.knox.nudge.fixture.TriggerFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TriggerSchedulerUnitTests {

    private TaskScheduler taskScheduler;
    private TriggerScheduler triggerScheduler;

    @BeforeEach
    public void setUp() {
        taskScheduler = mock(TaskScheduler.class);
        triggerScheduler = new TriggerScheduler(taskScheduler);
    }

    @Test
    public void testScheduleTask() {
        var due = LocalDate.now();
        var expectedInstant = due.minusMonths(TriggerFixture.TRIGGER.getSpan()).atStartOfDay(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> scheduledFuture = mock(ScheduledFuture.class);
        doReturn(scheduledFuture).when(taskScheduler).schedule(any(Runnable.class), any(Instant.class));

        triggerScheduler.scheduleTask(due, TriggerFixture.TRIGGER);

        verify(taskScheduler).schedule(any(Runnable.class), eq(expectedInstant));

        var task = Optional.ofNullable(triggerScheduler.getTasks().get(TriggerFixture.TRIGGER.getId()));
        assertThat(task).isPresent();
    }

    @Test
    public void testCancelTask() {
        var due = LocalDate.now();
        ScheduledFuture<?> scheduledFuture = mock(ScheduledFuture.class);
        doReturn(scheduledFuture).when(taskScheduler).schedule(any(Runnable.class), any(Instant.class));

        triggerScheduler.scheduleTask(due, TriggerFixture.TRIGGER);
        triggerScheduler.cancelTask(TriggerFixture.TRIGGER.getId());

        var task = Optional.ofNullable(triggerScheduler.getTasks().get(TriggerFixture.TRIGGER.getId()));
        assertThat(task).isEmpty();

        verify(scheduledFuture, times(1)).cancel(false);
    }
}
