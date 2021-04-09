package com.denchik.demo.model;

import com.denchik.demo.bot.BotState;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends AbstractBaseEntity{
    @Column(name = "chat_id")
    private Long chatId;
    private String first_name;
    private String last_name;
    private String username;
    private String language_code;
    @Column(name = "bot_state")
    private Integer state_id;
    @ManyToOne
    @JoinColumn(name = "balance_id",nullable = false)
    private Balance balance;
    public User () {

    }
    public User(Long chatId, String first_name, String last_name) {
        this.chatId = chatId;
        this.first_name = first_name;
        this.last_name = last_name;
    }
    public User(Long chatId, String first_name, String last_name,Integer bot_state) {
        this.chatId = chatId;
        this.first_name = first_name;
        this.last_name = last_name;
    }
    public User(Long chatId, String first_name, String last_name, String username,Integer state_id) {
        this.chatId = chatId;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.state_id = state_id;
    }
    public User(Long chatId, String first_name, String last_name, String username, String language_code, Integer state_id) {
        this.chatId = chatId;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.language_code = language_code;
        this.state_id = state_id;
    }

    public Long getChat_id() {
        return chatId;
    }

    public void setChat_id(Long chat_id) {
        this.chatId = chat_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    public Integer getState_id() {
        return state_id;
    }

    public void setState_id(BotState botState) {
        this.state_id = botState.ordinal();
    }

    @Override
    public String toString () {
        return "user id : " + id +
                " chat_id : " + chatId +
                " user_name : " + username +
                " fist_name : " + first_name +
                " bot_state : " + BotState.getBotStateById(state_id);
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }
}
