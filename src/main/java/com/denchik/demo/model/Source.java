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
    private String type_source;
    public Source() {

    }
    public Source(String type_source) {
        this.type_source = type_source;
    }

    public String getType_source() {
        return type_source;
    }

    public void setType_source(String type_source) {
        this.type_source = type_source;
    }
}
