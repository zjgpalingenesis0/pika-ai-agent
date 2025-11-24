package com.zjg.pikaaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;

import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

/**
 * 创建自定义rag检索增强的工厂
 */
@Component
public class LoveAppRagCustomAdvisorFactory {

    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        //指定过滤查询条件
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        //构建向量数据库的文档检索器
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)    //过滤条件
                .similarityThreshold(0.5)    //相似度阈值
                .topK(3)     //返回文档数量
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)    //文档检索器
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())    //查询增强器
                .build();
    }
}
