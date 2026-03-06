package com.zjg.pikaaiagent.controller;

import com.zjg.pikaaiagent.agent.PikaManus;
import com.zjg.pikaaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    @Resource
    private VectorStore pgVectorStore;

    /**
     * 注入 PikaManus Bean
     */
    @Resource
    private PikaManus pikaManus;

    /**
     * 同步调用AI恋爱大师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId){
        return loveApp.doChat(message,chatId);
    }

    /**
     * 流式调用   直接调用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId){
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * 流式调用  可以灵活决定每次哪块输出
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/SseEmitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId){
        //定义一个sseEmitter对象，设置超时时间3分钟
        SseEmitter sseEmitter = new SseEmitter(180000L);
        //获取Flux数据流，并直接订阅
        loveApp.doChatByStream(message, chatId)
                .subscribe(
                        //处理每一条消息
                        chunk -> {
                            try {
                                sseEmitter.send(chunk);
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        //处理错误
                        sseEmitter::completeWithError,
                        //处理完成
                        sseEmitter::complete
                );


        return sseEmitter;
    }

    /**
     * 流式调用超级智能体（支持 Skill）
     * @return
     */
    @GetMapping("agent/love_app")
    public SseEmitter doChatWithManus(String messages) {
        // 使用注入的 PikaManus Bean，已配置好 Skill 组件
        return pikaManus.runStream(messages);
    }

}
