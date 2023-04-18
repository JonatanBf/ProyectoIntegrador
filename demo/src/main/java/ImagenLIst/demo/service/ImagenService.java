package ImagenLIst.demo.service;

import ImagenLIst.demo.entidades.Imagen;
import ImagenLIst.demo.repository.ImagenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImagenService {

    private final ImagenRepository imagenRepository;

    public List<Imagen> listar(){
        return imagenRepository.findAll();
    }

    public void modificar(Imagen i, Long id) {
        var imagenGuardada = imagenRepository.findById(id);
        if (imagenGuardada.isPresent()){
            var imagen = imagenGuardada.get();
            if( i.getTitulo()!= null && !i.getTitulo().equals(""))  imagen.setTitulo(i.getTitulo());
            if( i.getUrl() != null && !i.getUrl().equals("")) imagen.setUrl(i.getUrl());
            imagenRepository.save(imagen);
        }
    }

    public void eliminar(Long  id) {
        imagenRepository.deleteById(id);
    }

    public Optional<Imagen> buscarPorId(Long id) {
        return imagenRepository.findById(id);
    }

    public List<Imagen> buscarTitulo(String titulo){
        return imagenRepository.findByTitulo(titulo);
    }

    public void eliminarPorIds(List<Long> idsImagen) {
        imagenRepository.deleteByIdIn(idsImagen);
    }

}
