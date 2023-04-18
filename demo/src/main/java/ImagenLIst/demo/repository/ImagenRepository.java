package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {

    @Query("select i FROM Imagen i where i.titulo = ?1")
    public List<Imagen> findByTitulo(String titulo);

    public void deleteByIdIn(List<Long> ids);
}
