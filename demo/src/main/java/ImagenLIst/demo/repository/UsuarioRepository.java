package ImagenLIst.demo.repository;

import ImagenLIst.demo.entidades.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND TYPE(u) <> Cliente")
    public Optional<Usuario> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("DELETE FROM Usuario u WHERE u.id = :idCliente AND TYPE(u) <> Usuario")
    public void deleteByIdCliente(@Param("idCliente") Long idCliente);

    @Query("SELECT u FROM Usuario u WHERE TYPE(u) <> Cliente")
    public List<Usuario> listar();

    @Query("SELECT u FROM Usuario u WHERE TYPE(u) <> Usuario")
    public List<Usuario> listarClientes();

    public Optional<List<Usuario>> findByNombre(String nombre);

    public Optional<List<Usuario>> findByApellido(String apellido);

    @Query("select u FROM Usuario u where u.rol.id = ?1")
    public List<Usuario> findByRol_Id(Long idRol);

    @Query("select u FROM Usuario u where u.rol.nombre = ?1")
    public List<Usuario> findByRol_Nombre(String rolNombre);

    @Query("select u FROM Usuario u where u.ciudad.id = ?1")
    public List<Usuario> findByCiudad_Id(Long idCiudad);

    @Query("select u FROM Usuario u where u.ciudad.nombre = ?1")
    public List<Usuario> findByCiudad_Nombre(String ciudadNombre);

    @Query("SELECT MAX(u.id) FROM Usuario u")
    Long findLastId();
}
