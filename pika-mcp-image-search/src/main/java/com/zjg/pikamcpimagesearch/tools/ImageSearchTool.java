package com.zjg.pikamcpimagesearch.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONArray;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageSearchTool {

    @Value("${pexels.api.key}")
    private String pexelsApiKey;

    @Tool(description = "Search image from web")
    public String searchImage(@ToolParam(description = "Search query keywords") String query) {
        try {
            if (StrUtil.isBlank(pexelsApiKey)) {
                return "错误：Pexels API密钥未配置，请在application.yml中设置pexels.api.key";
            }

            if (StrUtil.isBlank(query)) {
                return "错误：搜索关键词不能为空";
            }

            // 发送HTTP请求到Pexels API
            String url = "https://api.pexels.com/v1/search?query=" + query + "&per_page=5";

            HttpResponse response = HttpRequest.get(url)
                    .header("Authorization", pexelsApiKey)
                    .timeout(30000)
                    .execute();

            if (!response.isOk()) {
                return "错误：API请求失败，状态码：" + response.getStatus() +
                       "，响应：" + response.body();
            }

            // 解析JSON响应
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray photos = jsonResponse.getJSONArray("photos");

            if (photos == null || photos.isEmpty()) {
                return "未找到与关键词 '" + query + "' 相关的图片";
            }

            // 提取图片信息
            List<String> imageUrls = new ArrayList<>();
            StringBuilder result = new StringBuilder();

            result.append("🔍 搜索关键词：").append(query).append("\n");
            result.append("📊 总共找到：").append(jsonResponse.getInt("total_results")).append(" 张图片\n\n");

            for (int i = 0; i < Math.min(photos.size(), 5); i++) {
                JSONObject photo = photos.getJSONObject(i);
                JSONObject src = photo.getJSONObject("src");

                String mediumUrl = src.getStr("medium");
                String photographer = photo.getStr("photographer");
                String alt = photo.getStr("alt");
                int width = photo.getInt("width");
                int height = photo.getInt("height");

                result.append("🖼️  图片 ").append(i + 1).append("：\n");
                result.append("   📝 描述：").append(alt != null ? alt : "无描述").append("\n");
                result.append("   📸 摄影师：").append(photographer).append("\n");
                result.append("   📐 尺寸：").append(width).append(" × ").append(height).append("\n");
                result.append("   🔗 中等尺寸：").append(mediumUrl).append("\n\n");

                imageUrls.add(mediumUrl);
            }

            return result.toString();

        } catch (Exception e) {
            return "搜索图片时发生错误：" + e.getMessage() +
                   "\n请检查API密钥是否正确，或网络连接是否正常。";
        }
    }
}
