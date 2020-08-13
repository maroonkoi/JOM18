package com.softserve.edu.controller;

import com.softserve.edu.config.JwtProvider;
import com.softserve.edu.dto.*;

import com.softserve.edu.model.Role;
import com.softserve.edu.model.Roles;
import com.softserve.edu.model.User;
import com.softserve.edu.service.MarathonService;
import com.softserve.edu.service.RoleService;
import com.softserve.edu.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {

    private UserService userService;
    private RoleService roleService;
    private MarathonService marathonService;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private final static String MENTOR = Roles.MENTOR.toString();

    public UserController(UserService userService, RoleService roleService,
                          MarathonService marathonService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.roleService = roleService;
        this.marathonService = marathonService;
        this.jwtProvider = jwtProvider;
    }

    @Autowired
    @Qualifier("bCrypt")
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signin")
    public TokenResponse signIn(
            @RequestParam(value = "login", required = true)
                    String login,
            @RequestParam(value = "password", required = true)
                    String password) {
        log.info("**/signin userLogin = " + login);
        UserRequest userRequest = new UserRequest(login, password);
        UserResponce userResponce = userService.findByLoginAndPassword(userRequest);
        return new TokenResponse(jwtProvider.generateToken(userResponce.getLogin()));
    }

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public OperationResponce signUp() {
        return new OperationResponce(String.valueOf(true));
    }

    @PostMapping("/signup")
    public OperationResponce signUp(
            @RequestParam(value = "login", required = true)
                    String login,
            @RequestParam(value = "password", required = true)
                    String password) {
        log.info("**/signup userLogin = " + login);
        User user = new User();
        user.setEmail(login);
        user.setPassword(password);
        return new OperationResponce(String.valueOf(userService.createOrUpdateUser(user)));
    }
}
