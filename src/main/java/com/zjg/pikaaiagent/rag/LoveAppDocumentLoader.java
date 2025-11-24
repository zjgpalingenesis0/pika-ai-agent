package com.zjg.pikaaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;

import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 恋爱大师应用文档加载器 文档加载，最后分好块
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadDocuments() {
        List<Document> allDocuments = new ArrayList<>();

        try {
            // 把多个文档读取到
            Resource[] resources = resourcePatternResolver
                    .getResources("classpath:documents/*.md");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                //添加一个元信息
                String status = fileName.substring(fileName.length() - 6, fileName.length() - 4);
                //官方文档中markdown文档读取器的配置，直接拿过来
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)    //不包括代码块
                        .withIncludeBlockquote(false)    //不包包括引用
                        .withAdditionalMetadata("filename", fileName)
                        .withAdditionalMetadata("status", status)   //添加元信息，作为标签
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("document read error,文档加载失败", e);
        }

        return allDocuments;
    }
}
