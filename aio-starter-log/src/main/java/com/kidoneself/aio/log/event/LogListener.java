package com.kidoneself.aio.log.event;


import com.kidoneself.aio.log.model.OptLogDTO;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

/**
 * 注解形式，异步监听事件
 *
 * @author pangu 7333791@qq.com
 * @since 2020-7-15
 */
@Slf4j
@Setter
@AllArgsConstructor
public class LogListener {

    private Consumer<OptLogDTO> consumer;

    @Async
    @Order
    @EventListener(LogEvent.class)
    public void saveSysLog(LogEvent event) {
        OptLogDTO optLogDTO = (OptLogDTO) event.getSource();
        consumer.accept(optLogDTO);
    }

}
