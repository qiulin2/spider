package com.spider.main;

import com.spider.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zql
 * @Date 2019/8/6 0006
 * @Description 可打成jar包 放到服务器用java -jar 执行任务   不同任务不同参数
 **/
public class Main {

    public static void main(String[] args) {

//        String para = "taskname=TestRedis";
        String para = "taskname=Spider&url=http://tl.cyg.changyou.com/goods/char_detail?serial_num=201912091748056873";
        System.out.println(para.getBytes().length);
//        para = args[0];
        Map<String,Object> pm = parseParameter(para);
        String taskname = pm.get("taskname").toString();

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        AbstractTask task = applicationContext.getBean(taskname.substring(0,1).toLowerCase()+taskname.substring(1),AbstractTask.class);
        task.setArgumentsMap(pm);
        task.doJob();
    }

    private static Map<String,Object> parseParameter(String para) {
        Map<String,Object> ret = new HashMap<>();
        String[] ps1 = para.split("&+");
        for(String p1 : ps1) {
            if(!p1.contains("url=")){
                String[] ps2 = p1.split("=");
                ret.put(ps2[0], ps2[1]);
            }else {
                ret.put("url", p1.substring(4,p1.length()));
            }
        }
        return ret;
    }


}
