package com.denchik.demo.appconfig;

import com.denchik.demo.ControlMoneyTelegramBot;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.index.qual.SearchIndexBottom;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public ControlMoneyTelegramBot MySuperTelegramBot () {
        DefaultBotOptions defaultBotOptions = new DefaultBotOptions();
        ControlMoneyTelegramBot controlMoneyTelegramBot = new ControlMoneyTelegramBot(defaultBotOptions);
        controlMoneyTelegramBot.setBotToken(botToken);
        controlMoneyTelegramBot.setBotUserName(botUserName);
        controlMoneyTelegramBot.setWebHookPath(webHookPath);
        return controlMoneyTelegramBot;
    }
}
