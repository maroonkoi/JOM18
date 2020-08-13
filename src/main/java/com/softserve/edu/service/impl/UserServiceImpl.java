package com.softserve.edu.service.impl;

import com.softserve.edu.model.Marathon;
import com.softserve.edu.model.User;
import com.softserve.edu.repository.MarathonRepository;
import com.softserve.edu.repository.UserRepository;
import com.softserve.edu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.BiFunction;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.softserve.edu.config.CustomUserDetails;
import com.softserve.edu.dto.UserRequest;
import com.softserve.edu.dto.UserResponce;
import com.softserve.edu.repository.RoleRepository;


@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final MarathonRepository marathonRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           MarathonRepository marathonRepository) {
        this.userRepository = userRepository;
        this.marathonRepository = marathonRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? new ArrayList<>() : users;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createOrUpdateUser(User entity) {
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        BiFunction<User, User, User> saveNewUser = (user1, user2) -> {
            user2.setEmail(user1.getEmail());
            user2.setFirstName(user1.getFirstName());
            user2.setLastName(user1.getLastName());
            user2.setRole(user1.getRole());
            user2.setPassword(user1.getPassword());
            user2 = userRepository.save(user2);
            return user2;
        };

        if (entity.getId() != null) {
            Optional<User> user = userRepository.findById(entity.getId());
            if (user.isPresent()) {
                return saveNewUser.apply(entity, user.get());
            }
        }
        if (!entity.getEmail().isEmpty()) {
            Optional<User> user = Optional.ofNullable(userRepository.getUserByEmail(entity.getEmail()));
            if (user.isPresent()) {
                return saveNewUser.apply(entity, user.get());
            }
        }

        entity = userRepository.save(entity);
        return entity;
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean addUserToMarathon(User user, Marathon marathon) {
        User userEntity = userRepository.getOne(user.getId());
        Marathon marathonEntity = marathonRepository.getOne(marathon.getId());
        marathonEntity.getUsers().add(userEntity);
        return marathonRepository.save(marathonEntity) != null;
    }

    @Override
    public boolean deleteUserFromMarathon(User user, Marathon marathon) {
        User userEntity = userRepository.getOne(user.getId());
        Marathon marathonEntity = marathonRepository.getOne(marathon.getId());
        marathonEntity.getUsers().remove(userEntity);
        return marathonRepository.save(marathonEntity) != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not Found!");
        }
        return user;
    }

    public User findByLogin(String login) {
        return userRepository.getUserByEmail(login);
    }

    public UserResponce findByLoginAndPassword(UserRequest userRequest) {
        UserResponce result = null;
        User user = userRepository.getUserByEmail(userRequest.getLogin());
        if ((user != null)
                && (passwordEncoder.matches(userRequest.getPassword(),
                user.getPassword()))) {
            result = new UserResponce();
            result.setLogin(userRequest.getLogin());
            result.setRolename(user.getRole().getName());
        }
        return result;
    }

    public String getExpirationLocalDate() {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime localDate = customUserDetails.getExpirationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'at' hh:mm");
        return localDate.format(formatter);
    }

    public List<User> getAllByRoleName(String roleName) {
        return userRepository.getAllByRoleName(roleName);
    }

    public List<User> getAllByMarathonId(Long marathonId){
        return userRepository.getAllByMarathonId(marathonId);
    }
}
