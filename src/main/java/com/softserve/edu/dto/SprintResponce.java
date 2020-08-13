package com.softserve.edu.dto;

import com.softserve.edu.model.Sprint;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
public class SprintResponce {

    @NotEmpty
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private LocalDate startDate;

    @NotEmpty
    private LocalDate endDate;

    public SprintResponce(Sprint sprint){
        this.id = sprint.getId();
        this.title = sprint.getTitle();
        this.startDate = sprint.getStartDate();
        this.endDate = sprint.getEndDate();
    }
}
