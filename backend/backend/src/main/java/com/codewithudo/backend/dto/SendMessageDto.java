package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.LoungeMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageDto {
    
    @NotNull(message = "Lounge ID is required")
    private Long loungeId;
    
    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String content;
    
    private LoungeMessage.MessageType messageType = LoungeMessage.MessageType.TEXT;
    
    private Long replyToMessageId;
}
