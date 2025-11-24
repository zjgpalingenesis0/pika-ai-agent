package com.zjg.pikaaiagent.app;

import com.zjg.pikaaiagent.advisor.MyLoggerAdvisor;
import com.zjg.pikaaiagent.advisor.ReReadingAdvisor;
import com.zjg.pikaaiagent.chatmemory.FileBasedChatMemory;
import com.zjg.pikaaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.zjg.pikaaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private final static String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，" +
            "告知用户可倾诉恋爱难题。围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * 初始化AI 客户端
     * @param zhiPuAiChatModel  选用对话模型
     */
    public LoveApp(ChatModel dashscopeChatModel) {
        // 初始化自定义的基于文件的对话记忆(项目根目录下的，chat-memory目录)
//        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        //ChatMemory  chatMemory = new FileBasedChatMemory(fileDir);
        // 初始化基于内存的对话记忆。
        ChatMemory chatMemory = new InMemoryChatMemory();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new MyLoggerAdvisor()      // 自定义日志Advisor，可按需开启
//                        new ReReadingAdvisor()   // 自定义推理增强Advisor，可按需开启
                )
                .build();

    }

    /**
     * AI 基础对话（支持多轮对话）
     * @param message  用户提示词
     * @param chatId   对话房间号
     * @return   回答内容
     */
    public String doChat(String message, String chatId) {

        //返回ChatResponse对象（如果只要content，可以最后换成.content()就行了）
        ChatResponse chatResponse = chatClient
                .prompt()  // 启动构建器
                .user(message)   // 用户输入
                .advisors(advisor ->     // 记忆顾问
                        advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                )
                .call()
                .chatResponse();    // 执行调用

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;
    }

    record LoveReport(String title, List<String> suggestions) {

    }

    /**
     * 多轮对话，并生成恋爱报告
     * @param message  用户提示次
     * @param chatId   对话房间
     * @return  返回恋爱报告
     */
    public LoveReport doChatWithReport(String message, String chatId) {

        LoveReport loveReport = chatClient
                .prompt()  // 启动构建器
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)   // 用户输入
                .advisors(advisor ->     // 记忆顾问
                        advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                )
                .call()
                .entity(LoveReport.class);// 执行调用

        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    /**
     * 和rag对话的功能，做知识问答
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        //对用户输入重写
        String query = queryRewriter.doQueryRewrite(message);

        ChatResponse chatResponse = chatClient
                .prompt()  // 启动构建器
                .system(SYSTEM_PROMPT)
                .user(query)   // 用户输入重写后的prompt
                .advisors(advisor ->     // 记忆顾问
                        advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                )
                //开启日志，观察结果
                .advisors(new MyLoggerAdvisor())
                //应用RAG知识库知识问答
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                //应用RAG云知识库，检索增强服务
//                .advisors(loveAppRagCloudAdvisor)
                //应用RAG检索增强服务（基于PgVector向量存储）
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))

                //应用自定义RAG检索增强服务（文档查询器+上下文增强）
                .advisors(
                        LoveAppRagCustomAdvisorFactory
                            .createLoveAppRagCustomAdvisor(loveAppVectorStore, "已婚")
                )
                .call()
                .chatResponse();// 执行调用
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
