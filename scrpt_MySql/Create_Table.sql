CREATE TABLE `caracteristicas` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `icono` varchar(250) DEFAULT NULL,
   `nombre` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`id`)
);
  
CREATE TABLE `categoria` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `descripcion` varchar(250) DEFAULT NULL,
   `titulo` varchar(45) DEFAULT NULL,
   `url` varchar(250) DEFAULT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY (`titulo`)
 );

CREATE TABLE `ciudades` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `nombre` varchar(45) DEFAULT NULL,
   `pais` varchar(45) DEFAULT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY (`nombre`)
 );
 
 CREATE TABLE `productos` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `descripcion` varchar(255) DEFAULT NULL,
   `direccion` varchar(300) DEFAULT NULL,
   `latitud` varchar(255) DEFAULT NULL,
   `longitud` varchar(255) DEFAULT NULL,
   `nombre` varchar(255) DEFAULT NULL,
   `categoria_id` bigint DEFAULT NULL,
   `ciudad_id` bigint DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY  (`categoria_id`),
   KEY (`ciudad_id`),
   CONSTRAINT  FOREIGN KEY (`ciudad_id`) REFERENCES `ciudades` (`id`),
   CONSTRAINT  FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`id`)
 );
 
 CREATE TABLE `imagen` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `titulo` varchar(100) DEFAULT NULL,
   `url` varchar(500) DEFAULT NULL,
   `imagen_id` bigint DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY (`imagen_id`),
   CONSTRAINT FOREIGN KEY (`imagen_id`) REFERENCES `productos` (`id`)
 );
 
 CREATE TABLE `producto_caracteristica` (
   `producto_id` bigint NOT NULL,
   `caracteristica_id` bigint NOT NULL,
   KEY (`caracteristica_id`),
   KEY (`producto_id`),
   CONSTRAINT FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`),
   CONSTRAINT FOREIGN KEY (`caracteristica_id`) REFERENCES `caracteristicas` (`id`)
 );
 
 CREATE TABLE `politicas` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `nombre` varchar(45) DEFAULT NULL,
   `politicas_id` bigint DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY (`politicas_id`),
   CONSTRAINT FOREIGN KEY (`politicas_id`) REFERENCES `productos` (`id`)
 );
 
 CREATE TABLE `politicas_descripcion` (
   `politicas_id` bigint NOT NULL,
   `descripcion` varchar(255) DEFAULT NULL,
   KEY (`politicas_id`),
   CONSTRAINT  FOREIGN KEY (`politicas_id`) REFERENCES `politicas` (`id`)
 );
  
 CREATE TABLE `roles` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `nombre` varchar(250) DEFAULT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY  (`nombre`)
 );
 
  CREATE TABLE `usuario` (
   `tipo` varchar(31) NOT NULL,
   `id` bigint NOT NULL AUTO_INCREMENT,
   `apellido` varchar(45) DEFAULT NULL,
   `email` varchar(45) DEFAULT NULL,
   `nombre` varchar(45) DEFAULT NULL,
   `contraseña` varchar(500) DEFAULT NULL,
   `ciudad_id` bigint DEFAULT NULL,
   `rol_id` bigint DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY (`ciudad_id`),
   KEY  (`rol_id`),
   CONSTRAINT  FOREIGN KEY (`ciudad_id`) REFERENCES `ciudades` (`id`),
   CONSTRAINT  FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`)
 );
 
 CREATE TABLE `reservas` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `fecha_final` date NOT NULL,
   `fecha_inicial` date NOT NULL,
   `hora_comienzo` time NOT NULL,
   `hora_final` time DEFAULT NULL,
   `cliente_id` bigint NOT NULL,
   `producto_id` bigint NOT NULL,
   PRIMARY KEY (`id`),
   KEY (`cliente_id`),
   KEY  (`producto_id`),
   CONSTRAINT  FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`),
   CONSTRAINT FOREIGN KEY (`cliente_id`) REFERENCES `usuario` (`id`)
 );
 
 CREATE TABLE `puntuacion` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `puntuacion` bigint DEFAULT NULL,
   `id_producto` bigint NOT NULL,
   `id_usuario` bigint NOT NULL,
   PRIMARY KEY (`id`),
   KEY (`id_producto`),
   KEY  (`id_usuario`),
   CONSTRAINT  FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id`),
   CONSTRAINT  FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`)
 );
 
 
 
 
 
 
