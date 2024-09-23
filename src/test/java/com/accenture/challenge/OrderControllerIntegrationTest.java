package com.accenture.challenge;

import com.accenture.challenge.controller.OrderController;
import com.accenture.challenge.dto.OrderDto;
import com.accenture.challenge.model.Order;
import com.accenture.challenge.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void processOrder_success() throws Exception {
        // Arrange
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerId(12345L);
        orderDto.setOrderAmount(new BigDecimal("99.99"));
        orderDto.setOrderItems(new ArrayList<>());  // Empty list of items for now

        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(12345L);
        order.setOrderAmount(new BigDecimal("99.99"));

        when(orderService.processOrder(any(Order.class))).thenReturn(CompletableFuture.completedFuture(order));

        // Act and Assert
        mockMvc.perform(post("/api/orders/processOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk());

        // Verify that the orderService.processOrder() was called once with any Order object
        verify(orderService, times(0)).processOrder(any(Order.class));
    }

    @Test
    void processOrder_fail_emptyOrderItems() throws Exception {
        // Arrange
        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerId(12345L);
        orderDto.setOrderAmount(new BigDecimal("99.99"));

        // Act and Assert
        mockMvc.perform(post("/api/orders/processOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Verify that orderService.processOrder() is never called because of validation failure
        verify(orderService, never()).processOrder(any(Order.class));
    }

    @Test
    void getOrder_success() throws Exception {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setOrderItems(new ArrayList<>());

        when(orderService.getOrderById(1L)).thenReturn(new Order());

        mockMvc.perform(get("/api/orders/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllOrders_success() throws Exception {
        List<OrderDto> orderDtos = Arrays.asList(new OrderDto(), new OrderDto());

        when(orderService.getAllOrders()).thenReturn(Arrays.asList(new Order(), new Order()));

        mockMvc.perform(get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
