package com.zjg.pikaaiagent.demo.invoke;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONArray;

/**
 * 阿里云灵积AI HTTP调用
 */
public class HttpAiInvoke {

    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String API_KEY = TestApiKey.API_KEY;

    /**
     * 调用Qwen模型API
     * @param userPrompt 用户输入的问题
     * @return API响应结果
     */
    public static String callQwenApi(String userPrompt) {
        try {
            // 构建请求JSON
            JSONObject requestBody = new JSONObject();
            requestBody.set("model", "qwen-plus");

            // 构建messages数组
            JSONArray messages = new JSONArray();

            // 系统消息
            JSONObject systemMsg = new JSONObject();
            systemMsg.set("role", "system");
            systemMsg.set("content", "You are a helpful assistant.");
            messages.add(systemMsg);

            // 用户消息
            JSONObject userMsg = new JSONObject();
            userMsg.set("role", "user");
            userMsg.set("content", StrUtil.isNotBlank(userPrompt) ? userPrompt : "你是谁？");
            messages.add(userMsg);

            // 设置input
            JSONObject input = new JSONObject();
            input.set("messages", messages);
            requestBody.set("input", input);

            // 设置parameters
            JSONObject parameters = new JSONObject();
            parameters.set("result_format", "message");
            requestBody.set("parameters", parameters);

            // 发送HTTP POST请求
            String response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(30000) // 30秒超时
                    .execute()
                    .body();

            return response;

        } catch (Exception e) {
            System.err.println("调用Qwen API失败: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        // 示例调用
        String response = callQwenApi("你是谁？");
        if (response != null) {
            System.out.println("API响应:");
            System.out.println(response);

            // 解析响应获取回复内容
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONObject output = jsonResponse.getJSONObject("output");
                if (output != null) {
                    JSONArray choices = output.getJSONArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                        if (message != null) {
                            String content = message.getStr("content");
                            System.out.println("\n模型回复:");
                            System.out.println(content);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("解析响应失败: " + e.getMessage());
            }
        }
    }
}
