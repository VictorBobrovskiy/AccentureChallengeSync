package com.accenture.challenge.util;

import com.accenture.challenge.dto.OrderDto;
import com.accenture.challenge.dto.OrderItemDto;
import com.accenture.challenge.model.Order;
import com.accenture.challenge.model.OrderItem;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j // Enable logging for this class
@UtilityClass
public class OrderMapper {

    // Convertir la entidad Order a OrderDto
    public static OrderDto toDto(Order order) {
        if (order == null) {
            log.debug("El pedido es nulo, devolviendo null");
            return null;
        }
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setCustomerId(order.getCustomerId());
        orderDto.setOrderAmount(order.getOrderAmount());
        orderDto.setStatus(order.getStatus());

        // Mapear OrderItems a OrderItemDtos
        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(OrderMapper::toOrderItemDto)
                .collect(Collectors.toList());
        orderDto.setOrderItems(orderItemDtos);

        log.debug("Convertido Order a OrderDto con ID: {}", order.getId());
        return orderDto;
    }

    // Convertir OrderDto a entidad Order
    public static Order toEntity(OrderDto orderDto) {
        if (orderDto == null) {
            log.debug("El OrderDto es nulo, devolviendo null");
            return null;
        }
        Order order = new Order();
        order.setCustomerId(orderDto.getCustomerId());
        order.setOrderAmount(orderDto.getOrderAmount());
        order.setStatus(orderDto.getStatus());

        // Mapear OrderItemDtos a OrderItems
        List<OrderItem> orderItems = orderDto.getOrderItems().stream()
                .map(OrderMapper::toOrderItemEntity)
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);

        log.debug("Convertido OrderDto a Order con ID: {}", orderDto.getId());
        return order;
    }

    // Convertir lista de entidades Order a lista de OrderDto
    public List<OrderDto> toDtoList(List<Order> orders) {
        log.debug("Convirtiendo lista de Orders a OrderDtos");
        return orders.stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    // Convertir lista de OrderDto a lista de entidades Order
    public List<Order> toEntityList(List<OrderDto> orderDtos) {
        log.debug("Convirtiendo lista de OrderDtos a Orders");
        return orderDtos.stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
    }

    // Convertir entidad OrderItem a OrderItemDto
    public static OrderItemDto toOrderItemDto(OrderItem orderItem) {
        if (orderItem == null) {
            log.debug("El OrderItem es nulo, devolviendo null");
            return null;
        }
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setProductId(orderItem.getProductId());
        orderItemDto.setQuantity(orderItem.getQuantity());
        orderItemDto.setPrice(orderItem.getPrice());

        log.debug("Convertido OrderItem a OrderItemDto con ID: {}", orderItem.getId());
        return orderItemDto;
    }

    // Convertir OrderItemDto a entidad OrderItem
    public static OrderItem toOrderItemEntity(OrderItemDto orderItemDto) {
        if (orderItemDto == null) {
            log.debug("El OrderItemDto es nulo, devolviendo null");
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(orderItemDto.getProductId());
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(orderItemDto.getPrice());

        log.debug("Convertido OrderItemDto a OrderItem");
        return orderItem;
    }
}
