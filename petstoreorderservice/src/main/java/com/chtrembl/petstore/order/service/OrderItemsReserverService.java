package com.chtrembl.petstore.order.service;

import com.chtrembl.petstore.order.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemsReserverService {

    private final RestTemplate restTemplate;

    @Value("${petstore.service.orderItemsReserver.url}")
    private String orderItemsReserverUrl;

    public void reserverOrderItems(Order order) {
        log.info("Calling OrderItemsReserver service for orderId={}", order.getId());

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            headers.set("X-Session-Id", order.getId());


            HttpEntity<Order> entity = new HttpEntity<>(order, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    String.format("%s/api/OrderItemsReserver", orderItemsReserverUrl),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("OrderItemsReserver response={} for orderId={}", response.getStatusCode(), order.getId());

        } catch(Exception ex){
            log.error("Error calling OrderItemsReserver: {}", ex.getMessage(), ex);
        }
    }
}
