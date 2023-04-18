package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("select p FROM Producto p where p.ciudad.nombre = ?1")
    public List<Producto> getByCiudad(String nombre);

    @Query("select p FROM Producto p where p.ciudad.id = ?1")
    public List<Producto> findByCiudad_Id(Long idCiudad);

    @Query("SELECT p FROM Producto p JOIN p.caracteristicas c WHERE c.id = :caracteristicaId")
    public List<Producto> findByCaracteristicaId(@Param("caracteristicaId") Long caracteristicaId);

    @Query("SELECT p FROM Producto p JOIN p.politicas c WHERE c.id = :politicaId")
    public List<Producto> findByPoliticaId(@Param("politicaId") Long politicaId);

    @Query("select p FROM Producto p where p.categoria.id = ?1")
    public List<Producto> findByCategoria_Id(Long idCategoria);

    @Query("select p FROM Producto p where p.nombre = ?1")
    public List<Producto> getByNombre(String nombre);

    @Query("select p FROM Producto p where p.descripcion = ?1")
    public List<Producto> getByDescripcion(String descripcion);

    List<Producto> findAllByCaracteristicas_IdIn(List<Long> idsCaracteristicas);

    @Query("SELECT p FROM Producto p WHERE p.categoria.id IN :idsCategorias")
    List<Producto> findAllByCategoriaIds(@Param("idsCategorias") List<Long> idsCategorias);

    @Query("SELECT p FROM Producto p WHERE p.ciudad.id IN :idsCiudades")
    List<Producto> findAllByCiudadIds(@Param("idsCiudades") List<Long> idsCiudades);

    @Query(value = "SELECT p FROM Producto p ORDER BY RAND()")
    List<Producto> findRandomProductos();

}
