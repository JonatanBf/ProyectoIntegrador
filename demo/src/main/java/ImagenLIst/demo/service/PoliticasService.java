package ImagenLIst.demo.service;

import ImagenLIst.demo.entidades.Politicas;
import ImagenLIst.demo.repository.PoliticasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PoliticasService {

    private final PoliticasRepository politicasRepository;

    public Politicas agregar(Politicas politicas){
        return politicasRepository.save(politicas);
    }

    public List<Politicas> listar(){
        return politicasRepository.findAll();
    }

    public List<Politicas> buscarNombre(String nombre){
        return politicasRepository.findByNombre(nombre);
    }

    public void modificar(Politicas p, Long id) {

        var politicasGuardada = politicasRepository.findById(id);
        var nombre = p.getNombre();
        var descripcion = p.getDescripcion();

        if (politicasGuardada.isPresent()){
            var politicas = politicasGuardada.get();
            if( nombre!= null && !nombre.equals(""))  politicas.setNombre(nombre);
            if( descripcion != null && !descripcion.isEmpty()) politicas.setDescripcion(descripcion);
            politicasRepository.save(politicasGuardada.get());
        }
    }

    public void eliminar(Long  id) {
        politicasRepository.deleteById(id);
    }

    public Optional<Politicas> buscarPorId(Long id) {
        return politicasRepository.findById(id);
    }

    public List<Politicas> buscarEnDescripcion(String cadenaBusqueda) {
        List<Politicas> resultado = new ArrayList<>();
        var politicasList = politicasRepository.findAll();
        for (Politicas politicas : politicasList) {
            var descripcionList = politicas.getDescripcion();
            for (String descripcionItem : descripcionList) {
                if (descripcionItem.contains(cadenaBusqueda))
                    resultado.add(politicas);
            }
        }
        return resultado;
    }

}
