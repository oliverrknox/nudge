package io.oliverknox.nudge.service;

import io.oliverknox.nudge.converter.NudgeConverter;
import io.oliverknox.nudge.converter.TriggerConverter;
import io.oliverknox.nudge.domain.GetNudges;
import io.oliverknox.nudge.domain.UpsertNudge;
import io.oliverknox.nudge.exception.EntityMissingException;
import io.oliverknox.nudge.model.Nudge;
import io.oliverknox.nudge.model.Trigger;
import io.oliverknox.nudge.repository.NudgeRepository;
import io.oliverknox.nudge.scheduler.TriggerScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public NudgeService(NudgeRepository nudgeRepository, TriggerScheduler triggerScheduler) {
        this.nudgeRepository = nudgeRepository;
        this.triggerScheduler = triggerScheduler;
    }

    public GetNudges getAllNudgesForUser(String userId) {
        logger.info("getAllNudgesForUser(userId: {}): enter", userId);
        var getNudges = new GetNudges(nudgeRepository.findAllByUserId(userId).stream().map(NudgeConverter::convert).toList());
        logger.info("getAllNudgesForUser(userId: {}): exit", userId);
        return getNudges;
    }

    public GetNudges getAllNudgesForUser(String userId, Sort sort) {
        logger.info("getAllNudgesForUser(userId: {}, sort: {}): enter", userId, sort);
        var getNudges = new GetNudges(nudgeRepository.findAllByUserId(userId, sort).stream().map(NudgeConverter::convert).toList());
        logger.info("getAllNudgesForUser(userId: {}, sort: {}): exit", userId, sort);
        return getNudges;
    }

    @Transactional
    public Long createNudgeForUser(Jwt principal, UpsertNudge upsertNudge) {
        logger.info("createNudgeForUser(principal: {}, upsertNudge: {}): enter", principal.getSubject(), upsertNudge);
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

        logger.info("createNudgeForUser(): exit");
        return savedNudge.getId();
    }

    @Transactional
    public void updateNudgeForUser(Long id, String userId, UpsertNudge upsertNudge) throws EntityMissingException {
        logger.info("updateNudgeForUser(id: {}, userId: {}, upsertNudge: {}): enter", id, userId, upsertNudge);
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
        logger.info("updateNudgeForUser(): exit");
    }

    @Transactional
    public void deleteNudgeForUser(Long id, String userId) throws EntityMissingException {
        logger.info("deleteNudgeForUser(id: {}, userId: {}): enter", id, userId);
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
        logger.info("deleteNudgeForUser(): exit");
    }
}
