package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Puntuacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuntuacionRepository extends JpaRepository<Puntuacion, Long> {

    @Query("select p FROM Puntuacion p where p.usuario.id = ?1")
    public List<Puntuacion> getByUsuarioId(Long idUsuario);

    @Query("select p FROM Puntuacion p where p.producto.id = ?1")
    public List<Puntuacion> getByProductoId(Long idProducto);

    @Query("select p FROM Puntuacion p where p.puntuacion = ?1")
    public List<Puntuacion> getByPuntuacion(Long puntuacion);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Puntuacion p WHERE p.usuario.id = :idUsuario AND p.producto.id = :idProducto")
    boolean existsByUsuarioAndProducto(Long idUsuario, Long idProducto);

    @Query("SELECT COUNT(DISTINCT p.usuario), p.producto.id, AVG(p.puntuacion) "
            + "FROM Puntuacion p "
            + "GROUP BY p.producto.id")
    List<Object[]> obtenerPromedioAlls();

    @Query("SELECT AVG(p.puntuacion) FROM Puntuacion p WHERE p.producto.id = :idProducto")
    Double obtenerPromedioPorProducto(@Param("idProducto") Long idProducto);

}
