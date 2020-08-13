package com.softserve.edu.dto;

import com.softserve.edu.model.Roles;
import com.softserve.edu.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class StudentResponce {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String email;

    public StudentResponce(User student){
        this.id = student.getId();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.email = student.getEmail();
    }
}
