package com.quintus.labs.datingapp.Profile;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private List<String> images;
    private String about;
    private String jobDescription;
    private String company;
    private String school;
    private Gender gender;


    public enum Gender{
        MALE, FEMALE
    }

}
