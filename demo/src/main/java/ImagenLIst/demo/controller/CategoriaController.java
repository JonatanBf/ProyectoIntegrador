package ImagenLIst.demo.controller;

import ImagenLIst.demo.entidades.Categoria;
import ImagenLIst.demo.repository.ProductoRepository;
import ImagenLIst.demo.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = { "http://127.0.0.1:5173", "http://localhost:5173", "http://equipo7-tomi.s3-website.us-east-2.amazonaws.com",  "http://localhost:8080"})
@RestControllerAdvice
@Validated
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final ProductoRepository productoRepository;

    @GetMapping("/listar")
    public List<Categoria> listar(){
        return categoriaService.listar();
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Categoria categoria) {

        String titulo = categoria.getTitulo();
        Optional<Categoria> categoriaOptional = categoriaService.buscarTitulo(titulo);

        if (categoriaOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Titulo: " + titulo + " ya existen en nuestros registros.");
        }
        categoriaService.agregar(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body("La Categoria se guardo correctamente.");

    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@Valid @RequestBody Categoria categoria, @PathVariable Long id) {

        String titulo = categoria.getTitulo();
        Optional<Categoria> categoriaOptional = categoriaService.buscarPorId(id);

        if (categoriaOptional.isPresent()) {

            Optional<Categoria> categoriaConMismoNombre = categoriaService.buscarTitulo(titulo);

            if (categoriaConMismoNombre.isPresent()) {
                String mensajeDeError = "Categoria con Titulo : " + titulo + " ya existe en nuestros registros";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(mensajeDeError);
            }
            categoriaService.modificar(categoria, id);
            String mensajeDeExito = "Categoria se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Categoria para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Categoria> categoriaOptional = categoriaService.buscarPorId(id);

        if (categoriaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ninguna Categoria");
        }
        if (productoRepository.findByCategoria_Id(id).isEmpty()){
            categoriaService.eliminar(id);
            return ResponseEntity.ok().body("Categoria con Id: " + id + " se eliminó con éxito");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id : " + id + ", no puede eliminarse ya que cuenta con productos asociados");
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Categoria> categoriaOptional = categoriaService.buscarPorId(id);

        return categoriaOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ninguna Categoria")
                : ResponseEntity.ok(categoriaOptional);
    }

    @GetMapping("/buscarTitulo/{titulo}")
    public ResponseEntity<?> buscarTitulo(@PathVariable String titulo){

        Optional<Categoria> categoriaOptional= categoriaService.buscarTitulo(titulo);

        return categoriaOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria con Titulo : " + titulo+ ", no pertenece a ninguna Categoria")
                : ResponseEntity.ok(categoriaOptional);
    }

    @GetMapping("/buscarDescripcion/{descripcion}")
    public ResponseEntity<?> buscarDescripcion(@PathVariable String descripcion){

        Optional<Categoria> categoriaOptional = categoriaService.buscarDescripcion(descripcion);

        return categoriaOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria con Descripcion : " + descripcion+ ", no pertenece a ninguna Categoria")
                : ResponseEntity.ok(categoriaOptional);
    }

}