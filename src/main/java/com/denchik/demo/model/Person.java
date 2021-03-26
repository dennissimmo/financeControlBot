package com.denchik.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "person")
public class Person extends AbstractBaseEntity{
    private String name;
    @OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "balance_id",nullable = false)
    private Balance balance;
    public Person () {

    }
    public Person(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
