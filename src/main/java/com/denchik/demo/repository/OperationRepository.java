package com.denchik.demo.repository;

import com.denchik.demo.model.Category;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface OperationRepository extends JpaRepository<Operation,Long> {
    List<Operation> findAllByCategory (Category category);
    List<Operation> findOperationByUser(User user);
    Operation findTopByOrderByCreateAt ();
    Operation findTopByOrderById ();
    Operation findById (Integer id);
}
