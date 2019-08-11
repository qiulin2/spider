package com.spider.main;

import java.util.Map;

/**
 * @Author zql
 * @Date 2019/8/6 0006
 * @Description TODO
 **/
public abstract class AbstractTask {

    public abstract int doJob();

    //参数信息
    private Map<String,Object> argumentsMap;





    public Map<String, Object> getArgumentsMap() {
        return argumentsMap;
    }

    public void setArgumentsMap(Map<String, Object> argumentsMap) {
        this.argumentsMap = argumentsMap;
    }
}
