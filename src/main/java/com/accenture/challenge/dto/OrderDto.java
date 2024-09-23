package com.accenture.challenge.dto;

import com.accenture.challenge.model.OrderItem;
import com.accenture.challenge.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Schema(description = "DTO que representa una Orden")
public class OrderDto {

    private static final Logger logger = LoggerFactory.getLogger(OrderDto.class);

    @Nullable
    @Schema(description = "Identificador único de la orden", example = "1")
    private Long id;

    @NotNull
    @Schema(description = "Identificador único del cliente", example = "12345")
    private Long customerId;

    @NotNull
    @Positive
    @Schema(description = "Monto total de la orden", example = "99.99")
    private BigDecimal orderAmount;

    @NotNull
    @Schema(description = "Lista de artículos en la orden", example = "[\"item1\", \"item2\"]")
    private List<OrderItemDto> orderItems;

    @Schema(description = "Estado actual de la orden", example = "PROCESSING")
    private OrderStatus status;

    @Override
    public String toString() {
        String orderDetails = "OrderDto{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", orderAmount=" + orderAmount +
                ", orderItems=" + orderItems +
                ", status=" + status +
                '}';

        // Registrar los detalles de la orden
        logger.info("Detalles de la orden: {}", orderDetails);

        return orderDetails;
    }
}
