package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.SearchResultDto;
import com.codewithudo.backend.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:3000")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/global")
    public ResponseEntity<SearchResultDto> globalSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        SearchResultDto result = searchService.globalSearch(query, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<SearchResultDto> searchUsers(
            @RequestParam String query,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        SearchResultDto result = searchService.searchUsers(query, industry, skills, location, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        
        List<String> suggestions = searchService.getSearchSuggestions(query, limit);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<String>> getPopularSearches(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<String> popularSearches = searchService.getPopularSearches(limit);
        return ResponseEntity.ok(popularSearches);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<String>> getRecentSearches(
            @RequestParam(defaultValue = "5") int limit) {
        
        List<String> recentSearches = searchService.getRecentSearches(limit);
        return ResponseEntity.ok(recentSearches);
    }
}
