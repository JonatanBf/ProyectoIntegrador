UPDATE categorias
SET titulo="Hotel_Nuevo", descripcion = "Update", url = "https://www.shutterstock.com/image-illustration/bright-cozy-modern-bedroom-dressing-room-560973166"
WHERE id = 2; -- Titulo Existente

UPDATE ciudad
SET nombre ="CiudadNew", pais="NewPais"
WHERE id=1;

UPDATE imagen
SET titulo ="UpdateTitulo", url="https://www.shutterstock.com/image-illustration/bright-cozy-modern-bedroom-dressing-room-560973166"
WHERE id=1;

UPDATE productos
SET nombre ="UpdateNombre", descripcion = "Update", id_Imagen=3 ,id_Categoria=5 ,id_Ciudad=2 , id_Caracteristicas=1
WHERE id=1;

UPDATE productos
SET id_Caracteristicas=7
WHERE id=6;

UPDATE puntuaciones
SET  id_Producto=2 ,id_Usuario=1 ,puntuacion=2 
WHERE id=1;

UPDATE usuarios
SET  nombre="UpdateNombre", apellido="UpdateApellido", email="Update@gmail.com" ,contrasenia ="$2a$10$lqEPqMK.oRzXmMX429MfOuROvpaIJHsEAd0nQcw.Yh/rLHQGmmVzW"
WHERE id=7;

UPDATE caracteristicas
SET  nombre="UpdateNombre", icono="Icono"
WHERE id=1;