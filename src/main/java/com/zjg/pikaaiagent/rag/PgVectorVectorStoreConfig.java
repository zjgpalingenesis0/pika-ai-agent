package com.zjg.pikaaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
public class PgVectorVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    @Primary
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate,
                                           EmbeddingModel dashscopeEmbeddingModel) {

        // 官方示例
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // Optional: defaults to model dimensions or 1536
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // 使用默认的public schema
                .vectorTableName("life_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(25)           // 限制每批最多25条,符合DashScope API限制
                .build();

        // 加载所有文档
        List<Document> documents = loveAppDocumentLoader.loadDocuments();

        // 检查数据库中已存在的文档 (通过 metadata 中的 filename 判断)
        Set<String> existingFiles = jdbcTemplate.queryForList(
                "SELECT DISTINCT metadata->>'filename' as filename FROM life_store",
                String.class
        ).stream().collect(Collectors.toSet());

        // 只插入数据库中不存在的文档
        List<Document> newDocuments = documents.stream()
                .filter(doc -> {
                    String filename = (String) doc.getMetadata().get("filename");
                    return !existingFiles.contains(filename);
                })
                .collect(Collectors.toList());

        if (!newDocuments.isEmpty()) {
            System.out.println("发现 " + newDocuments.size() + " 个新文档,开始存储...");
            // DashScope API 限制每次最多 25 条,必须手动分批
            int batchSize = 25;
            for (int i = 0; i < newDocuments.size(); i += batchSize) {
                int end = Math.min(i + batchSize, newDocuments.size());
                List<Document> batch = newDocuments.subList(i, end);
                vectorStore.add(batch);
                System.out.println("已存储 " + end + "/" + newDocuments.size() + " 个文档块");
            }
            System.out.println("新文档存储完成");
        } else {
            System.out.println("所有文档已存在于数据库,无需重复加载");
        }

        return vectorStore;
    }
}

