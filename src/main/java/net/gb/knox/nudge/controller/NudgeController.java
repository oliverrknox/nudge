package net.gb.knox.nudge.controller;

import net.gb.knox.nudge.domain.GetNudges;
import net.gb.knox.nudge.domain.UpsertNudge;
import net.gb.knox.nudge.exception.EntityMissingException;
import net.gb.knox.nudge.service.NudgeService;
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

    @Autowired
    public NudgeController(NudgeService nudgeService) {
        this.nudgeService = nudgeService;
    }

    @GetMapping
    public ResponseEntity<GetNudges> getAllNudgesForUser(@AuthenticationPrincipal Jwt principal,
                                                        @RequestParam Optional<String> field,
                                                        @RequestParam Optional<Sort.Direction> direction) {
        GetNudges getNudges;
        if (field.isEmpty() || direction.isEmpty()) {
            getNudges = nudgeService.getAllNudgesForUser(principal.getSubject());
        } else {
            getNudges = nudgeService.getAllNudgesForUser(principal.getSubject(), Sort.by(direction.get(), field.get()));
        }

        return ResponseEntity.ok(getNudges);
    }

    @PostMapping
    public ResponseEntity<Void> createNudgeForUser(@AuthenticationPrincipal Jwt principal,
                                                   @RequestBody UpsertNudge createNudge) throws URISyntaxException {
        var nudgeId = nudgeService.createNudgeForUser(principal, createNudge);
        var location = new URI("/nudges/" + nudgeId);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNudgeForUser(@AuthenticationPrincipal Jwt principal, @PathVariable Long id,
                                                   @RequestBody UpsertNudge updateNudge) throws EntityMissingException {
        nudgeService.updateNudgeForUser(id, principal.getSubject(), updateNudge);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNudgeForUser(@AuthenticationPrincipal Jwt principal,
                                                    @PathVariable Long id) throws EntityMissingException {
        nudgeService.deleteNudgeForUser(id, principal.getSubject());
        return ResponseEntity.noContent().build();
    }
}
