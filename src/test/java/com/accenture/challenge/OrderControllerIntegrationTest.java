//package com.accenture.challenge;
//
//import com.accenture.challenge.controller.OrderController;
//import com.accenture.challenge.dto.OrderDto;
//import com.accenture.challenge.model.Order;
//import com.accenture.challenge.model.OrderItem;
//import com.accenture.challenge.service.OrderService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class OrderControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private OrderService orderService;
//
//    @InjectMocks
//    private OrderController orderController;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void processOrder_success() throws Exception {
//        OrderDto orderDto = new OrderDto();
//        orderDto.setCustomerId(12345L);
//        orderDto.setOrderAmount(new BigDecimal("99.99"));
//        orderDto.setOrderItems(new ArrayList<>());
//
//        Order order = new Order();
//        order.setId(1L);
//
//        when(orderService.processOrder(any(Order.class))).thenReturn(CompletableFuture.completedFuture(order));
//
//        mockMvc.perform(post("/api/orders/processOrder")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(orderDto)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//    }
//
//    @Test
//    void getOrder_success() throws Exception {
//        OrderDto orderDto = new OrderDto();
//        orderDto.setId(1L);
//        orderDto.setOrderItems(new ArrayList<>());
//
//        when(orderService.getOrderById(1L)).thenReturn(new Order());
//
//        mockMvc.perform(get("/api/orders/1")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//    }
//
//    @Test
//    void getAllOrders_success() throws Exception {
//        List<OrderDto> orderDtos = Arrays.asList(new OrderDto(), new OrderDto());
//
//        when(orderService.getAllOrders()).thenReturn(Arrays.asList(new Order(), new Order()));
//
//        mockMvc.perform(get("/api/orders")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//    }
//}
