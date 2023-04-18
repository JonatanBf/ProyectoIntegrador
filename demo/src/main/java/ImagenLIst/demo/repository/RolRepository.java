package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    @Query("select r FROM Rol r where r.nombre = ?1")
    Optional<Rol> getByNombre(String nombre);

}
