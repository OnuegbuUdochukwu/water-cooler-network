package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.MentorshipProgramDTO;
import com.codewithudo.backend.entity.MentorshipProgram;
import com.codewithudo.backend.entity.MentorshipRelationship;
import com.codewithudo.backend.entity.MentorshipSession;
import com.codewithudo.backend.service.MentorshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentorship")
@CrossOrigin(origins = "*")
public class MentorshipController {

    @Autowired
    private MentorshipService mentorshipService;

    // Program Management
    @GetMapping("/programs/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<List<MentorshipProgramDTO>> getActivePrograms(@PathVariable Long companyId) {
        try {
            List<MentorshipProgramDTO> programs = mentorshipService.getActivePrograms(companyId);
            return ResponseEntity.ok(programs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/programs/{programId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<MentorshipProgramDTO> getProgramById(@PathVariable Long programId) {
        try {
            MentorshipProgramDTO program = mentorshipService.getProgramById(programId);
            if (program != null) {
                return ResponseEntity.ok(program);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/programs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<MentorshipProgramDTO> createProgram(@RequestBody MentorshipProgramDTO programDTO) {
        try {
            MentorshipProgramDTO createdProgram = mentorshipService.createProgram(programDTO);
            return ResponseEntity.ok(createdProgram);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/programs/{programId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<MentorshipProgramDTO> updateProgram(
            @PathVariable Long programId, @RequestBody MentorshipProgramDTO programDTO) {
        try {
            MentorshipProgramDTO updatedProgram = mentorshipService.updateProgram(programId, programDTO);
            if (updatedProgram != null) {
                return ResponseEntity.ok(updatedProgram);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/programs/{programId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<String> deleteProgram(@PathVariable Long programId) {
        try {
            Boolean deleted = mentorshipService.deleteProgram(programId);
            if (deleted) {
                return ResponseEntity.ok("Program deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete program");
        }
    }

    // Relationship Management
    @GetMapping("/relationships/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<List<MentorshipRelationship>> getUserRelationships(@PathVariable Long userId) {
        try {
            List<MentorshipRelationship> relationships = mentorshipService.getUserRelationships(userId);
            return ResponseEntity.ok(relationships);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/relationships/mentor/{mentorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or #mentorId == authentication.principal.id")
    public ResponseEntity<List<MentorshipRelationship>> getMentorRelationships(@PathVariable Long mentorId) {
        try {
            List<MentorshipRelationship> relationships = mentorshipService.getMentorRelationships(mentorId);
            return ResponseEntity.ok(relationships);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/relationships/mentee/{menteeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or #menteeId == authentication.principal.id")
    public ResponseEntity<List<MentorshipRelationship>> getMenteeRelationships(@PathVariable Long menteeId) {
        try {
            List<MentorshipRelationship> relationships = mentorshipService.getMenteeRelationships(menteeId);
            return ResponseEntity.ok(relationships);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/relationships")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<MentorshipRelationship> createRelationship(
            @RequestParam Long programId,
            @RequestParam Long mentorId,
            @RequestParam Long menteeId,
            @RequestParam String goals) {
        try {
            MentorshipRelationship relationship = mentorshipService.createRelationship(programId, mentorId, menteeId, goals);
            if (relationship != null) {
                return ResponseEntity.ok(relationship);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/relationships/{relationshipId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<MentorshipRelationship> updateRelationshipStatus(
            @PathVariable Long relationshipId,
            @RequestParam MentorshipRelationship.Status status) {
        try {
            MentorshipRelationship relationship = mentorshipService.updateRelationshipStatus(relationshipId, status);
            if (relationship != null) {
                return ResponseEntity.ok(relationship);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/relationships/{relationshipId}/feedback")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<MentorshipRelationship> addFeedback(
            @PathVariable Long relationshipId,
            @RequestParam Long userId,
            @RequestParam String feedback,
            @RequestParam Integer rating) {
        try {
            MentorshipRelationship relationship = mentorshipService.addFeedback(relationshipId, userId, feedback, rating);
            if (relationship != null) {
                return ResponseEntity.ok(relationship);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Session Management
    @GetMapping("/sessions/relationship/{relationshipId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<List<MentorshipSession>> getRelationshipSessions(@PathVariable Long relationshipId) {
        try {
            List<MentorshipSession> sessions = mentorshipService.getRelationshipSessions(relationshipId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<MentorshipSession> createSession(
            @RequestParam Long relationshipId,
            @RequestBody MentorshipService.MentorshipSessionDTO sessionDTO) {
        try {
            MentorshipSession session = mentorshipService.createSession(relationshipId, sessionDTO);
            if (session != null) {
                return ResponseEntity.ok(session);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/sessions/{sessionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<MentorshipSession> updateSession(
            @PathVariable Long sessionId,
            @RequestBody MentorshipService.MentorshipSessionDTO sessionDTO) {
        try {
            MentorshipSession session = mentorshipService.updateSession(sessionId, sessionDTO);
            if (session != null) {
                return ResponseEntity.ok(session);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/sessions/{sessionId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<MentorshipSession> updateSessionStatus(
            @PathVariable Long sessionId,
            @RequestParam MentorshipSession.Status status) {
        try {
            MentorshipSession session = mentorshipService.updateSessionStatus(sessionId, status);
            if (session != null) {
                return ResponseEntity.ok(session);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/sessions/{sessionId}/notes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<MentorshipSession> addSessionNotes(
            @PathVariable Long sessionId,
            @RequestParam Long userId,
            @RequestParam String notes,
            @RequestParam String noteType) {
        try {
            MentorshipSession session = mentorshipService.addSessionNotes(sessionId, userId, notes, noteType);
            if (session != null) {
                return ResponseEntity.ok(session);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Matching
    @GetMapping("/matching/mentors")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<List<Map<String, Object>>> findMentorsForMentee(
            @RequestParam Long menteeId,
            @RequestParam Long programId) {
        try {
            List<Map<String, Object>> mentors = mentorshipService.findMentorsForMentee(menteeId, programId);
            return ResponseEntity.ok(mentors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/matching/mentees")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER') or hasRole('USER')")
    public ResponseEntity<List<Map<String, Object>>> findMenteesForMentor(
            @RequestParam Long mentorId,
            @RequestParam Long programId) {
        try {
            List<Map<String, Object>> mentees = mentorshipService.findMenteesForMentor(mentorId, programId);
            return ResponseEntity.ok(mentees);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
