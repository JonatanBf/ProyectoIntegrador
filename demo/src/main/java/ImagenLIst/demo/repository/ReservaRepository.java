package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Producto;
import ImagenLIst.demo.entidades.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("SELECT r FROM Reserva r WHERE r.producto.id = :idProducto AND r.fechaInicial >= :fecha")
    List<Reserva> buscarReservasPorProductoYFecha(@Param("idProducto") Long idProducto, @Param("fecha") LocalDate fecha);


    @Query("select r FROM Reserva r where r.cliente.id = ?1")
    public List<Reserva> getByClienteId(Long idCliente);

    @Query("select r FROM Reserva r where r.producto.id = ?1")
    public List<Reserva> getByProductoId(Long idProducto);

    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.producto = :producto AND r.fechaFinal >= :fechaInicial AND r.fechaInicial <= :fechaFinal")
    boolean existsByProductoRangoFechas(@Param("producto") Producto producto, @Param("fechaInicial") LocalDate fechaInicial,
                                        @Param("fechaFinal") LocalDate fechaFinal);

    @Query("SELECT DISTINCT p\n" +
            "FROM Producto p\n" +
            "WHERE p.ciudad.nombre = :nombreCiudad\n" +
            "" +
            "AND p.id NOT IN (\n" +
            "    SELECT r.producto.id\n" +
            "    FROM Reserva r\n" +
            "    WHERE r.producto.ciudad.nombre = :nombreCiudad\n" +
            "    AND (\n" +
            "        (r.fechaInicial BETWEEN :fechaInicio AND :fechaFin)\n" +
            "        OR (r.fechaFinal BETWEEN :fechaInicio AND :fechaFin)\n" +
            "        OR (:fechaInicio BETWEEN r.fechaInicial AND r.fechaFinal)\n" +
            "        OR (:fechaFin BETWEEN r.fechaInicial AND r.fechaFinal)\n" +
            "    )\n" +
            ")")
    List<Producto> findProductosDisponiblesByCiudadAndFechas(@Param("nombreCiudad") String nombreCiudad,
                                                             @Param("fechaInicio") LocalDate fechaInicio,
                                                             @Param("fechaFin") LocalDate fechaFin);
    @Query("SELECT DISTINCT p\n" +
            "FROM Producto p\n" +
            "WHERE p.id NOT IN (\n" +
            "    SELECT r.producto.id\n" +
            "    FROM Reserva r\n" +
            "    WHERE (\n" +
            "        (r.fechaInicial BETWEEN :fechaInicio AND :fechaFin)\n" +
            "        OR (r.fechaFinal BETWEEN :fechaInicio AND :fechaFin)\n" +
            "        OR (:fechaInicio BETWEEN r.fechaInicial AND r.fechaFinal)\n" +
            "        OR (:fechaFin BETWEEN r.fechaInicial AND r.fechaFinal)\n" +
            "    )\n" +
            ")")
    List<Producto> findProductosDisponiblesByFechas(@Param("fechaInicio") LocalDate fechaInicio,
                                                    @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT MAX(r.id) FROM Reserva r")
    Long findLastId();

    List<Reserva> findByFechaInicial(LocalDate fechaInicial);
}
