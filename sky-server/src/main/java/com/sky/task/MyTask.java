package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自定义定时任务类
 */
@Component //实例化，交给spring容器进行管理，自动生成bean交给容器管理
@Slf4j
public class MyTask {

    /**
     * 定时任务 每隔5秒触发一次
     */
    //@Scheduled(cron = "0/5 * * * * ?")  //从第0秒每隔五秒触发一次
    public void executeTask(){
        log.info("定时任务开始执行：{}",new Date());
    }
}