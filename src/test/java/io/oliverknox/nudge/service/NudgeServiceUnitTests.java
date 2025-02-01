package io.oliverknox.nudge.service;

import io.oliverknox.nudge.domain.*;
import io.oliverknox.nudge.exception.EntityMissingException;
import io.oliverknox.nudge.fixture.JwtFixture;
import io.oliverknox.nudge.fixture.NudgeFixture;
import io.oliverknox.nudge.model.Nudge;
import io.oliverknox.nudge.model.Trigger;
import io.oliverknox.nudge.repository.NudgeRepository;
import io.oliverknox.nudge.scheduler.TriggerScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class NudgeServiceUnitTests {

    private NudgeRepository repository;
    private TriggerScheduler triggerScheduler;
    private NudgeService service;

    @BeforeEach
    public void setUp() {
        repository = mock(NudgeRepository.class);
        triggerScheduler = mock(TriggerScheduler.class);
        service = new NudgeService(repository, triggerScheduler);

        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    public void tearDown() {
        TransactionSynchronizationManager.clear();
    }

    @Test
    public void testGetAllNudgesForUser() {
        var sort = Sort.by("title");
        when(repository.findAllByUserId(NudgeFixture.USER_ID)).thenReturn(NudgeFixture.NUDGES);
        when(repository.findAllByUserId(NudgeFixture.USER_ID, sort)).thenReturn(NudgeFixture.NUDGES);

        var getNudges = service.getAllNudgesForUser(NudgeFixture.USER_ID);
        assertThat(getNudges).isInstanceOf(GetNudges.class);
        assertThat(getNudges.nudges()).isNotEmpty();

        getNudges = service.getAllNudgesForUser(NudgeFixture.USER_ID, sort);
        assertThat(getNudges).isInstanceOf(GetNudges.class);
        assertThat(getNudges.nudges()).isNotEmpty();
    }

    @Test
    public void testCreateNudgeForUser() {
        var createTrigger = new CreateTrigger(Period.DAY, 1, Optional.of(Communication.ASSERTIVE));
        var upsertNudge = new UpsertNudge("Title", "Description",
                LocalDate.of(2025, 1, 1), List.of(createTrigger));
        when(repository.save(any(Nudge.class))).thenReturn(NudgeFixture.NUDGE);

        service.createNudgeForUser(JwtFixture.JWT, upsertNudge);

        verify(repository, times(1)).save(any(Nudge.class));

        // Simulate transaction commit
        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(triggerScheduler).scheduleTask(any(Jwt.class), any(Nudge.class), any(Trigger.class));
    }

    @Test
    public void testUpdateNudgeForUser() throws EntityMissingException {
        var id = 1L;
        var createTrigger = new CreateTrigger(Period.DAY, 1, Optional.of(Communication.ASSERTIVE));
        var upsertNudge = new UpsertNudge("Title", "Description",
                LocalDate.of(2025, 1, 1), List.of(createTrigger));
        when(repository.findByIdAndUserId(id, NudgeFixture.USER_ID)).thenReturn(Optional.of(NudgeFixture.NUDGE));

        service.updateNudgeForUser(id, NudgeFixture.USER_ID, upsertNudge);

        verify(repository, times(1)).save(any(Nudge.class));

        // Simulate transaction commit
        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(triggerScheduler).cancelTask(NudgeFixture.NUDGE.getId());
    }

    @Test
    public void testUpdateNudgeForUserThrows() {
        var id = 1L;
        var createTrigger = new CreateTrigger(Period.DAY, 1, Optional.of(Communication.ASSERTIVE));
        var upsertNudge = new UpsertNudge("Title", "Description",
                LocalDate.of(2025, 1, 1), List.of(createTrigger));
        when(repository.existsByIdAndUserId(id, NudgeFixture.USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.updateNudgeForUser(id, NudgeFixture.USER_ID, upsertNudge))
                .isInstanceOf(EntityMissingException.class);
    }

    @Test
    public void testDeleteNudgeForUser() throws EntityMissingException {
        var id = 1L;
        when(repository.existsByIdAndUserId(id, NudgeFixture.USER_ID)).thenReturn(true);

        service.deleteNudgeForUser(id, NudgeFixture.USER_ID);

        verify(repository, times(1)).deleteByIdAndUserId(id, NudgeFixture.USER_ID);

        // Simulate transaction commit
        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(triggerScheduler).cancelTask(NudgeFixture.NUDGE.getId());
    }

    @Test
    public void testDeleteNudgeForUserThrows() {
        var id = 1L;
        when(repository.existsByIdAndUserId(id, NudgeFixture.USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteNudgeForUser(id, NudgeFixture.USER_ID)).isInstanceOf(EntityMissingException.class);
    }
}
