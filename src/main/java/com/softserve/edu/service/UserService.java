package com.softserve.edu.service;

import com.softserve.edu.model.Marathon;
import com.softserve.edu.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.softserve.edu.dto.UserRequest;
import com.softserve.edu.dto.UserResponce;
import java.util.List;
import java.util.Optional;


public interface UserService {
    List<User> getAll();
    List<User> getAllByRoleName(String roleName);
    Optional<User> getUserById(Long id);
    UserDetails loadUserByUsername(String username);
    User createOrUpdateUser( User user);
    void deleteUserById(Long id);
    boolean addUserToMarathon(User user, Marathon marathon);
    boolean deleteUserFromMarathon(User user, Marathon marathon);
    String getExpirationLocalDate();
    UserResponce findByLoginAndPassword(UserRequest userRequest);
    User findByLogin(String login);
    List<User> getAllByMarathonId(Long marathonId);
}
