package com.travixa.services.platform;

import com.travixa.entity.platform_cms_support.CmsPage;
import com.travixa.repository.platform.CmsPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CmsContentService {

    private final CmsPageRepository cmsPageRepository;

    public CmsContentService(CmsPageRepository cmsPageRepository) {
        this.cmsPageRepository = cmsPageRepository;
    }

    public List<CmsPage> findAll() {
        return cmsPageRepository.findAll();
    }

    public Optional<CmsPage> findById(Long id) {
        return cmsPageRepository.findById(id);
    }

    @Transactional
    public CmsPage save(CmsPage cmsPage) {
        return cmsPageRepository.save(cmsPage);
    }

    @Transactional
    public void deleteById(Long id) {
        cmsPageRepository.deleteById(id);
    }
}
