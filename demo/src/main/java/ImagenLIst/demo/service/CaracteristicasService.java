package ImagenLIst.demo.service;

import ImagenLIst.demo.entidades.Caracteristicas;
import ImagenLIst.demo.repository.CaracteristicasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CaracteristicasService {

    private final CaracteristicasRepository caracteristicasRepository;

    public Caracteristicas agregar(Caracteristicas caracteristicas){
        return caracteristicasRepository.save(caracteristicas);
    }

    public List<Caracteristicas> listar(){
        return caracteristicasRepository.findAll();
    }

    public void modificar(Caracteristicas c, Long id) {

        var cGuardada = caracteristicasRepository.findById(id);
        var buscarNombre = caracteristicasRepository.findByNombre(c.getNombre());

        if (cGuardada.isPresent()){
            var caracteristicas = cGuardada.get();
            if(c.getNombre() != null && !c.getNombre().equals("") && buscarNombre.isEmpty())caracteristicas.setNombre(c.getNombre());
            if(c.getIcono()!= null && !c.getIcono().equals(""))caracteristicas.setIcono(c.getIcono());
            caracteristicasRepository.save(caracteristicas);
        }
    }

    public void eliminar(Long  id) {
        caracteristicasRepository.deleteById(id);
    }

    public Optional<Caracteristicas> buscarPorId(Long id) {
        return caracteristicasRepository.findById(id);
    }

    public Optional<Caracteristicas> buscarPorNombre (String nombre){
        return caracteristicasRepository.findByNombre(nombre);
    }
}
