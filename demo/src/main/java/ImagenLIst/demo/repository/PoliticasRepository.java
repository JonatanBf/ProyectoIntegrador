package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Politicas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoliticasRepository extends JpaRepository<Politicas, Long> {

    @Query("select p FROM Politicas p where p.nombre = ?1")
    public List<Politicas> findByNombre(String nombre);
}

