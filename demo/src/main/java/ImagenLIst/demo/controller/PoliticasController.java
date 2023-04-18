package ImagenLIst.demo.controller;

import ImagenLIst.demo.entidades.Politicas;
import ImagenLIst.demo.repository.ProductoRepository;
import ImagenLIst.demo.service.PoliticasService;
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
@RequestMapping("/politicas")
public class PoliticasController {

    private final PoliticasService politicasService;

    private final ProductoRepository productoRepository;

    @GetMapping("/listar")
    public List<Politicas> listar(){
        return politicasService.listar();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Politicas> politicasOptional = politicasService.buscarPorId(id);

        if (politicasOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ninguna Politica");
        }
        if (productoRepository.findByPoliticaId(id).isEmpty()){
            politicasService.eliminar(id);
            return ResponseEntity.ok().body("Politica con Id: " + id + " se eliminó con éxito");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id : " + id + ", no puede eliminarse ya que cuenta con productos asociados");
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Politicas> politicasOptional = politicasService.buscarPorId(id);

        return politicasOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ninguna Politica")
                : ResponseEntity.ok(politicasOptional);
    }

    @GetMapping("/buscarNombre/{nombre}")
    public ResponseEntity<?> buscarNombre(@PathVariable String nombre){

       List<Politicas> politicasNombre = politicasService.buscarNombre(nombre);

        return politicasNombre.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Politicas con Nombre : " + nombre+ ", no pertenece a ninguna Politica")
                : ResponseEntity.ok(politicasNombre);
    }

    @GetMapping("/buscarDescripcion/{descripcion}")
    public ResponseEntity<?> buscarDescripcion(@PathVariable String descripcion){

        List<Politicas> politicasList= politicasService.buscarEnDescripcion(descripcion);

        return politicasList.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Politicas con Descripcion : " + descripcion+ ", no pertenece a ninguna Politicas")
                : ResponseEntity.ok(politicasList);
    }

}


