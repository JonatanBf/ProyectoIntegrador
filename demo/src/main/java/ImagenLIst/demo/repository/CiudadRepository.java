package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Long> {

    @Query("select c FROM Ciudad c where c.nombre = ?1")
    public Optional<Ciudad> findByCiudad(String titulo);
}
