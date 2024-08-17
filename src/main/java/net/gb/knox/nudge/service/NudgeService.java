package net.gb.knox.nudge.service;

import net.gb.knox.nudge.converter.NudgeConverter;
import net.gb.knox.nudge.converter.TriggerConverter;
import net.gb.knox.nudge.domain.GetNudges;
import net.gb.knox.nudge.domain.UpsertNudge;
import net.gb.knox.nudge.exception.EntityMissingException;
import net.gb.knox.nudge.model.Nudge;
import net.gb.knox.nudge.model.Trigger;
import net.gb.knox.nudge.repository.NudgeRepository;
import net.gb.knox.nudge.scheduler.TriggerScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Consumer;

@Service
public class NudgeService {

    private final NudgeRepository nudgeRepository;
    private final TriggerScheduler triggerScheduler;

    @Autowired
    public NudgeService(NudgeRepository nudgeRepository, TriggerScheduler triggerScheduler) {
        this.nudgeRepository = nudgeRepository;
        this.triggerScheduler = triggerScheduler;
    }

    public GetNudges getAllNudgesForUser(String userId) {
        return new GetNudges(nudgeRepository.findAllByUserId(userId).stream().map(NudgeConverter::convert).toList());
    }

    public GetNudges getAllNudgesForUser(String userId, Sort sort) {
        return new GetNudges(nudgeRepository.findAllByUserId(userId, sort).stream().map(NudgeConverter::convert).toList());
    }

    @Transactional
    public Long createNudgeForUser(Jwt principal, UpsertNudge upsertNudge) {
        var newNudge = new Nudge(principal.getSubject(), upsertNudge.title(), upsertNudge.description(), upsertNudge.due(),
                upsertNudge.triggers().stream().map(TriggerConverter::convert).toList());

        var savedNudge = nudgeRepository.save(newNudge);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                Consumer<Trigger> scheduleTask = (Trigger trigger) -> triggerScheduler.scheduleTask(principal, savedNudge, trigger);
                savedNudge.getTriggers().parallelStream().forEach(scheduleTask);
            }
        });

        return savedNudge.getId();
    }

    @Transactional
    public void updateNudgeForUser(Long id, String userId, UpsertNudge upsertNudge) throws EntityMissingException {
        var triggerIds = nudgeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityMissingException(String.format("Nudge not found with id = %s and userId = %s", id, userId)))
                .getTriggers().stream().map(Trigger::getId).toList();

        var updatedNudge = new Nudge(id, userId, upsertNudge.title(), upsertNudge.description(), upsertNudge.due(),
                    upsertNudge.triggers().stream().map(TriggerConverter::convert).toList());

        nudgeRepository.save(updatedNudge);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                triggerIds.parallelStream().forEach(triggerScheduler::cancelTask);
            }
        });
    }

    @Transactional
    public void deleteNudgeForUser(Long id, String userId) throws EntityMissingException {
        if (!nudgeRepository.existsByIdAndUserId(id, userId)) {
            throw new EntityMissingException(String.format("Nudge not found with id = %s and userId = %s", id, userId));
        }
        nudgeRepository.deleteByIdAndUserId(id, userId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                triggerScheduler.cancelTask(id);
            }
        });
    }
}
