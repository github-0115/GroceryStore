package uyun.show.server.domain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalUtil {
    protected final static Logger logger = LoggerFactory.getLogger(EvalUtil.class);

    /**
     * @param elKey
     * @param elValue
     * @param express
     * @return boolean
     * @desc 表达式计算是否告警
     */
    public static Boolean isInclude(String elKey, Object elValue, String express) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        engine.put(elKey, elValue);
        boolean eval = false;
        try {
            eval = (boolean) engine.eval(express);
        } catch (ScriptException e) {
            logger.error("Evaluate ScriptException : " + e);
        }
        return eval;
    }

}
