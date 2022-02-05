package com.denchik.demo.service;


import com.denchik.demo.model.*;
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
    public List<Operation> findLastOperationsUser (int user_id, int limit, int year, int month) {
        return operationRepository.findLastUserOperations(user_id,limit,year,month);
    }
    public Operation getLastOperationByCreateDate () {
        return operationRepository.findTopByOrderByCreateAt();
    }
    @Transactional(readOnly = true)
    public Operation getLastOperationByID () {
        return operationRepository.findTopByOrderById();
    }
    @Transactional(readOnly = true)
    public List<Operation> getUserOperations (User user) {
        return operationRepository.findOperationByUserAndCategoryNotNull(user);
    }
    @Transactional(readOnly = true)
    public Operation findOperationById (Integer id) {
        return operationRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public List<Operation> findAllOperationByTypeCategory (TypeOperation typeOperation, User user) {
        return  operationRepository.findOperationByTypeOperationAndUserOrderByAmount(typeOperation,user);
    }
    @Transactional(readOnly = true)
    public List<Operation> getUserOperationsOrdered (User user) {
        return operationRepository.findOperationsByUserAndCategoryNotNullOrderByCreateAt(user);
    }
    @Transactional(readOnly = true)
    public List<Operation> findOperationByCategoryAndUser (Category category,User user) {
        return operationRepository.findOperationsByCategoryAndUser(category,user);
    }
    @Transactional(readOnly = true)
    public List<Operation> getOperationByDay () {
        return operationRepository.findOperationByDate();
    }
    @Transactional(readOnly = true)
    public double sumAmountOperationsByTypeOperation (TypeOperation typeOperation,User user,int month, int year) {
        return  operationRepository.sumAmountOperationsForOperationType(typeOperation,user,month,year);
    }

    @Transactional(readOnly = true)
    public double sumAmountOperationsByCategory (Category category,User user) {
        return  operationRepository.sumAmountOperationsForCategory(category,user);
    }
    @Transactional(readOnly = true)
    public List<Operation> getOperationPerNumberMonth (int numberOfMonth,int user_id, int year) {
        return operationRepository.getOperationByNumberMonthAndYear(numberOfMonth,user_id,year);
    }
    public List<Operation> getSumOperationByCategoryPerMonth (int userID) {
        return operationRepository.getSumOperationByCategoryPerMonth(userID);
    }
    @Transactional(readOnly = true)
    public List<Operation> findOperationsByUser (User user) {
        return operationRepository.findOperationByUserAndCategoryNotNull(user);
    }
    @Transactional
    public void deleteOperation (Operation operation) {
        operationRepository.delete(operation);
    }
    @Transactional
    public void deleteAllOperationByUserID (Integer userID) {operationRepository.deleteAllByUserId(userID);}
}
