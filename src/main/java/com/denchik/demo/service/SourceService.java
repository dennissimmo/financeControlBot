package com.denchik.demo.service;

import com.denchik.demo.model.Source;
import com.denchik.demo.repository.SourceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component

public class SourceService {
    private SourceRepository sourceRepository;
    public SourceService (SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }
    @Transactional(readOnly = true)
    public Source getSourceByTypeSource (String sourceName) {
        return sourceRepository.getSourceByTypeSource(sourceName);
    }
    @Transactional
    public List<Source> getAllSources () {
        return sourceRepository.findAll();
    }
    @Transactional
    public Source addSource (Source source) {
        return sourceRepository.save(source);
    }
    @Transactional
    public void saveSource (Source source) {
        sourceRepository.save(source);
    }
}
