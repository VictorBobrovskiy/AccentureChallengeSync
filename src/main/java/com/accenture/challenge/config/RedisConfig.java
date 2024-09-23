package com.accenture.challenge.config;

import com.accenture.challenge.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j; // Importar para @Slf4j
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory; // Asegurar importaciones correctas
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuración de Redis para la aplicación.
 */
@Configuration
@Slf4j // Añadir la anotación Lombok para el logging
public class RedisConfig {

    /**
     * Configura y proporciona una instancia de RedisConnectionFactory utilizando Lettuce.
     *
     * @return RedisConnectionFactory para conexiones a Redis
     */

    @Bean
    LettuceConnectionFactory redisConnectionFactory() {
        log.debug("Creando RedisConnectionFactory para conexión a Redis");
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName("redis"); // replace with your Redis host
        factory.setPort(6379); // replace with your Redis port if different
        return factory;
    }

    /**
     * Configura y proporciona una instancia de RedisTemplate para operaciones con Redis.
     * Configura los serializadores para las claves y los valores.
     *
     * @param connectionFactory la fábrica de conexiones de Redis
     * @return RedisTemplate configurado
     */
    @Bean
    public RedisTemplate<String, Order> redisTemplate(RedisConnectionFactory connectionFactory) {
        // Registrar información sobre la configuración del RedisTemplate
        log.debug("Configurando RedisTemplate con RedisConnectionFactory");

        RedisTemplate<String, Order> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configurar el serializador para las claves como String
        template.setKeySerializer(new StringRedisSerializer());

        // Configurar el serializador para los valores como JSON utilizando Jackson
        Jackson2JsonRedisSerializer<Order> serializer = new Jackson2JsonRedisSerializer<>(Order.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // Registrar el módulo JavaTimeModule para manejar LocalDateTime correctamente
        objectMapper.registerModule(new JavaTimeModule());
        serializer.setObjectMapper(objectMapper);

        template.setValueSerializer(serializer);

        // Mensaje de depuración para confirmar que el RedisTemplate ha sido configurado
        log.debug("RedisTemplate configurado correctamente con serializadores personalizados");

        return template;
    }

    /**
     * Proporciona un serializador Jackson2JsonRedisSerializer para uso general.
     * Configura el ObjectMapper para manejar correctamente los tipos de fecha y hora de Java 8.
     *
     * @return Jackson2JsonRedisSerializer configurado
     */
    @Bean
    public Jackson2JsonRedisSerializer<Object> redisSerializer() {
        // Registrar información sobre la configuración del serializador
        log.debug("Configurando Jackson2JsonRedisSerializer para serialización de objetos");

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // Registrar el módulo JavaTimeModule para soporte de LocalDateTime y otras clases de fecha y hora de Java 8
        objectMapper.registerModule(new JavaTimeModule());
        serializer.setObjectMapper(objectMapper);

        // Mensaje de depuración para confirmar que el serializador ha sido configurado
        log.debug("Jackson2JsonRedisSerializer configurado correctamente con ObjectMapper personalizado");

        return serializer;
    }
}
