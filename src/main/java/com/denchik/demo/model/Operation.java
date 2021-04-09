package com.denchik.demo.model;

import javax.persistence.*;
import java.sql.Timestamp;
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
    /*@OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "person_id",nullable = false)
    private Person person;*/
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "type_operation_id",nullable = false)
    private TypeOperation typeOperation;
    @Column(name = "is_regular")
    private boolean is_regular;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at",nullable = false)
    private Date createAt;
    @Column(name = "raw_text",nullable = false)
    private String raw_text;
    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "user_id")
    private User user;
    @Override
    public String toString () {
        return "Amount : " + amount + " User : " + user.getFirst_name() + " Source : " + source.getTypeSource() + " Type : " + typeOperation.getName();
    }
    public Operation () {

    }
    public Operation (double amount,String raw_text,Source source,User user) {
        this.amount = amount;
        this.raw_text = raw_text;
        this.source = source;
        this.user = user;
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


    public String getRaw_text() {
        return raw_text;
    }

    public void setRaw_text(String raw_text) {
        this.raw_text = raw_text;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
    public static boolean isExpense (Operation operation) {
        if (operation.getTypeOperation().getName().equals("EXPENSE")) {
            return true;
        } else {
            return false;
        }
    }
}
