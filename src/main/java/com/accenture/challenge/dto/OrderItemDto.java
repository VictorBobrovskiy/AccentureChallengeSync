package com.accenture.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "DTO que representa una Item del Orden")
public class OrderItemDto {

    @Schema(description = "Identificador único de la item", example = "1")
    private Long id;

    @Schema(description = "Identificador único del producto", example = "1")
    private Long productId;

    @Schema(description = "La cantidad de los productos", example = "2")
    private int quantity;

    @Schema(description = "Precio de unidad", example = "99.99")
    private BigDecimal price;
}