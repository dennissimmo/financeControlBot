package com.denchik.demo.service;


import com.denchik.demo.model.Operation;
import com.denchik.demo.model.User;
import com.denchik.demo.repository.OperationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component

public class OperationService {
    private OperationRepository operationRepository;

    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Transactional
    public Operation addOperation (Operation operation) {
        return operationRepository.save(operation);
    }
    @Transactional
    public void saveOperation (Operation operation) {
       operationRepository.save(operation);
    }
    @Transactional(readOnly = true)
    public Operation getLastOperationByCreateDate () {
        return operationRepository.findTopByOrderByCreateAt();
    }
    @Transactional(readOnly = true)
    public Operation getLastOperationByID () {
        return operationRepository.findTopByOrderById();
    }
    @Transactional(readOnly = true)
    public List<Operation> getUserOperations (User user) {
        return operationRepository.findOperationByUser(user);
    }
    @Transactional(readOnly = true)
    public Operation findOperationById (Integer id) {
        return operationRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public List<Operation> findOperationsByUser (User user) {
        return operationRepository.findOperationByUser(user);
    }
    @Transactional
    public void deleteOperation (Operation operation) {
        operationRepository.delete(operation);
    }

}
