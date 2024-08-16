package net.gb.knox.nudge.controller;

import net.gb.knox.nudge.domain.GetNudges;
import net.gb.knox.nudge.domain.UpsertNudge;
import net.gb.knox.nudge.exception.EntityMissingException;
import net.gb.knox.nudge.service.NudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<GetNudges> getAllNudgesForUser(Authentication authentication,
                                                        @RequestParam Optional<String> field,
                                                        @RequestParam Optional<Sort.Direction> direction) {
        GetNudges getNudges;
        if (field.isEmpty() || direction.isEmpty()) {
            getNudges = nudgeService.getAllNudgesForUser(authentication.getName());
        } else {
            getNudges = nudgeService.getAllNudgesForUser(authentication.getName(), Sort.by(direction.get(), field.get()));
        }

        return ResponseEntity.ok(getNudges);
    }

    @PostMapping
    public ResponseEntity<Void> createNudgeForUser(Authentication authentication,
                                                   @RequestBody UpsertNudge createNudge) throws URISyntaxException {
        var nudgeId = nudgeService.createNudgeForUser(authentication.getName(), createNudge);
        var location = new URI("/nudges/" + nudgeId);

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNudgeForUser(Authentication authentication, @PathVariable Long id,
                                                   @RequestBody UpsertNudge updateNudge) throws EntityMissingException {
        nudgeService.updateNudgeForUser(id, authentication.getName(), updateNudge);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNudgeForUser(Authentication authentication,
                                                    @PathVariable Long id) throws EntityMissingException {
        nudgeService.deleteNudgeForUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
