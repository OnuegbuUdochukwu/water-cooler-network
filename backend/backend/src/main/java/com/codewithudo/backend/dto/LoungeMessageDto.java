package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.LoungeMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoungeMessageDto {
    
    private Long id;
    private Long loungeId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String content;
    private LoungeMessage.MessageType messageType;
    private Long replyToMessageId;
    private String replyToUserName;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
}
