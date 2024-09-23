package com.accenture.challenge.service;

import com.accenture.challenge.error.OrderNotFoundException;
import com.accenture.challenge.model.Order;
import com.accenture.challenge.model.OrderItem;
import com.accenture.challenge.model.OrderStatus;
import com.accenture.challenge.repository.OrderItemRepository;
import com.accenture.challenge.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Slf4j // Enable logging for this class
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RedisTemplate<String, Order> redisTemplate;
    private final OrderProcessor orderProcessor;

    // Crear un grupo de hilos personalizado con un número fijo de hilos
    private final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();


    @Override
    @Transactional  // Make sure the transaction covers the synchronous part
    public CompletableFuture<Order> processOrder(Order order) {

        // Save the order and update the status synchronously within the transaction
        order.setStatus(OrderStatus.RECEIVED);

        log.debug("Guardando la orden con ID: {}", order.getId());

        Order savedOrder = orderRepository.save(order);

        // Set the order ID for each OrderItem
        for (OrderItem item : order.getOrderItems()) {
            item.setOrderId(savedOrder.getId());  // Make sure this is correct
        }
        orderItemRepository.saveAll(order.getOrderItems());

        // Start the asynchronous processing
        return CompletableFuture.supplyAsync(() -> {

            // Simulate order processing
            orderProcessor.processOrder(savedOrder);
            log.debug("Orden procesada: {}", savedOrder.getId());

            // Save the completed order
            Order completedOrder = orderRepository.save(savedOrder);
            redisTemplate.opsForValue().set(completedOrder.getId().toString(), completedOrder);
            log.debug("Orden completada guardada en Redis con ID: {}", completedOrder.getId());

            // Return the completed order
            return completedOrder;
        }, forkJoinPool);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#orderId")
    public Order getOrderById(Long orderId) {

        log.debug("Buscando la orden con ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("El orden con id: " + orderId + " no existe"));

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {

        log.debug("Obteniendo los elementos de la orden para el ID: {}", orderId);

        return orderItemRepository.findAllByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {

        log.debug("Obteniendo todas las órdenes");

        return orderRepository.findAll();
    }
}
