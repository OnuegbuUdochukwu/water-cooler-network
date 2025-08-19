package com.codewithudo.backend.dto;

import java.util.List;

public class SearchResultDto {
    
    private String query;
    private int totalResults;
    private int totalUsers;
    private int totalPages;
    private int currentPage;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<UserDto> users;
    private List<Object> lounges; // TODO: Replace with LoungeDto when available
    private List<Object> meetings; // TODO: Replace with MeetingDto when available
    private List<Object> announcements; // TODO: Replace with AnnouncementDto when available
    
    // Constructors
    public SearchResultDto() {}
    
    public SearchResultDto(String query, int totalResults, List<UserDto> users, 
                          List<Object> lounges, List<Object> meetings, List<Object> announcements) {
        this.query = query;
        this.totalResults = totalResults;
        this.users = users;
        this.lounges = lounges;
        this.meetings = meetings;
        this.announcements = announcements;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public int getTotalResults() {
        return totalResults;
    }
    
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
    
    public List<UserDto> getUsers() {
        return users;
    }
    
    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
    
    public List<Object> getLounges() {
        return lounges;
    }
    
    public void setLounges(List<Object> lounges) {
        this.lounges = lounges;
    }
    
    public List<Object> getMeetings() {
        return meetings;
    }
    
    public void setMeetings(List<Object> meetings) {
        this.meetings = meetings;
    }
    
    public List<Object> getAnnouncements() {
        return announcements;
    }
    
    public void setAnnouncements(List<Object> announcements) {
        this.announcements = announcements;
    }
    
    public int getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
