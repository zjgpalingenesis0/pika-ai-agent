package com.zjg.pikaaiagent;


import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {
//    PgVectorStoreAutoConfiguration.class,
//    org.springframework.ai.autoconfigure.chat.client.ChatClientAutoConfiguration.class
//})
@SpringBootApplication
public class PikaAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PikaAiAgentApplication.class, args);
    }

}

