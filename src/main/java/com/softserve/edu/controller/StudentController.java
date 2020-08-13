package com.softserve.edu.controller;

import com.softserve.edu.config.JwtProvider;
import com.softserve.edu.dto.*;
import com.softserve.edu.model.*;
import com.softserve.edu.service.MarathonService;
import com.softserve.edu.service.RoleService;
import com.softserve.edu.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
@Data
@Slf4j
public class StudentController {
    private MarathonService marathonService;
    private UserService userService;
    private RoleService roleService;
    private JwtProvider jwtProvider;
    private Role studentRole;
    private final static String MENTOR = Roles.MENTOR.toString();

    public StudentController(MarathonService marathonService, UserService studentService, RoleService roleService, JwtProvider jwtProvider) {
        this.marathonService = marathonService;
        this.userService = studentService;
        this.roleService = roleService;
        this.jwtProvider = jwtProvider;
        this.studentRole = roleService.getAll().stream()
                .filter(r -> r.getName().equals(Roles.TRAINEE.toString())).findAny().orElse(null);
    }

    @GetMapping
    public List<StudentResponce> getAllStudents() {
        log.info("get all students ");
        List<User> students = userService.getAllByRoleName(Roles.TRAINEE.toString());
        return students.stream().map(st -> new StudentResponce(st)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponce> getStudent(@PathVariable long id) {
        log.info("get User with id " + id);
        Optional<User> student = userService.getUserById(id);
        if(!student.isPresent()){
            log.error(String.format("user with id %d is not existed", id));
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(new StudentResponce(student.get()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponce createStudent(@Valid @RequestBody User student) {
        log.info("create student");
        User user = new User();
        user.setFirstName(student.getFirstName());
        user.setLastName(student.getLastName());
        user.setEmail(student.getEmail());
        user.setPassword(student.getPassword());
        user.setRole(studentRole);
        User newStudent = userService.createOrUpdateUser(user);
        return new StudentResponce(newStudent);
    }

    @PutMapping
    public ResponseEntity editStudent(@RequestBody StudentRequest studentRequest) {
        Optional<User> studentInDB = userService.getUserById(studentRequest.getId());
        if(!studentInDB.isPresent()){
            log.error(String.format("trying to update student with unknown id"));
            return ResponseEntity.notFound().build();
        }
        User student = studentInDB.get();
        student.setFirstName(studentRequest.getFirstName());
        student.setLastName(studentRequest.getLastName());
        student.setEmail(studentRequest.getEmail());
        student.setPassword(studentRequest.getPassword());
        student.setRole(studentRole);
        userService.createOrUpdateUser(student);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable long id) {
        log.info("delete student with id " + id);
        Optional<User> student = userService.getUserById(id);
        if(!student.isPresent()){
            log.error(String.format("user with id %d is not existed", id));
            return ResponseEntity.notFound().build();
        }

        for (Marathon marathon : student.get().getMarathons()) {
            userService.deleteUserFromMarathon(student.get(), marathon);
        }
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{studentId}/add-to-marathon/{marathonId}")
    public ResponseEntity addStudentToMarathon(@PathVariable long studentId, @PathVariable long marathonId) {
        Optional<User> student = userService.getUserById(studentId);
        if(!student.isPresent()){
            log.error(String.format("user with id %d is not existed", studentId));
            return ResponseEntity.notFound().build();
        }
        Optional<Marathon> marathon = Optional.of(marathonService.getMarathonById(marathonId));
        if(!marathon.isPresent()){
            log.error(String.format("marathon with id %d is not existed", marathonId));
            return ResponseEntity.notFound().build();
        }
        userService.addUserToMarathon(student.get(), marathon.get());
        return ResponseEntity.ok().build();
    }
}
