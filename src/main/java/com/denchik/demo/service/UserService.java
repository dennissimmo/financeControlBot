package com.denchik.demo.service;

import com.denchik.demo.model.User;
import com.denchik.demo.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserService {
    private final UserRepository userRepository;
    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional(readOnly = true)
    public List<User> findAll () {
        return userRepository.findAll();
    }
    @Transactional(readOnly = true)
    public User findUserByChat_id (Long chat_id) {
        return userRepository.findByChatId(chat_id);
    }
    @Transactional
    public User addUser (User user) {
        return userRepository.save(user);
    }
    @Transactional
    public void deleteUserByChat_id (Long chat_id) {
        userRepository.deleteByChatId(chat_id);
    }
    @Transactional
    public void saveUser (User user){
        userRepository.save(user);
    }

}
