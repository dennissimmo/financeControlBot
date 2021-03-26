package com.denchik.demo.model;

import com.denchik.demo.handlers.BotState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.naming.Name;
import javax.persistence.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
public class User extends AbstractBaseEntity{
    private Integer chat_id;
    private String first_name;
    private String last_name;
    private String username;
    private String language_code;
    private BotState bot_state;

    public User(String first_name, String last_name, String username, String language_code, BotState bot_state) {
        this.chat_id = chat_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.language_code = language_code;
        this.bot_state = bot_state;
    }
    public User () {

    }
    public Integer getChat_id() {
        return chat_id;
    }

    public void setChat_id(Integer chat_id) {
        this.chat_id = chat_id;
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

    public BotState getBot_state() {
        return bot_state;
    }

    public void setBot_state(BotState bot_state) {
        this.bot_state = bot_state;
    }
}
