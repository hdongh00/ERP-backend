package com.example.erp.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    //M2 버전 방식
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder.build();
    }
}
