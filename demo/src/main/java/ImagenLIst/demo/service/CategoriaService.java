package ImagenLIst.demo.service;

import ImagenLIst.demo.entidades.Categoria;
import ImagenLIst.demo.repository.CategoriaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> listar(){
        return categoriaRepository.findAll();
    }

    public Categoria agregar(Categoria c){
        return categoriaRepository.save(c);
    }

    public Optional<Categoria> buscarTitulo (String titulo){
        return categoriaRepository.findByTitulo(titulo);
    }

    public Optional<Categoria> buscarDescripcion (String descripcion){
        return categoriaRepository.findByDescripcion(descripcion);
    }

    public void modificar(Categoria c, Long id) {

        var categoriaGuardada = categoriaRepository.findById(id);
        if (categoriaGuardada.isPresent()){
            var categoria = categoriaGuardada.get();
            if(c.getTitulo()!= null && !c.getTitulo().equals(""))  categoria.setTitulo(c.getTitulo());
            if(c.getDescripcion()!= null && !c.getDescripcion().equals(""))  categoria.setDescripcion(c.getDescripcion());
            if(c.getUrl()!= null && !c.getUrl().equals(""))categoria.setUrl(c.getUrl());
            categoriaRepository.save(categoria);
        }
    }
    public void eliminar(Long  id) {
        categoriaRepository.deleteById(id);
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }
}
