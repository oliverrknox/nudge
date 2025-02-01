package io.oliverknox.nudge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.oliverknox.nudge.domain.GetNudges;
import io.oliverknox.nudge.domain.UpsertNudge;
import io.oliverknox.nudge.exception.EntityMissingException;
import io.oliverknox.nudge.service.NudgeService;
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
@ApiResponses({@ApiResponse(responseCode = "400", ref = "badRequest"), @ApiResponse(responseCode = "401", ref = "unauthenticated"),
        @ApiResponse(responseCode = "403", ref = "forbidden"), @ApiResponse(responseCode = "404", ref = "notFound")})
public class NudgeController {

    private final NudgeService nudgeService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public NudgeController(NudgeService nudgeService) {
        this.nudgeService = nudgeService;
    }

    @GetMapping
    @Operation(summary = "Get all nudges for user", description = "Gets all nudges for the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "Got all nudges for user", content = @Content(schema = @Schema(implementation = GetNudges.class)))
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
    @Operation(summary = "Create nudge for user", description = "Creates a new nudge and registers background tasks " +
            "for the currently authenticated user.")
    @ApiResponse(responseCode = "201", description = "Created nudge")
    public ResponseEntity<Void> createNudgeForUser(@AuthenticationPrincipal Jwt principal,
                                                   @RequestBody UpsertNudge createNudge) throws URISyntaxException {
        logger.info("createNudgeForUser(principal: {}, createNudge: {}): enter", principal.getSubject(), createNudge);
        var nudgeId = nudgeService.createNudgeForUser(principal, createNudge);
        var location = new URI("/nudges/" + nudgeId);

        logger.info("createNudgeForUser(): exit");
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update nudge for user", description = "Updates an existing nudge and re-registers any " +
            "background tasks for the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "Updated nudge")
    public ResponseEntity<Void> updateNudgeForUser(@AuthenticationPrincipal Jwt principal, @PathVariable Long id,
                                                   @RequestBody UpsertNudge updateNudge) throws EntityMissingException {
        logger.info("updateNudgeForUser(principal: {}, id: {}, updateNudge: {}): enter", principal.getSubject(), id, updateNudge);
        nudgeService.updateNudgeForUser(id, principal.getSubject(), updateNudge);

        logger.info("updateNudgeForUser(): exit");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete nudge for user", description = "Deletes a nudge for the currently authenticated user " +
            "and cleans up any background tasks.")
    @ApiResponse(responseCode = "204", description = "Deleted nudge.")
    public ResponseEntity<Void> deleteNudgeForUser(@AuthenticationPrincipal Jwt principal,
                                                    @PathVariable Long id) throws EntityMissingException {
        logger.info("deleteNudgeForUser(principal: {}, id: {}): enter", principal.getSubject(), id);
        nudgeService.deleteNudgeForUser(id, principal.getSubject());

        logger.info("deleteNudgeForUser(): exit");
        return ResponseEntity.noContent().build();
    }
}
