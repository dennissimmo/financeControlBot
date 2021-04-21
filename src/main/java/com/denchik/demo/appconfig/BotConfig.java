package com.denchik.demo.appconfig;

import com.denchik.demo.bot.ControlMoneyTelegramBot;
import com.denchik.demo.service.CategoryService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webHookPath;
    private String botToken;
    private String botUserName;
    @Bean
    public ControlMoneyTelegramBot MySuperTelegramBot() {
        ControlMoneyTelegramBot controlMoneyTelegramBot = new ControlMoneyTelegramBot();
        controlMoneyTelegramBot.setBotToken(botToken);
        controlMoneyTelegramBot.setBotUserName(botUserName);
        controlMoneyTelegramBot.setWebHookPath(webHookPath);
        return controlMoneyTelegramBot;
    }
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
