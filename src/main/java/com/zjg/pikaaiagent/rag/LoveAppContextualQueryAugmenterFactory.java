package com.zjg.pikaaiagent.rag;


import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.stereotype.Component;

/**
 * 创建上下文查询增强器的工厂， 算是异常处理。实现当查不到相关文档的时候去回复特定内容
 */
@Component
public class LoveAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance() {

        //检索为空时，要把提示词修改
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                    你应该输出下面的内容：
                    抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                    有问题可以进入百度查询 https://www.baidu.com/
                """);

        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)    // 不允许为空
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}

