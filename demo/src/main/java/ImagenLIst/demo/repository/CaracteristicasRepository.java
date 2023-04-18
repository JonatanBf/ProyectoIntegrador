package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Caracteristicas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaracteristicasRepository extends JpaRepository<Caracteristicas, Long> {

    @Query("select c FROM Caracteristicas c where c.nombre = ?1")
    public Optional<Caracteristicas> findByNombre(String titulo);
}
