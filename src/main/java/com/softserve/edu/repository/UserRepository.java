package com.softserve.edu.repository;


import com.softserve.edu.model.Progress;
import com.softserve.edu.model.Role;
import com.softserve.edu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users where email =?1", nativeQuery = true)
    User getUserByEmail(String email);

    @Query(value = "select * from users where role_id = (select roles.id from roles where roles.name=?1)", nativeQuery = true)
    List<User> getAllByRoleName(String roleName);

    @Query(value = "select * from users where id = (SELECT user_id from marathon_user where marathon_id =?1)", nativeQuery = true)
    List<User> getAllByMarathonId(Long marathonId);
}
