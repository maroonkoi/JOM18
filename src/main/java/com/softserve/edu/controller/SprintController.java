package com.softserve.edu.controller;

import com.softserve.edu.dto.SprintResponce;
import com.softserve.edu.model.Sprint;
import com.softserve.edu.service.MarathonService;
import com.softserve.edu.service.SprintService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sprints")
@Data
@Slf4j
public class SprintController {

    private SprintService sprintService;
    private MarathonService marathonService;

    public SprintController(SprintService sprintService, MarathonService marathonService) {
        this.sprintService = sprintService;
        this.marathonService = marathonService;
    }

    @GetMapping("/marathon/{marathonId}")
    public List<SprintResponce> getAllSprintsOfSomeMarathon(@PathVariable long marathonId) {
        log.info("get all sprints ");
        List<Sprint> sprints = sprintService.getSprintsByMarathonId(marathonId);
        return sprints.stream().map(s -> new SprintResponce(s)).collect(Collectors.toList());
    }


    @GetMapping("/{sprintId}")
    public ResponseEntity<SprintResponce> getOneSprint(@PathVariable long sprintId) {
        Optional<Sprint> sprint = Optional.of(sprintService.getSprintById(sprintId));
        if(!sprint.isPresent()){
            log.error(String.format("sprint with id %d is not existed", sprintId));
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(new SprintResponce(sprint.get()));
    }

    @PreAuthorize("hasAuthority(MENTOR)")
    @PostMapping("/{marathonId}")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createSprint(@Valid @RequestBody Sprint sprint, @PathVariable long marathonId) {
        log.info("create sprint in marathon with id " + marathonId);
        return sprintService.addSprintToMarathon(sprint, marathonService.getMarathonById(marathonId));
    }

    @PreAuthorize("hasAuthority(MENTOR)")
    @PutMapping
    public ResponseEntity editSprint(@Valid @RequestBody Sprint sprintEntity) {
        Optional<Sprint> sprintInDB = Optional.of(sprintService.getSprintById(sprintEntity.getId()));
        if(!sprintInDB.isPresent()){
            log.error(String.format("trying to update sprint with unknown id"));
            return ResponseEntity.notFound().build();
        }
        Sprint sprint = sprintInDB.get();
        sprint.setTitle(sprintEntity.getTitle());
        sprint.setStartDate(sprintEntity.getStartDate());
        sprint.setEndDate(sprintEntity.getEndDate());
        sprintService.updateSprint(sprint);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority(MENTOR)")
    @DeleteMapping("/{sprintId}")
    public ResponseEntity deleteSprint(@PathVariable long sprintId) {
        log.info(String.format("delete sprint id = %d from marathone", sprintId));
        Optional<Sprint> sprint = Optional.of(sprintService.getSprintById(sprintId));
        if(sprint.isPresent()){
            sprintService.deleteById(sprintId);
        }
        return ResponseEntity.ok().build();
    }
}
