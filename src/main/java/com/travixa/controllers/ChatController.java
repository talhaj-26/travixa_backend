package com.travixa.controllers;

import com.travixa.dto.support_platform.ChatConversationResponse;
import com.travixa.entity.platform_cms_support.ChatConversation;
import com.travixa.services.platform.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/chat-conversations")
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ChatConversationResponse> findAll() {
        return chatService.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ChatConversationResponse findById(@PathVariable Long id) {
        return chatService.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
    }

    private ChatConversationResponse toResponse(ChatConversation conversation) {
        return new ChatConversationResponse(conversation.getId(), conversation.getCustomer().getUserName(),
                conversation.getAssignedTo() == null ? null : conversation.getAssignedTo().getUserName(),
                conversation.getSubject(), conversation.getStatus(), conversation.getStartedAt(),
                conversation.getLastMessageAt());
    }
}
