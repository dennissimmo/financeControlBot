package com.denchik.demo.repository;


import com.denchik.demo.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)

public interface SourceRepository extends JpaRepository<Source,Long> {
    List<Source> findAll();
    Source getSourceByTypeSource (String typeSource);
}
