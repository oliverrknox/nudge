package net.gb.knox.nudge.service;

import net.gb.knox.nudge.domain.*;
import net.gb.knox.nudge.exception.EntityMissingException;
import net.gb.knox.nudge.fixture.NudgeFixture;
import net.gb.knox.nudge.model.Nudge;
import net.gb.knox.nudge.repository.NudgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class NudgeServiceUnitTests {

    private NudgeRepository repository;
    private NudgeService service;

    @BeforeEach
    public void setUp() {
        repository = mock(NudgeRepository.class);
        service = new NudgeService(repository);
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

        service.createNudgeForUser(NudgeFixture.USER_ID, upsertNudge);

        verify(repository, times(1)).save(any(Nudge.class));
    }

    @Test
    public void testUpdateNudgeForUser() throws EntityMissingException {
        var id = 1L;
        var createTrigger = new CreateTrigger(Period.DAY, 1, Optional.of(Communication.ASSERTIVE));
        var upsertNudge = new UpsertNudge("Title", "Description",
                LocalDate.of(2025, 1, 1), List.of(createTrigger));
        when(repository.existsByIdAndUserId(id, NudgeFixture.USER_ID)).thenReturn(true);

        service.updateNudgeForUser(id, NudgeFixture.USER_ID, upsertNudge);

        verify(repository, times(1)).save(any(Nudge.class));
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
    }

    @Test
    public void testDeleteNudgeForUserThrows() {
        var id = 1L;
        when(repository.existsByIdAndUserId(id, NudgeFixture.USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteNudgeForUser(id, NudgeFixture.USER_ID)).isInstanceOf(EntityMissingException.class);
    }
}
