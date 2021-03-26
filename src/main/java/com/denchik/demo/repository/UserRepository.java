package com.denchik.demo.repository;

import com.denchik.demo.model.AbstractBaseEntity;
import com.denchik.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findAllByUsername (String username);
}
