package com.travixa.repository.platform;

import com.travixa.entity.platform_cms_support.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
}
