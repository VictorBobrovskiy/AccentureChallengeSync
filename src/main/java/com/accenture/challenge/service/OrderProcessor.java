package com.accenture.challenge.service;

import com.accenture.challenge.error.ValidateOrderException;
import com.accenture.challenge.model.Order;
import com.accenture.challenge.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class OrderProcessor {

    private final Map<Long, Order> orderStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public void processOrder(Order order) {
        log.debug("Iniciando el procesamiento del orden con ID: {}", order.getId());

        order.setStatus(OrderStatus.PROCESSING);
        log.debug("Estado del orden actualizado a PROCESSING.");

        try {
            // Simular alguna lógica de validación (por ejemplo, verificar stock, crédito del cliente, etc.)
            validateOrder(order);

            // Simular el recálculo del precio del orden
            recalculateOrderPrice(order);
            log.debug("Precio del orden recalculado a: {}", order.getOrderAmount());

            // Si todo es exitoso, marcar el orden como COMPLETED
            order.setStatus(OrderStatus.COMPLETED);
            log.debug("Estado del orden actualizado a COMPLETED.");

            orderStorage.putIfAbsent(order.getId(), order);
            log.debug("Orden guardada en el almacenamiento.");

        } catch (Exception e) {
            // En caso de cualquier falla, marcar el orden como FAILED
            order.setStatus(OrderStatus.FAILED);
            log.debug("Error procesando el orden: {}", e.getMessage());
        }

        // Simular un retraso en el procesamiento
        try {
            Thread.sleep(random.nextInt(400) + 100);  // Retraso aleatorio entre 100-500ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.debug("El hilo fue interrumpido durante el procesamiento.");
        }
    }

    // Validación del orden
    private void validateOrder(Order order) {
        // Verificando si hay artículos en el orden
        if (order.getOrderItems().isEmpty()) {
            throw new ValidateOrderException("El orden debe contener al menos un artículo.");
        }
    }

    // Aplicando un descuento aleatorio
    private void recalculateOrderPrice(Order order) {
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        // Simular la aplicación de un descuento aleatorio
        BigDecimal discount = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0, 0.1))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        totalAmount = totalAmount.subtract(totalAmount.multiply(discount))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        order.setOrderAmount(totalAmount);
    }
}
