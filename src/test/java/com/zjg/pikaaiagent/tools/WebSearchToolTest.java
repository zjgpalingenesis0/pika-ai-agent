package com.zjg.pikaaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebSearchToolTest {
    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Test
    void testSearchWeb() {
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        String query = "csdn的作用有哪些";
        String result = webSearchTool.searchWeb(query);
        Assertions.assertNotNull(result);
    }
}