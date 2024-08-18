package net.gb.knox.nudge.controller;

import net.gb.knox.nudge.domain.GetNudges;
import net.gb.knox.nudge.domain.UpsertNudge;
import net.gb.knox.nudge.exception.EntityMissingException;
import net.gb.knox.nudge.service.NudgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/nudges")
public class NudgeController {

    private final NudgeService nudgeService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public NudgeController(NudgeService nudgeService) {
        this.nudgeService = nudgeService;
    }

    @GetMapping
    public ResponseEntity<GetNudges> getAllNudgesForUser(@AuthenticationPrincipal Jwt principal,
                                                        @RequestParam Optional<String> field,
                                                        @RequestParam Optional<Sort.Direction> direction) {
        logger.info("getAllNudgesForUser(principal: {}, field: {}, direction: {}): enter", principal.getSubject(), field, direction);
        GetNudges getNudges;
        if (field.isEmpty() || direction.isEmpty()) {
            getNudges = nudgeService.getAllNudgesForUser(principal.getSubject());
        } else {
            getNudges = nudgeService.getAllNudgesForUser(principal.getSubject(), Sort.by(direction.get(), field.get()));
        }

        logger.info("getAllNudgesForUser(): exit");
        return ResponseEntity.ok(getNudges);
    }

    @PostMapping
    public ResponseEntity<Void> createNudgeForUser(@AuthenticationPrincipal Jwt principal,
                                                   @RequestBody UpsertNudge createNudge) throws URISyntaxException {
        logger.info("createNudgeForUser(principal: {}, createNudge: {}): enter", principal.getSubject(), createNudge);
        var nudgeId = nudgeService.createNudgeForUser(principal, createNudge);
        var location = new URI("/nudges/" + nudgeId);

        logger.info("createNudgeForUser(): exit");
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNudgeForUser(@AuthenticationPrincipal Jwt principal, @PathVariable Long id,
                                                   @RequestBody UpsertNudge updateNudge) throws EntityMissingException {
        logger.info("updateNudgeForUser(principal: {}, id: {}, updateNudge: {}): enter", principal.getSubject(), id, updateNudge);
        nudgeService.updateNudgeForUser(id, principal.getSubject(), updateNudge);

        logger.info("updateNudgeForUser(): exit");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNudgeForUser(@AuthenticationPrincipal Jwt principal,
                                                    @PathVariable Long id) throws EntityMissingException {
        logger.info("deleteNudgeForUser(principal: {}, id: {}): enter", principal.getSubject(), id);
        nudgeService.deleteNudgeForUser(id, principal.getSubject());

        logger.info("deleteNudgeForUser(): exit");
        return ResponseEntity.noContent().build();
    }
}
