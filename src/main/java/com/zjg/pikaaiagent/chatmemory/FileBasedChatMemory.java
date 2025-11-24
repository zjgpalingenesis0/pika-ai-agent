package com.zjg.pikaaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import dev.langchain4j.data.message.ChatMessage;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;
    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        //设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

        // 注册spring-AI的Message相关类
        kryo.register(Message.class);
        kryo.register(UserMessage.class);
        kryo.register(SystemMessage.class);
        kryo.register(AssistantMessage.class);
    }
    // 构造对象时，指定文件保存目录
    public FileBasedChatMemory(String baseDir) {
        this.BASE_DIR = baseDir;
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // 默认构造函数，使用默认目录
    public FileBasedChatMemory() {
        this(System.getProperty("user.dir") + "/tmp/chat-memory");
    }

    /**
     * 单条消息的增加（保存）
     * @param conversationId
     * @param message
     */
    @Override
    public void add(String conversationId, Message message) {
        // 获取往期消息
        List<Message> messageList = getOrCreateConversation(conversationId);
        // 添加新消息
        messageList.add(message);
        // 保存会话消息
        saveConversation(conversationId, messageList);
    }

    /**
     * 增加多条消息
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId, messageList);
    }

    /**
     * 查询消息列表中最后n条
     * @param conversationId
     * @param lastN
     * @return
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (conversationId == null || lastN <= 0) {
            return new ArrayList<>();
        }
        List<Message> messages = getOrCreateConversation(conversationId);

        return messages.stream()
                .skip(Math.max(0, messages.size() - lastN))
                .toList();
    }

    /**
     * 删除会话在的文件夹
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 创建或获取会话消息列表
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * 保存会话信息
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前会话文件
     * @param conversationId
     * @return
     */
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

}
