
INSERT INTO caracteristicas (nombre,icono)
VALUES 
("Wi-Fi","fa-solid fa-wifi"),
("Television","fa-solid fa-tv"),
("Aire Acondicionado","fa-solid fa-snowflake"),
("Cocina","fa-solid fa-fire-burner"),
("Mascotas","fa-solid fa-paw"),
("Estacionamiento","fa-solid fa-car"),
("Pileta","fa-solid fa-person-swimming"),
("Spa","fa-solid fa-spa"),
("Sauna","fa-solid fa-hot-tub"),
("Transporte al aeropuerto","fa-solid fa-plane-departure"),
("Terraza","fa-solid fa-umbrella-beach"),
("Bar","fa-solid fa-cocktail"),
("Desayuno gratis","fa-solid fa-coffee"),
("Recepción 24 horas","fa-solid fa-clock"),
("Restaurante","fa-solid fa-utensils"),
("Servicio a la habitación","fa-solid fa-concierge-bell"),
("Mascotas permitidas","fa-solid fa-dog"),
("Piscina","fa-solid fa-swimming-pool"),
("Gimnasio","fa-solid fa-dumbbell");

INSERT INTO categoria ( titulo, descripcion, url)
VALUES 
("Hoteles de Lujo", 
"Hoteles de lujo que ofrecen servicios de alta gama y comodidades premium.", 
"https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"),

("Hoteles Económicos", 
"Hoteles económicos con precios asequibles y comodidades básicas.", 
"https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"),

("Hoteles Boutique", 
"Hoteles de diseño únicos y personalizados que ofrecen una experiencia de hospedaje distinta.", 
"https://images.unsplash.com/photo-1582719508461-905c673771fd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1025&q=80"),

("Resorts", 
"Hoteles con amplias instalaciones de entretenimiento y actividades para vacaciones y descanso.", 
"https://images.unsplash.com/photo-1583522862857-bd9f6d34a236?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"),

("Casas de Huéspedes", 
"Hoteles que ofrecen habitaciones en hogares o propiedades privadas.", 
"https://images.unsplash.com/photo-1584132878434-3b678a00a8bc?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"),

("Departamentos", 
"Departamentos para alquiler de corto o largo plazo.", 
"https://images.unsplash.com/photo-1618773928121-c32242e63f39?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"),

("Casas", 
"Casas para alquiler vacacional o residencial.", 
"https://images.unsplash.com/photo-1645379033960-72d6cb488c0e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80");

INSERT INTO ciudades ( nombre, pais)
VALUES 
("Alver", "Argentina"),
("Mendoza", "Argentina"),
("Manizales", "Argentina"),
("Bariloche", "Argentina"),
("Buenos Aires", "Argentina"),
("Córdoba", "Argentina"),
("Rosario", "Argentina"),
("Salta", "Argentina"),
("La Plata", "Argentina"),
("Barranquilla", "Colombia");

INSERT INTO productos (nombre, descripcion, imagenes,caracteristicas,id_) 
VALUES 
('NH Buenos Aires Latino', 
'El NH Latino ofrece habitaciones amplias equipadas con comodidades de lujo y decoradas 
con una combinación de estilo tradicional y moderno. Las habitaciones tienen ventanas grandes con vistas a la ciudad, por lo que son luminosas.', 
'{
"imagenes": [
{"titulo": "Restaurante",
 "url": "https://images.unsplash.com/photo-1584132878434-3b678a00a8bc?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"}, 
 {"titulo": "Piscina",
 "url": "https://images.unsplash.com/photo-1582719508461-905c673771fd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1025&q=80"}
 ]
 }');


INSERT INTO usuario (nombre, apellido,email,contraseña)
VALUES 
("Sofía","Hernández","sofia.hernandez@example.com","098poi"),
("David","Rodríguez","david.rodriguez@example.com","plmokn"),
("Ana","González","ana.gonzalez@example.com","qazwsx"),
("Laura","Sánchez", "laura.sanchez@example.com","xyz789"),
("Carlos","Martínez","carlos.martinez@example.com","abc123"),
("María","García","maria.garcia@example.com","qwe123"),
("Juan","Pérez","juan.perez@example.com","p@ssw0rd");

INSERT INTO puntuacion (id_producto, id_usuario, puntuacion)
VALUES 
(1,1,10),
(2,2,9),
(1,3,8),
(2,4,7),
(1,5,6),
(2,6,5),
(1,7,4),
(2,1,3),
(1,2,2),
(2,3,1),
(1,4,10),
(2,5,9),
(1,6,8),
(2,7,7),
(1,1,6),
(2,2,5),
(1,3,4),
(1,4,3),
(2,5,2),
(1,6,1);

-- Corregir POliticas

INSERT INTO politicas (nombre, descripcion)
VALUES 
('Normas de la casa', 
'["No hacer ruido después de las 10 pm",
"Respetar las horas de silencio",
"No traer mascotas sin autorización",
"No dejar objetos personales en áreas comunes",
"No fumar dentro del área común"]'),

('Salud y Seguridad', 
'["Respetar las normas de capacidad máxima en las áreas comunes",
"No utilizar aparatos eléctricos peligrosos en la habitación",
"No dejar objetos de valor sin supervisión en la habitación"'),

('Politica de cancelacion', 
'["No hay reembolso por salida anticipada",
"Se permite cambiar la fecha de la reserva sin cargo adicional sujeto a disponibilidad",
"Las cancelaciones deben realizarse por escrito o por teléfono"]');







