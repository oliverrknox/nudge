package net.gb.knox.nudge.scheduler;

import net.gb.knox.nudge.fixture.JwtFixture;
import net.gb.knox.nudge.fixture.NudgeFixture;
import net.gb.knox.nudge.fixture.TriggerFixture;
import net.gb.knox.nudge.model.Nudge;
import net.gb.knox.nudge.scheduler.task.SendEmailTask;
import net.gb.knox.nudge.scheduler.task.TaskResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TriggerSchedulerUnitTests {

    private TaskScheduler taskScheduler;
    private SendEmailTask sendEmailTask;
    private TriggerScheduler triggerScheduler;

    @BeforeEach
    public void setUp() {
        taskScheduler = mock(TaskScheduler.class);
        sendEmailTask = mock(SendEmailTask.class);
        triggerScheduler = new TriggerScheduler(taskScheduler, sendEmailTask);
    }

    @Test
    public void testScheduleTask() {
        var expectedInstant = NudgeFixture.NUDGE.getDue().minusMonths(TriggerFixture.TRIGGER.getSpan()).atStartOfDay(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> scheduledFuture = mock(ScheduledFuture.class);
        doReturn(scheduledFuture).when(taskScheduler).schedule(any(), (Instant) any());
        when(sendEmailTask.build(any(Jwt.class), any(Nudge.class), any(TaskResult.class))).thenReturn(() -> {});

        triggerScheduler.scheduleTask(JwtFixture.JWT, NudgeFixture.NUDGE, TriggerFixture.TRIGGER);

        verify(taskScheduler).schedule(any(Runnable.class), eq(expectedInstant));

        var task = Optional.ofNullable(triggerScheduler.getTasks().get(TriggerFixture.TRIGGER.getId()));
        assertThat(task).isPresent();
    }

    @Test
    public void testCancelTask() {
        ScheduledFuture<?> scheduledFuture = mock(ScheduledFuture.class);
        doReturn(scheduledFuture).when(taskScheduler).schedule(any(Runnable.class), any(Instant.class));
        when(sendEmailTask.build(any(Jwt.class), any(Nudge.class), any(TaskResult.class))).thenReturn(() -> {});

        triggerScheduler.scheduleTask(JwtFixture.JWT, NudgeFixture.NUDGE, TriggerFixture.TRIGGER);
        triggerScheduler.cancelTask(TriggerFixture.TRIGGER.getId());

        var task = Optional.ofNullable(triggerScheduler.getTasks().get(TriggerFixture.TRIGGER.getId()));
        assertThat(task).isEmpty();

        verify(scheduledFuture, times(1)).cancel(false);
    }
}
