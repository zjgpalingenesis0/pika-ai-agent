package com.zjg.pikaaiagent.agent.model;

public enum AgentState {

    /**
     * 空闲状态
     */
    IDLE,
    /**
     * 任务执行状态
     */
    RUNNING,
    /**
     * 任务完成状态
     */
    FINISHED,
    /**
     * 错误状态
     */
    ERROR

}
