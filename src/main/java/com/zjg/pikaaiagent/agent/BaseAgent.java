package com.zjg.pikaaiagent.agent;

import com.zjg.pikaaiagent.agent.model.AgentState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.zjg.pikaaiagent.agent.model.AgentState.*;

/**
 * agent基类，提供状态管理，内存管理，执行控制，抽象接口
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseAgent {
    /**
     * 核心属性
     */
    private String name;
    /**
     * 提示词属性
     */
    private String systemPrompt;
    private String nextStepPrompt;
    /**
     * 代理状态
     */
    private AgentState state = IDLE;
    /**
     * 执行步骤控制
     * 当前步骤
     * 最大步骤数
     */
    private int currentStep = 0;
    private int maxSteps = 5;
    /**
     * LLM大模型
     */
    private ChatClient chatClient;
    /**
     * 上下文记忆
     */
    private List<Message> messageList = new ArrayList<>();

    /**
     * 同步调用
     * @param userPrompt
     * @return
     */
    public String run(String userPrompt) {
        //基础校验
        // 判断state是否为空闲
        if (this.state != IDLE) {
            throw new RuntimeException("Cannot run agent when state is " + this.state);
        }
        // 判断用户提示词是否为空
        if (StringUtils.isAnyBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent when user prompt is blank");
        }

        //执行，并更改状态
        this.state = RUNNING;
        //记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        List<String> results = new ArrayList<>();
        try {
            for (int i = 0; i < maxSteps && this.state != FINISHED; i ++) {
                currentStep ++;
                log.info("Executing step: " + currentStep + "/" + maxSteps);
                //单步执行
                String resultStep = step();
                String result = "Step" + currentStep + ": " + resultStep;
                results.add(result);
            }
            //检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                this.state = FINISHED;
                //保存列表加一个结束语做标志
                results.add("teminated: Reached max steps (" + maxSteps + ")");
            }

            return String.join("\n", results);
        } catch (Exception e) {
            this.state = ERROR;
            log.error("Error executing step: " + currentStep + "/" + maxSteps, e);
            return "报错信息: " + e.getMessage();

        } finally {
            //清理资源
            this.cleanup();
        }

    }

    /**
     * 流式调用  异步方法
     * @param userPrompt
     * @return
     */
    public SseEmitter runStream(String userPrompt) {
        //创建SseEmitter对象，超时时间5分钟
        SseEmitter sseEmitter = new SseEmitter(300000L);
        //异步处理,避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                //基础校验
                // 判断state是否为空闲
                if (this.state != IDLE) {
                    sseEmitter.send("错误，无法从状态运行代理" + this.state);
                    sseEmitter.complete();
                    return;
                }
                // 判断用户提示词是否为空
                if (StringUtils.isAnyBlank(userPrompt)) {
                    sseEmitter.send("错误，用户提示词不能为空运行代理");
                    sseEmitter.complete();
                    return;
                }

                //执行，并更改状态
                this.state = RUNNING;
                //记录消息上下文
                messageList.add(new UserMessage(userPrompt));
                List<String> results = new ArrayList<>();
                try {
                    for (int i = 0; i < maxSteps && this.state != FINISHED; i ++) {
                        currentStep ++;
                        log.info("Executing step: " + currentStep + "/" + maxSteps);
                        //单步执行
                        String resultStep = step();
                        String result = "Step" + currentStep + ": " + resultStep;
                        //发送每一步的结果
                        sseEmitter.send(result);
                    }
                    //检查是否超出步骤限制
                    if (currentStep >= maxSteps) {
                        this.state = FINISHED;
                        sseEmitter.send("执行结束：达到最大步骤 (" + maxSteps + ")");
                    }
                    //正常完成
                    sseEmitter.complete();
                } catch (Exception e) {
                    this.state = ERROR;
                    log.error("智能体执行失败", e);
                    try {
                        sseEmitter.send("执行错误: " + e.getMessage());
                        sseEmitter.complete();
                    } catch (IOException ex) {
                        sseEmitter.completeWithError(ex);
                    }

                } finally {
                    //清理资源
                    this.cleanup();
                }
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
        });

        //设置超时回调和正常完成
        sseEmitter.onTimeout(() -> {
            this.state = ERROR;
            cleanup();
            log.warn("sse connection timeout");
        });
        sseEmitter.onCompletion(() -> {
            if (this.state == RUNNING) {
                this.state = FINISHED;
            }

            cleanup();
            log.info("sse connection completion");

        });

        return sseEmitter;

    }
    /**
     * 单个步骤
     * @return  步骤执行结果
     */
    public abstract String step();

    /**
     * 清理资源，每个子类可以重写
     */
    protected void cleanup() {}
}
