package com.zjg.pikaaiagent.agent;

import com.zjg.pikaaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent{

    /**
     * 思考，抽象类，处理当前状态并决定下一步行动
     */
    public abstract boolean think();

    /**
     * 行动，抽象类，执行决定的行动
     */
    public abstract String act();

    /**
     * 要实现父类的step方法
     */
    @Override
    public String step() {
        try {
            //先思考，得到一个结果
            boolean result = think();
            //如果结果为false，就是思考完决定不需要行动
            //如果为true，要执行下一步行动
            if (!result) {
                setState(AgentState.FINISHED);
                return "思考完成，不需要行动";
            }
            else {
                return "开始行动！" + "\n" + act();
            }
        } catch (Exception e) {
            log.error("ReAct error, Cannot think or act", e);
            return "步骤执行失败" + e.getMessage();
        }

    }
}
