package com.denchik.demo.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "operation")
public class Operation extends AbstractBaseEntity {
    @Column(name = "amount")
    private Double amount;
    @Column(name = "note",nullable = true)
    private String note;
    @OneToOne()
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;
    @OneToOne()
    @JoinColumn(name = "source_id",nullable = false)
    private Source source;
    @OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "person_id",nullable = false)
    private Person person;
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "type_operation_id",nullable = false)
    private TypeOperation typeOperation;
    @Column(name = "is_regular")
    private boolean is_regular;
    @Column(name = "create_at",nullable = false)
    private Date create_at;
    @Column(name = "raw_text",nullable = false)
    private String raw_text;
    public Operation () {

    }
    public Operation(Double amount, String note, Category category, Source source, Person person, TypeOperation typeOperation, boolean is_regular, Date create_at, String raw_text) {
        this.amount = amount;
        this.note = note;
        this.category = category;
        this.source = source;
        this.person = person;
        this.typeOperation = typeOperation;
        this.is_regular = is_regular;
        this.create_at = create_at;
        this.raw_text = raw_text;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public TypeOperation getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(TypeOperation typeOperation) {
        this.typeOperation = typeOperation;
    }

    public boolean isIs_regular() {
        return is_regular;
    }

    public void setIs_regular(boolean is_regular) {
        this.is_regular = is_regular;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public String getRaw_text() {
        return raw_text;
    }

    public void setRaw_text(String raw_text) {
        this.raw_text = raw_text;
    }
}
