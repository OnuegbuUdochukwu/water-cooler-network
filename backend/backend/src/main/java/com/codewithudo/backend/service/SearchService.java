package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.SearchResultDto;
import com.codewithudo.backend.dto.UserDto;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Global search across all content types
    public SearchResultDto globalSearch(String query, int page, int size) {
        SearchResultDto result = new SearchResultDto();
        result.setQuery(query);
        result.setTotalResults(0);
        result.setUsers(new ArrayList<>());
        result.setLounges(new ArrayList<>());
        result.setMeetings(new ArrayList<>());
        result.setAnnouncements(new ArrayList<>());
        
        if (query == null || query.trim().isEmpty()) {
            return result;
        }
        
        // Search users with basic query
        SearchResultDto userResults = searchUsers(query, null, null, null, page, size);
        result.setUsers(userResults.getUsers());
        result.setTotalUsers(userResults.getTotalUsers());
        result.setTotalResults(userResults.getTotalUsers());
        
        // TODO: Add other search types (lounges, meetings, etc.)
        
        return result;
    }
    
    // Advanced user search with filters
    public SearchResultDto searchUsers(String query, String industry, String skills, 
                                      String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;
        
        if (hasFilters(industry, skills, location)) {
            users = userRepository.findUsersWithFilters(
                query != null ? "%" + query.toLowerCase() + "%" : "%",
                industry != null ? "%" + industry.toLowerCase() + "%" : "%",
                skills != null ? "%" + skills.toLowerCase() + "%" : "%",
                location != null ? "%" + location.toLowerCase() + "%" : "%",
                pageable
            );
        } else if (query != null && !query.trim().isEmpty()) {
            users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, pageable
            );
        } else {
            users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, pageable);
        }
        
        SearchResultDto result = new SearchResultDto();
        result.setQuery(query);
        result.setUsers(users.getContent().stream()
                .map(this::convertUserToDto)
                .collect(Collectors.toList()));
        result.setTotalUsers((int) users.getTotalElements());
        result.setTotalPages(users.getTotalPages());
        result.setCurrentPage(users.getNumber());
        result.setHasNext(users.hasNext());
        result.setHasPrevious(users.hasPrevious());
        
        return result;
    }
    
    // Smart search suggestions
    public List<String> getSearchSuggestions(String query, int limit) {
        List<String> suggestions = new ArrayList<>();
        
        if (query == null || query.length() < 2) {
            return suggestions;
        }
        
        // Get user name suggestions
        List<User> users = userRepository.findTop5ByNameContainingIgnoreCaseAndIsActiveTrue(query);
        suggestions.addAll(users.stream()
                .map(User::getName)
                .collect(Collectors.toList()));
        
        // Add industry suggestions
        List<String> industries = List.of(
            "Technology", "Healthcare", "Finance", "Education", "Marketing",
            "Sales", "Engineering", "Design", "Operations", "Consulting"
        );
        suggestions.addAll(industries.stream()
                .filter(industry -> industry.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList()));
        
        // Add skill suggestions
        List<String> skillSuggestions = List.of(
            "Java", "Python", "JavaScript", "React", "Angular", "Node.js",
            "Project Management", "Data Analysis", "Machine Learning",
            "Cloud Computing", "DevOps", "UI/UX Design"
        );
        suggestions.addAll(skillSuggestions.stream()
                .filter(skill -> skill.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList()));
        
        return suggestions.stream().distinct().limit(10).collect(Collectors.toList());
    }
    
    // Popular searches
    public List<String> getPopularSearches(int limit) {
        return List.of(
            "Software Engineer",
            "Product Manager",
            "Data Scientist",
            "UX Designer",
            "Marketing Manager",
            "Business Analyst",
            "DevOps Engineer",
            "Frontend Developer"
        );
    }
    
    // Recent searches for user
    public List<String> getRecentSearches(int limit) {
        // TODO: Implement search history storage and retrieval
        return List.of(
            "Java Developer",
            "Project Manager",
            "San Francisco"
        );
    }
    
    // Save search query for analytics
    public void saveSearchQuery(Long userId, String query, String type, int resultCount) {
        // TODO: Implement search analytics storage
        System.out.println("Search logged: User " + userId + " searched for '" + query + 
                          "' in " + type + " with " + resultCount + " results");
    }
    
    private boolean hasFilters(String industry, String skills, String location) {
        return (industry != null && !industry.trim().isEmpty()) ||
               (skills != null && !skills.trim().isEmpty()) ||
               (location != null && !location.trim().isEmpty());
    }
    
    private UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setIndustry(user.getIndustry());
        dto.setSkills(user.getSkills());
        dto.setInterests(user.getInterests());
        dto.setLinkedinUrl(user.getLinkedinUrl());
        dto.setRole(user.getRole().toString());
        dto.setCompanyId(user.getCompanyId());
        dto.setActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
