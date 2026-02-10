package com.zjg.pikaaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.zjg.pikaaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

import static com.zjg.pikaaiagent.agent.model.AgentState.FINISHED;

/**
 * 工具调用智能体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent{
    /**
     * 可用的工具
     */
    private final ToolCallback[] availableTools;
    /**
     * 保存工具调用信息的响应结果（要调用哪些工具）
     */
    private ChatResponse toolCallChatResponse;
    /**
     * 工具调用管理者
     */
    private final ToolCallingManager toolCallingManager;
    /**
     * 禁用SpringAI内置的工具调用，以便于自己维护选项和消息上下文
     */
    private final ChatOptions chatOptions;

    //构造器
    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();

    }

    @Override
    public boolean think() {

        try {
            //校验提示词，拼接用户提示词
            if(StringUtils.isNotBlank(getNextStepPrompt())) {
                UserMessage userMessage = new UserMessage(getNextStepPrompt());
                getMessageList().add(userMessage);
            }
            //调用AI大模型，获取工具调用结果
            Prompt prompt = new Prompt(getMessageList(), chatOptions);
            //获取响应结果
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            //记录响应信息
            this.toolCallChatResponse = chatResponse;

            //解析工具调用结果，获取要调用的工具
            //记录响应，用于act
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //获取要调用的工具列表
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            //输出显示信息
            String text = assistantMessage.getText();
            log.info(getName() + "的思考：" + text);
            log.info(toolCalls.size() + "个工具被使用");
            String toolCallInfo = toolCalls.stream()
                    .map(toolCall -> String.format(
                            "工具名称: %s, 参数: %s",
                            toolCall.name(), toolCall.arguments()
                    ))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);

            if (toolCalls.isEmpty()) {
                //只有不调用工具时，才要手动记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            }
            else {
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "在思考过程中遇到问题" + e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到了错误"));
            return false;
        }
    }

    @Override
    public String act() {
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有执行工具调用";
        }
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        //调用工具
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        //获取消息上下文
        List<Message> messages = toolExecutionResult.conversationHistory();
        setMessageList(messages);
        //获取工具调用的消息
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(messages);

        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具" + response.name() + "完成了任务！结果：" + response.responseData())
                .collect(Collectors.joining("\n"));
        //判断是否用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if (terminateToolCalled) {
            setState(FINISHED);
        }
        log.info(results);
        return results;
    }
}
