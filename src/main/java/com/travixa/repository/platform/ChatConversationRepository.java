package com.travixa.repository.platform;

import com.travixa.entity.platform_cms_support.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
}
