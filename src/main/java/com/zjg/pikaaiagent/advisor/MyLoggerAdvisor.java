package com.zjg.pikaaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import reactor.core.publisher.Flux;

import java.util.function.Function;

/**
 * 自定义日志Advisor - 适配 Spring AI 1.1.2
 * 参考 SimpleLoggerAdvisor 的新版本实现，保留原有的打印功能
 */
@Slf4j public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(MyLoggerAdvisor.class);
    private final int order;

    public MyLoggerAdvisor() {
        this(0);
    }

    public MyLoggerAdvisor(int order) {
        this.order = order;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        this.logRequest(chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        this.logResponse(chatClientResponse);
        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        this.logRequest(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);
        return (new ChatClientMessageAggregator()).aggregateChatClientResponse(chatClientResponses, this::logResponse);
    }

    protected void logRequest(ChatClientRequest request) {
//        String userText = request.userText();
//        logger.debug("request: {}", userText);
//        System.out.println("=================================");
//        System.out.println("AI Request: " + userText);

        logger.debug("request: {}", request);
        System.out.println("=================================");
        System.out.println("AI Request: " + request);
    }

    protected void logResponse(ChatClientResponse chatClientResponse) {
        ChatResponse chatResponse = chatClientResponse.chatResponse();
        String responseText = "";
        
        if (chatResponse != null && chatResponse.getResult() != null 
            && chatResponse.getResult().getOutput() != null) {
            responseText = chatResponse.getResult().getOutput().getText();
        }
        
        logger.debug("response: {}", responseText);
        System.out.println("=================================");
        System.out.println("AI Response: " + responseText);
    }
}
