package com.softserve.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Data
public class MarathonRequest {

    @NotEmpty
    private String title;
}
