package com.softserve.edu.controller;

import com.softserve.edu.config.JwtProvider;
import com.softserve.edu.dto.MarathonRequest;
import com.softserve.edu.dto.TokenResponse;
import com.softserve.edu.dto.UserRequest;
import com.softserve.edu.dto.UserResponce;
import com.softserve.edu.exception.EntityNotFoundException;
import com.softserve.edu.model.Marathon;
import com.softserve.edu.model.Role;
import com.softserve.edu.model.Roles;
import com.softserve.edu.model.User;
import com.softserve.edu.service.MarathonService;
import com.softserve.edu.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.softserve.edu.config.JwtFilter.BEARER;

@RestController
@RequestMapping("/marathon")
@Data
@Slf4j
public class MarathonController {
    private MarathonService marathonService;
    private UserService studentService;
    private JwtProvider jwtProvider;
    private final static String MENTOR = Roles.MENTOR.toString();

    public MarathonController(MarathonService marathonService, UserService studentService, JwtProvider jwtProvider) {
        this.marathonService = marathonService;
        this.studentService = studentService;
        this.jwtProvider = jwtProvider;
    }

    @PreAuthorize("hasAuthority(MENTOR)")
    @PostMapping
    public ResponseEntity createMarathon(MarathonRequest marathonRequest) {
        log.info("Create new marathon");
        Marathon marathon = new Marathon();
        marathon.setTitle(marathonRequest.getTitle());
        marathon = marathonService.createOrUpdate(marathon);
        return marathon != null ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority(MENTOR)")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteMarathon(@PathVariable long id) {
        log.info("Delete marathon with id " + id);
        Marathon marathon = marathonService.getMarathonById(id);
        if (marathon != null) {
            marathonService.deleteMarathonById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Marathon> getAllMarathons(@RequestHeader("Authorization") String token) {
        log.info("Get marathon list");
        String login = jwtProvider.getLoginFromToken(token.substring(BEARER.length()));
        User user = studentService.findByLogin(login);
        Role userRole = user.getRole();
        if (userRole.getName().equals("MENTOR")) {
            return marathonService.getAll();
        } else {
            return marathonService.getAllByUsers(user);
        }
    }

    @GetMapping("/{id}")
    public Marathon readMarathon(@PathVariable long id) {
        log.info("Get info about marathon with id " + id);
        Marathon marathon = marathonService.getMarathonById(id);
        return marathon;
    }

    @PreAuthorize("hasAuthority(MENTOR)")
    @PutMapping("/{id}")
    public ResponseEntity editMarathon(@PathVariable long id, MarathonRequest marathonRequest) {
        log.info("Edit marathon with id " + id);
        Marathon marathon = marathonService.getMarathonById(id);
        marathon.setTitle(marathonRequest.getTitle());
        marathon = marathonService.createOrUpdate(marathon);
        return marathon != null ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }


    @PreAuthorize("hasAuthority(MENTOR)")
    @GetMapping("/{id}/students")
    public List<User> getStudentsOfSomeMarathon(@PathVariable("id") long marathonId) {
        log.info("Get student of marathon with id " + marathonId);
        List<User> studentList = studentService.getAllByMarathonId(marathonId).stream()
                .filter(u -> u.getRole().equals("TRAINEE")).collect(Collectors.toList());
        return studentList;
    }

}
