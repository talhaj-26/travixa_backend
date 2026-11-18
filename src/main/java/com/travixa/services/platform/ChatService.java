package com.travixa.services.platform;

import com.travixa.entity.platform_cms_support.ChatConversation;
import com.travixa.repository.platform.ChatConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private final ChatConversationRepository chatConversationRepository;

    public ChatService(ChatConversationRepository chatConversationRepository) {
        this.chatConversationRepository = chatConversationRepository;
    }

    public List<ChatConversation> findAll() {
        return chatConversationRepository.findAll();
    }

    public Optional<ChatConversation> findById(Long id) {
        return chatConversationRepository.findById(id);
    }

    @Transactional
    public ChatConversation save(ChatConversation chatConversation) {
        return chatConversationRepository.save(chatConversation);
    }
}
