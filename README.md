### Documentación del Servicio de Procesamiento de Pedidos

Este servicio tiene como objetivo procesar y gestionar pedidos de manera eficiente utilizando una arquitectura basada en **Spring Boot**. Los pedidos se almacenan en una base de datos, se procesan de forma asíncrona, y se gestionan a través de un conjunto de endpoints REST. Redis se utiliza para almacenamiento en caché y se emplea un pool de hilos personalizado para manejar la concurrencia.

---

## Índice
1. [Descripción General](#descripción-general)
2. [Componentes del Servicio](#componentes-del-servicio)
   1. [OrderController](#ordercontroller)
   2. [OrderService](#orderservice)
   3. [OrderServiceImpl](#orderserviceimpl)
   4. [OrderProcessor](#orderprocessor)
   5. [OrderMapper](#ordermapper)
3. [Endpoints](#endpoints)
   1. [Procesar Orden](#procesar-orden)
   2. [Obtener Orden por ID](#obtener-orden-por-id)
   3. [Obtener Todas las Órdenes](#obtener-todas-las-órdenes)
4. [Manejo de Errores](#manejo-de-errores)
5. [Logs](#logs)
6. [Configuración de Redis](#configuración-de-redis)
7. [Consideraciones de Concurrencia](#consideraciones-de-concurrencia)
8. [Conclusión](#conclusión)

---

## Descripción General

El servicio de procesamiento de pedidos permite crear, procesar, recuperar y almacenar pedidos de clientes. El procesamiento se realiza de manera **asíncrona** para mejorar la escalabilidad y el rendimiento. 
Utiliza **Redis** para caching, lo que mejora el tiempo de respuesta para la recuperación de pedidos. El servicio también está diseñado para manejar solicitudes concurrentes mediante el uso de un **ForkJoinPool** personalizado.

---

## Componentes del Servicio

### OrderController

El `OrderController` expone los endpoints REST para procesar y recuperar pedidos. Utiliza el servicio `OrderService` para realizar operaciones sobre los pedidos y `OrderMapper` para convertir entre entidades y DTOs.

### OrderService

`OrderService` es una interfaz que define los métodos esenciales para el procesamiento y la recuperación de pedidos. Los métodos son implementados en `OrderServiceImpl`.

### OrderServiceImpl

La implementación de `OrderService` maneja la lógica de negocio para:
- Guardar un pedido y sus elementos en la base de datos.
- Procesar el pedido de manera asíncrona.
- Almacenar el pedido procesado en Redis.
- Recuperar pedidos por ID o obtener todos los pedidos.

El servicio utiliza un **ForkJoinPool** para manejar la ejecución asíncrona y mejorar la concurrencia.

### OrderProcessor

El `OrderProcessor` se encarga de realizar el procesamiento de la orden de manera asíncrona. Este componente simula la lógica de negocio compleja, como la validación de inventario, cálculo de precios o cualquier otra regla de negocio.

### OrderMapper

El `OrderMapper` es una clase de utilidad que convierte entre las entidades de **Order** y **OrderItem** a sus correspondientes **DTOs** (`OrderDto` y `OrderItemDto`).
Esta separación permite que las entidades no se expongan directamente en las respuestas de la API, mejorando la seguridad y la mantenibilidad.

---

## Endpoints

### Procesar Orden

#### Descripción

Este endpoint permite procesar una nueva orden de manera **asíncrona**. La orden se almacena en la base de datos, se procesa y luego se guarda en Redis para futuras consultas rápidas.

#### Detalles del Endpoint

- **URL**: `/api/orders/processOrder`
- **Método HTTP**: `POST`
- **Request Body**: `OrderDto` (JSON)
  
  ```json
  {
    "customerId": "123",
    "orderAmount": 150.75,
    "orderItems": [
      {
        "productId": "prod-001",
        "quantity": 2,
        "price": 50.25
      },
      {
        "productId": "prod-002",
        "quantity": 1,
        "price": 50.25
      }
    ],
    "status": "RECEIVED"
  }
  ```

- **Response Body**: `OrderDto` (JSON)

#### Respuestas

- **200 (OK)**: Pedido procesado exitosamente y devuelto en la respuesta.
- **400 (Bad Request)**: La solicitud contiene datos inválidos.
- **500 (Internal Server Error)**: Error interno del servidor al procesar el pedido.

### Obtener Orden por ID

#### Descripción

Este endpoint permite recuperar un pedido específico por su ID. Si el pedido ha sido procesado previamente, se recuperará desde Redis para mejorar el tiempo de respuesta.

#### Detalles del Endpoint

- **URL**: `/api/orders/{orderId}`
- **Método HTTP**: `GET`
- **Path Parameter**: `orderId` (Long)

- **Response Body**: `OrderDto` (JSON)

#### Respuestas

- **200 (OK)**: Pedido recuperado exitosamente.
- **404 (Not Found)**: El pedido con el ID especificado no existe.
- **500 (Internal Server Error)**: Error interno del servidor.

### Obtener Todas las Órdenes

#### Descripción

Este endpoint permite recuperar una lista completa de todas las órdenes almacenadas en la base de datos.

#### Detalles del Endpoint

- **URL**: `/api/orders`
- **Método HTTP**: `GET`

- **Response Body**: `List<OrderDto>`

#### Respuestas

- **200 (OK)**: Lista de pedidos recuperada exitosamente.
- **204 (No Content)**: No hay pedidos disponibles.
- **500 (Internal Server Error)**: Error interno del servidor.

---

## Manejo de Errores

El servicio utiliza una clase personalizada de manejo de errores (`ErrorHandler`) que captura excepciones comunes como:
- **OrderNotFoundException**: Cuando no se encuentra un pedido.
- **IllegalArgumentException**: Para datos de entrada inválidos.
- **General Exceptions**: Para errores no previstos.

Los errores se manejan devolviendo respuestas adecuadas con códigos de estado HTTP como `400`, `404` y `500`.

---

## Logs

El sistema de logs está habilitado usando **Lombok** (`@Slf4j`). Los logs se generan para:
- Guardar y procesar órdenes.
- Recuperar órdenes por ID.
- Manejar errores y excepciones.

Ejemplo de log al procesar una orden:

```plaintext
2024-09-23 13:00:00 DEBUG Guardando la orden con ID: 123
2024-09-23 13:00:02 INFO Pedido procesado exitosamente: 123
2024-09-23 13:00:05 DEBUG Orden completada guardada en Redis con ID: 123
```

---

## Configuración de Redis

Redis se utiliza para almacenar en caché los pedidos procesados, lo que permite una recuperación rápida de datos. La configuración se realiza a través de `application.yml`:

```yaml
spring:
  redis:
    host: redis
    port: 6379
  cache:
    type: redis
```

---

## Consideraciones de Concurrencia

El servicio utiliza un **ForkJoinPool** personalizado para manejar la ejecución asíncrona de las órdenes. Esto permite que múltiples órdenes se procesen al mismo tiempo sin bloquear la ejecución principal.

```java
private final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
```

El uso de **`CompletableFuture.supplyAsync()`** asegura que las operaciones de procesamiento de pedidos no bloqueen el hilo principal, mejorando la escalabilidad del servicio.

---

## Conclusión

Este servicio de procesamiento de pedidos está diseñado para ser escalable, eficiente y fácil de mantener. 
Utiliza Spring Boot, Redis para caching, y un pool de hilos personalizado para manejar múltiples solicitudes concurrentes. 
La aplicación está instrumentada con logs y un manejo robusto de errores para garantizar la estabilidad en entornos de producción.
