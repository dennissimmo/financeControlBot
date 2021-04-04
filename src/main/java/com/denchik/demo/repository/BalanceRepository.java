package com.denchik.demo.repository;

import com.denchik.demo.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)

public interface BalanceRepository extends JpaRepository<Balance,Long> {
    List<Balance> findAllByOrderByAmountAsc();
}
