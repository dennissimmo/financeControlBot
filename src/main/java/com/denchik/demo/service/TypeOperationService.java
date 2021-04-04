package com.denchik.demo.service;

import com.denchik.demo.model.TypeCategory;
import com.denchik.demo.model.TypeOperation;
import com.denchik.demo.repository.TypeOperationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TypeOperationService {
    private TypeOperationRepository typeOperationRepository;

    public TypeOperationService(TypeOperationRepository typeOperationRepository) {
        this.typeOperationRepository = typeOperationRepository;
    }
    @Transactional(readOnly = true)
    public TypeOperation getTypeByName (String name) {
        return typeOperationRepository.findTypeOperationByNameIgnoreCase(name);
    }
}
