package com.accenture.challenge;

import com.accenture.challenge.error.OrderNotFoundException;
import com.accenture.challenge.model.Order;
import com.accenture.challenge.model.OrderItem;
import com.accenture.challenge.repository.OrderItemRepository;
import com.accenture.challenge.repository.OrderRepository;
import com.accenture.challenge.service.OrderProcessor;
import com.accenture.challenge.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ValueOperations<String, Order> valueOperations;


    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private RedisTemplate<String, Order> redisTemplate;

    @Mock
    private OrderProcessor orderProcessor;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void processOrder_success() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setOrderItems(Collections.singletonList(new OrderItem()));
        order.setCustomerId(12345L);
        order.setOrderAmount(BigDecimal.valueOf(99.99));

        // Mock the behavior of the repositories
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.saveAll(any())).thenReturn(Collections.singletonList(new OrderItem()));

        // Mock the RedisTemplate and ValueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When processing the order
        CompletableFuture<Order> futureOrder = orderService.processOrder(order);

        // Then
        Order savedOrder = futureOrder.join(); // Join to get the result

        // Verify interactions
        verify(orderRepository, times(2)).save(order); // Verify save is called twice
        verify(orderItemRepository).saveAll(order.getOrderItems());
        verify(valueOperations).set(eq(order.getId().toString()), eq(savedOrder));

        // Assert the order is processed correctly
        assertEquals(savedOrder.getId(), order.getId());
    }


    @Test
    void getOrderById_orderFound() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);
        assertEquals(order, result);
    }

    @Test
    void getOrderById_orderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(1L);
        });

        assertEquals("El orden con id: 1 no existe", exception.getMessage());
    }

    @Test
    void getAllOrders_success() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();
        assertEquals(2, result.size());
    }
}
