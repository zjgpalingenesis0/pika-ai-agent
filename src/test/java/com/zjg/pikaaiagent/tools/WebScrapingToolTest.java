package com.zjg.pikaaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScrapingToolTest {

    @Test
    void testScrapeWebPage() {
        String url = "https://www.codefather.cn/";
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String result = webScrapingTool.scrapeWebPage(url);
        Assertions.assertNotNull(result);
    }
}