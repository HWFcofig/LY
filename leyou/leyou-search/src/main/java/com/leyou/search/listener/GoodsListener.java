package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author admin
 * @ClassName GoodsListener
 * @date 2020/5/3
 * @Version 1.0
 **/
@Component
public class GoodsListener {

    @Autowired
    private SearchService searchService;

    /**
     * 处理insert和update的消息
     *
     * @param id
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "LEYOU.CREATE.INDEX.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes
                    .TOPIC),key = {"item.insert", "item.update"}))
    public void listenCreate(Long id) throws IOException {
        if (id == null){
            return;
        }
        this.searchService.createIndex(id);
    }

}
