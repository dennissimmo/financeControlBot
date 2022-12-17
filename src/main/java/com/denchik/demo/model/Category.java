package com.denchik.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "category")
@NoArgsConstructor
@AllArgsConstructor
public class Category extends AbstractBaseEntity implements Serializable {
    @Column(name = "name",nullable = true)
    private String name;
    @OneToOne
    @JoinColumn(name = "type_category_id",nullable = false)
    private TypeCategory typeCategory;
    @Column(name = "locale",nullable = false)
    private String locale;
    @Override
    public String toString () {
        return "Id: " + getId() + "Name: " + getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeCategory getTypeCategory() {
        return typeCategory;
    }

    public void setTypeCategory(TypeCategory typeCategory) {
        this.typeCategory = typeCategory;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
