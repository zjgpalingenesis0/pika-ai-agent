package com.zjg.pikaaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 自定义查询重写器
 */
@Component
public class QueryRewriter {
    private final QueryTransformer queryTransformer;

    public QueryRewriter(ChatModel dashscopeChatModel) {
        ChatClient.Builder chatClient = ChatClient.builder(dashscopeChatModel);
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClient)
                .build();
    }
    //查询重写方法
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);
        //执行查询重写
        Query newPrompt = queryTransformer.transform(query);
        return newPrompt.text();
    }


}
