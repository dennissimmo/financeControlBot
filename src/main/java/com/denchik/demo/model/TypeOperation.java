package com.denchik.demo.model;

import org.checkerframework.checker.units.qual.A;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "type_operation")
public class TypeOperation extends AbstractBaseEntity {
        private String name;

        public TypeOperation () {

        }
        public TypeOperation(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
}
