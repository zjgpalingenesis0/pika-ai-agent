package com.zjg.pikaaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具
 */
@Component
public class TerminalOperationTool {
    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command){
        try {
            // 创建进程构建器，用于启动外部进程
            ProcessBuilder processBuilder = new ProcessBuilder();

            // 根据操作系统选择合适的命令执行方式
            // Windows系统使用cmd命令，Linux/Mac系统使用bash命令
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                // Windows: cmd /c 执行命令后关闭命令行
                processBuilder.command("cmd", "/c", command);
            } else {
                // Linux/Mac: bash -c 执行命令字符串
                processBuilder.command("bash", "-c", command);
            }

            // 将错误流重定向到标准输出流，这样可以从一个流中读取所有输出
            processBuilder.redirectErrorStream(true);
            // 启动进程执行命令
            Process process = processBuilder.start();

            // 使用StringBuilder收集命令输出
            StringBuilder output = new StringBuilder();
            // 使用try-with-resources确保BufferedReader自动关闭
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                // 逐行读取命令输出
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // 等待进程执行完成并获取退出码
            int exitCode = process.waitFor();
            // 获取完整的命令输出结果
            String result = output.toString().trim();

            // 根据退出码判断命令是否执行成功
            // 退出码为0表示执行成功，非0表示执行失败
            if (exitCode == 0) {
                return "命令执行成功:\n" + result;
            } else {
                return "命令执行失败 (退出码: " + exitCode + "):\n" + result;
            }

        } catch (IOException e) {
            // 处理IO异常：通常是由于命令不存在或权限问题
            return "执行命令时发生IO异常: " + e.getMessage();
        } catch (InterruptedException e) {
            // 处理中断异常：命令执行被外部中断
            Thread.currentThread().interrupt(); // 恢复中断状态
            return "命令执行被中断: " + e.getMessage();
        } catch (Exception e) {
            // 处理其他未知异常
            return "执行命令时发生未知错误: " + e.getMessage();
        }
    }
}
