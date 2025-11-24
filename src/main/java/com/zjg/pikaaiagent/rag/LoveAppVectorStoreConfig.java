package com.zjg.pikaaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 恋爱大师向量数据库配置（初始化基于内存的向量数据库 Bean)
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        //创建一个用EmbeddingModel转换文档为向量的的向量数据库（使用dashscopeEmbeddingModel）
        SimpleVectorStore simpleVectorStore = SimpleVectorStore
                .builder(dashscopeEmbeddingModel)
                .build();
        //加载所有文档
        List<Document> documents = loveAppDocumentLoader.loadDocuments();
        //用自定义切词器（效果一般，不太行）
//        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);
        //用关键词增强器创建元信息
        List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documents);
        //存入向量数据库
        simpleVectorStore.add(enrichDocuments);

        return simpleVectorStore;

    }
}
