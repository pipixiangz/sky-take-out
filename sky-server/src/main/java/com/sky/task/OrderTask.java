package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component //实例化，交给spring容器进行管理，自动生成bean交给容器管理
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    /**
     * 处理支付超时订单
     */
    //@Scheduled(cron = "0 * * * * ?") // 每分钟触发一次
    @Scheduled(cron = "1/5 * * * * ?")  //一分钟一次太慢了，所以改成5s一次，错开
    public void processTimeoutOrder(){
        log.info("定时处理支付超时订单：{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15); //或者.minusMinutes(15);
        // select * from orders where status = 1 and order_time < 当前时间-15分钟
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT, time);
        if(ordersList != null && !ordersList.isEmpty()){
            ordersList.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("支付超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }
    }

    /**
     * 处理“派送中”状态的订单
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    @Scheduled(cron = "0/5 * * * * ?")  //凌晨一点不容易一直跑着程序，所以改成5s一次
    public void processDeliveryOrder(){
        log.info("处理派送中订单：{}", LocalDateTime.now());
        // select * from orders where status = 4 and order_time < 当前时间-1小时
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if(ordersList != null && !ordersList.isEmpty()){
            ordersList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            });
        }
    }
}
