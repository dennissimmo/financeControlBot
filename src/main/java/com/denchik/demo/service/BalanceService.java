package com.denchik.demo.service;

import com.denchik.demo.model.Balance;
import com.denchik.demo.model.User;
import com.denchik.demo.repository.BalanceRepository;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component

public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public Balance addBalance(Balance balance) {
        return balanceRepository.save(balance);
    }
    @Transactional
    public void saveBalance (Balance balance){
        balanceRepository.save(balance);
    }
}

