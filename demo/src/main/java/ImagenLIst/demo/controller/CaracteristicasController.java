package ImagenLIst.demo.controller;

import ImagenLIst.demo.entidades.Caracteristicas;
import ImagenLIst.demo.repository.ProductoRepository;
import ImagenLIst.demo.service.CaracteristicasService;
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
@CrossOrigin(origins = { "http://127.0.0.1:5173", "http://localhost:5173", "http://equipo7-tomi.s3-website.us-east-2.amazonaws.com", "http://localhost:8080"})
@RestControllerAdvice
@Validated
@RequestMapping("/caracteristicas")
public class CaracteristicasController {

    private final CaracteristicasService caracteristicasService;
    private final ProductoRepository productoRepository;

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Caracteristicas c) {

        Optional<Caracteristicas> caracteristicasOptional= caracteristicasService.buscarPorNombre(c.getNombre());

        if (caracteristicasOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La característica " + c.getNombre() + " ya existen en nuestros registros.");
        }
        caracteristicasService.agregar(c);
        return ResponseEntity.status(HttpStatus.CREATED).body("La característica se guardaron correctamente.");

    }

    @GetMapping("/listar")
    public List<Caracteristicas> listar(){
        return caracteristicasService.listar();
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@Validated(Caracteristicas.ValidationGroup.class) @RequestBody Caracteristicas c, @PathVariable Long id) {
        Optional<Caracteristicas> getId = caracteristicasService.buscarPorId(id);

        Optional<Caracteristicas> buscarNombre = caracteristicasService.buscarPorNombre(c.getNombre());

        if (getId.isPresent()) {
            if (buscarNombre.isPresent()) {
                return new ResponseEntity<>("La Caracteristica : " + c.getNombre() + " ya existe en nuestros registros", null, HttpStatus.BAD_REQUEST);
            }
            caracteristicasService.modificar(c, id);
            return new ResponseEntity<>("La Caracteristica se modifico con exito", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("No existe Caracteristica para el Id: " + id, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Caracteristicas> caracteristicasOptional = caracteristicasService.buscarPorId(id);

        if (caracteristicasOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id + ", no tiene asignado ninguna Caracteristica");
          }
        if (productoRepository.findByCaracteristicaId(id).isEmpty()){
            caracteristicasService.eliminar(id);
            return ResponseEntity.ok().body("Caracteristica con Id: " + id + " se eliminó con éxito");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id : " + id + ", no puede eliminarse ya que cuenta con productos asociados");
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Caracteristicas> caracteristicasOptional = caracteristicasService.buscarPorId(id);

        return caracteristicasOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a  ninguna Caracteristica")
                : ResponseEntity.ok(caracteristicasOptional);
    }

    @GetMapping("/buscarNombre/{nombre}")
    public ResponseEntity<?> buscarNombre(@PathVariable String nombre){

        Optional<Caracteristicas> caracteristicasOptional  = caracteristicasService.buscarPorNombre(nombre);

        return caracteristicasOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nombre : " + nombre+ ", no tiene asignado ninguna Caracteristica")
                : ResponseEntity.ok(caracteristicasOptional);
    }
}
