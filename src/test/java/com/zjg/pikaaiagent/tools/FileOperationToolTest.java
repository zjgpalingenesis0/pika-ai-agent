package com.zjg.pikaaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.antlr.v4.runtime.misc.Utils.readFile;
import static org.junit.jupiter.api.Assertions.*;

class FileOperationToolTest {

    @Test
    void testReadFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "文件测试.txt";
        String result = fileOperationTool.readFile(fileName);
        Assertions.assertNotNull(result);
    }

    @Test
    void testWriteFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "文件测试.txt";
        String content = "我在测试自定义的文件读写工具！！！";
        String result = fileOperationTool.writeFile(fileName, content);
        Assertions.assertNotNull(result);
    }
}