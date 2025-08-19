package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Lounge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateLoungeDto {
    
    @NotBlank(message = "Lounge title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Topic is required")
    @Size(max = 100, message = "Topic cannot exceed 100 characters")
    private String topic;
    
    private String category;
    
    private List<String> tags;
    
    private Lounge.Visibility visibility = Lounge.Visibility.PUBLIC;
    
    private Integer maxParticipants;
}
