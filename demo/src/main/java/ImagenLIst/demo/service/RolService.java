package ImagenLIst.demo.service;

import ImagenLIst.demo.entidades.Rol;
import ImagenLIst.demo.repository.RolRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    public Rol agregar(Rol rol) {
        return rolRepository.save(rol);
    }

    public Optional<Rol> getByNombre(String nombre) {
        return rolRepository.getByNombre(nombre);
    }

    public Optional<Rol> buscarPorId(Long id) {
        return rolRepository.findById(id);
    }

    public void modificar(Rol rol, Long id) {

        var rolNew = rolRepository.findById(id).get();

        if(rol.getNombre() != null & !rol.getNombre().equals("")) rolNew.setNombre(rol.getNombre());
        rolRepository.save(rolNew);
    }

    public void eliminar(Long  id) {
        rolRepository.deleteById(id);
    }
}
