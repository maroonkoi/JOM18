package com.softserve.edu.repository;

import com.softserve.edu.model.Marathon;
import com.softserve.edu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarathonRepository extends JpaRepository<Marathon, Long> {
    List<Marathon> getAllByUsers(User user);
}
