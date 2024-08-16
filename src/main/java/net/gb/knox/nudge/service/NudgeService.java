package net.gb.knox.nudge.service;

import net.gb.knox.nudge.converter.NudgeConverter;
import net.gb.knox.nudge.converter.TriggerConverter;
import net.gb.knox.nudge.domain.GetNudges;
import net.gb.knox.nudge.domain.UpsertNudge;
import net.gb.knox.nudge.exception.EntityMissingException;
import net.gb.knox.nudge.model.Nudge;
import net.gb.knox.nudge.repository.NudgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NudgeService {

    private final NudgeRepository nudgeRepository;

    @Autowired
    public NudgeService(NudgeRepository nudgeRepository) {
        this.nudgeRepository = nudgeRepository;
    }

    public GetNudges getAllNudgesForUser(String userId) {
        return new GetNudges(nudgeRepository.findAllByUserId(userId).stream().map(NudgeConverter::convert).toList());
    }

    public GetNudges getAllNudgesForUser(String userId, Sort sort) {
        return new GetNudges(nudgeRepository.findAllByUserId(userId, sort).stream().map(NudgeConverter::convert).toList());
    }

    public Long createNudgeForUser(String userId, UpsertNudge upsertNudge) {
        var newNudge = new Nudge(userId, upsertNudge.title(), upsertNudge.description(), upsertNudge.due(),
                upsertNudge.triggers().stream().map(TriggerConverter::convert).toList());

        nudgeRepository.save(newNudge);

        return newNudge.getId();
    }

    public void updateNudgeForUser(Long id, String userId, UpsertNudge upsertNudge) throws EntityMissingException {
        if (!nudgeRepository.existsByIdAndUserId(id, userId)) {
            throw new EntityMissingException(String.format("Nudge not found with id = %s and userId = %s", id, userId));
        }

        var updatedNudge = new Nudge(id, userId, upsertNudge.title(), upsertNudge.description(), upsertNudge.due(),
                    upsertNudge.triggers().stream().map(TriggerConverter::convert).toList());
        nudgeRepository.save(updatedNudge);
    }

    @Transactional
    public void deleteNudgeForUser(Long id, String userId) throws EntityMissingException {
        if (!nudgeRepository.existsByIdAndUserId(id, userId)) {
            throw new EntityMissingException(String.format("Nudge not found with id = %s and userId = %s", id, userId));
        }
        nudgeRepository.deleteByIdAndUserId(id, userId);
    }
}
