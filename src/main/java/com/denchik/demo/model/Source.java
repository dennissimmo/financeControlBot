package com.denchik.demo.model;

import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "source_info")
public class Source extends AbstractBaseEntity{
    @Column(name = "type_source")
    private String typeSource;
    public Source() {

    }

    public Source(String typeSource) {
        this.typeSource = typeSource;
    }

    public String getTypeSource() {
        return typeSource;
    }

    public void setTypeSource(String typeSource) {
        this.typeSource = typeSource;
    }
}
