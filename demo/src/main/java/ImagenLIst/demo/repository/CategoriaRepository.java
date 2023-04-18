package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("select c FROM Categoria c where c.titulo = ?1")
    public Optional<Categoria> findByTitulo(String titulo);

    @Query("select c FROM Categoria c where c.descripcion = ?1")
    public Optional<Categoria> findByDescripcion(String descripcion);
}
