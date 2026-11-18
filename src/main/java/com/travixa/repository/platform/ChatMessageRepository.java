package com.travixa.repository.platform;

import com.travixa.entity.platform_cms_support.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
