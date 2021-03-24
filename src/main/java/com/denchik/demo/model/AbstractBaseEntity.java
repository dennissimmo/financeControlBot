package com.denchik.demo.repository;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@MappedSuperclass

@Access(AccessType.FIELD)

@Getter
@Setter

public class AbstractBaseEntity {
    public static final int START_SEQ = 100000;

    @Id
    @SequenceGenerator(name = "global_seq",sequenceName = "global_seq",allocationSize = 1,initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)

    protected Integer id;

    protected AbstractBaseEntity () {

    }
}
