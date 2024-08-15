package net.gb.knox.nudge.repository;

import net.gb.knox.nudge.fixture.NudgeFixture;
import net.gb.knox.nudge.model.Nudge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class NudgeRepositoryIntegrationTests {

    @Autowired
    private NudgeRepository nudgeRepository;

    private static Stream<Arguments> existsByIdAndUserIdParameterProvider() {
        var idAndUserId = Arguments.of(NudgeFixture.NUDGE.getId(), NudgeFixture.NUDGE.getUserId(), true);
        var missingIdAndUserId = Arguments.of(NudgeFixture.MISSING_ID, NudgeFixture.NUDGE.getUserId(), false);
        var idAndMissingUserId = Arguments.of(NudgeFixture.NUDGE.getId(), NudgeFixture.MISSING_USER_ID, false);

        return Stream.of(idAndUserId, missingIdAndUserId, idAndMissingUserId);
    }

    @Test
    public void testFindAllByUserId() {
        var all = nudgeRepository.findAllByUserId(NudgeFixture.NUDGE.getUserId());
        assertThat(all).hasSize(2).isInstanceOf(List.class).hasOnlyElementsOfType(Nudge.class);

        all = nudgeRepository.findAllByUserId(NudgeFixture.NUDGE.getUserId(), Sort.by("title"));
        assertThat(all).hasSize(2).isInstanceOf(List.class).hasOnlyElementsOfType(Nudge.class);

        all = nudgeRepository.findAllByUserId(NudgeFixture.MISSING_USER_ID);
        assertThat(all).isEmpty();
    }

    @Test
    public void testDeleteByIdAndUserId() {
        nudgeRepository.deleteByIdAndUserId(NudgeFixture.NUDGE.getId(), NudgeFixture.NUDGE.getUserId());
        var deletedNudge = nudgeRepository.findById(NudgeFixture.NUDGE.getId());
        assertThat(deletedNudge).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("existsByIdAndUserIdParameterProvider")
    public void testExistsByIdAndUserId(Long id, String userId, boolean expectedDoesExist) {
        var doesExist = nudgeRepository.existsByIdAndUserId(id, userId);
        assertThat(doesExist).isEqualTo(expectedDoesExist);
    }
}
