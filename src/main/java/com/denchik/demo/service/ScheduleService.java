package com.denchik.demo.service;
import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.model.User;
import com.denchik.demo.utils.Emojis;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

@Component
@EnableScheduling
@Log4j2
public class ScheduleService {
    private ControlMoneyTelegramBot controlMoneyTelegramBot;
    private UserService userService;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ReplyMessagesService replyMessagesService;
    protected String cronExpressions;

    public ScheduleService(ReplyMessagesService replyMessagesService,ControlMoneyTelegramBot controlMoneyTelegramBot, UserService userService) {
        this.replyMessagesService = replyMessagesService;
        this.controlMoneyTelegramBot = controlMoneyTelegramBot;
        this.userService = userService;
    }
    @Scheduled(cron = "0 52 21 * * *")
    public void sendNotificationForAllUsers () {
        SendMessage message = new SendMessage();
        message.enableHtml(true);
        List<User> users = userService.findAll();
        message.setText(replyMessagesService.getReplyText("rage", Emojis.HEART));
    for (User user : users) {
        log.info("Send reminder for use r : {}", user.toString());
        message.setChatId(user.getChatId());
        controlMoneyTelegramBot.executeSendMessage(message);
    }
    }
    //@Bean
   /* public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(10); // Set to number threads, we will send message for all users in the same time
        scheduler.initialize();
        return scheduler;
    }
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(poolScheduler());
        taskRegistrar.addTriggerTask(() -> scheduleCron(userService.findUserByChat_id(795182716L).getLanguage_code()),t -> {
            CronTrigger cronTrigger = new CronTrigger(userService.findUserByChat_id(795182716L).getLanguage_code());
            return cronTrigger.nextExecutionTime(t);
        });
    }


    public void scheduleCron(String cron) {
        SendMessage message = new SendMessage();
        User currentUser = userService.findUserByChat_id(795182716L);
        message.setText(replyMessagesService.getReplyText("notification.reminder")).setChatId(currentUser.getChatId());
        log.info("Send reminder for use r : {}", currentUser.toString());
        controlMoneyTelegramBot.executeSendMessage(message);
        // Do not put @Scheduled annotation above this method, we don't need it anymore.
        log.info("Current time - {} User Cron {}", LocalTime.now(),currentUser.getLanguage_code());
        log.info("scheduleCron: Next execution time of this taken from cron expression -> {}", cron);
    }*/
}
