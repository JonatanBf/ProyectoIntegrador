package ImagenLIst.demo.service;

import ImagenLIst.demo.entidades.Ciudad;
import ImagenLIst.demo.repository.CiudadRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CiudadService {

    private final CiudadRepository ciudadRepository;

    public Ciudad agregar(Ciudad c){
        return ciudadRepository.save(c);
    }

    public List<Ciudad> listar() {
        return ciudadRepository.findAll();
    }

    public void modificar(Ciudad c, Long id) {

        var ciudadGuardada = ciudadRepository.findById(id);
        var buscarCiudad = ciudadRepository.findByCiudad(c.getNombre());

        if (ciudadGuardada.isPresent()){
            var ciudad = ciudadGuardada.get();
            if(c.getNombre() != null && !c.getNombre().equals("") && buscarCiudad.isEmpty())ciudad.setNombre(c.getNombre());
            if(c.getPais()!= null && !c.getPais().equals(""))ciudad.setPais(c.getPais());
            ciudadRepository.save(ciudad);
        }
    }

    public void eliminar(Long  id) {
        ciudadRepository.deleteById(id);
    }

    public Optional<Ciudad> buscarPorId(Long id) {
        return ciudadRepository.findById(id);
    }

    public Optional<Ciudad> buscarPorCiudad (String ciudad){
        return ciudadRepository.findByCiudad(ciudad);
    }

}

