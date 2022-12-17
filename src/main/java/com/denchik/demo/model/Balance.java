package com.denchik.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "balance")
public class Balance extends AbstractBaseEntity {
    private Double amount;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_info_id",nullable = false)
    private Source source;
    public Balance () {

    }

    public Balance(Double amount, Source source) {
        this.amount = amount;
        this.source = source;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void upBalance (double amount) {
        this.amount += amount;
    }
    public void downBalance (double amount) {
        this.amount -= amount;
    }
}
