package com.zjg.pikaaiagent.agent;

import com.zjg.pikaaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * AI超级智能体，拥有自主规划能力，可直接使用
 */
@Component
public class PikaManus extends ToolCallAgent {

    public PikaManus(ToolCallback[] allTools, @Qualifier("dashScopeChatModel") ChatModel dashscopeChatModel) {
        super(allTools);
        setName("PikaManus");
        String SYSTEM_PROMPT = """  
                You are PikaManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                """;
        setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;
        setNextStepPrompt(NEXT_STEP_PROMPT);
        setMaxSteps(10);
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        setChatClient(chatClient);
    }
}

