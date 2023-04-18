package ImagenLIst.demo.controller;

import ImagenLIst.demo.entidades.Ciudad;
import ImagenLIst.demo.repository.ProductoRepository;
import ImagenLIst.demo.repository.UsuarioRepository;
import ImagenLIst.demo.service.CiudadService;
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
@RequestMapping("/ciudad")
public class CiudadController {

    private final CiudadService ciudadService;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Ciudad c) {

        String nombre = c.getNombre();
        Optional<Ciudad> ciudadOptional= ciudadService.buscarPorCiudad(nombre);

        if (ciudadOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nombre: " + nombre + " ya existen en nuestros registros.");
        }
        ciudadService.agregar(c);
        return ResponseEntity.status(HttpStatus.CREATED).body("La Ciudad se guardo correctamente.");

    }

    @GetMapping("/listar")
    public List<Ciudad> listar(){
        return ciudadService.listar();
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@RequestBody Ciudad c, @PathVariable Long id) {

        String nombre = c.getNombre();
        Optional<Ciudad> ciudadOptional = ciudadService.buscarPorId(id);

        if (ciudadOptional.isPresent()) {

            Optional<Ciudad> ciudadConMismoNombre= ciudadService.buscarPorCiudad(nombre);

            if (ciudadConMismoNombre.isPresent()) {
                String mensajeDeError = "Ciudad con Nombre : " + nombre + " ya existe en nuestros registros";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(mensajeDeError);
            }
            ciudadService.modificar(c, id);
            String mensajeDeExito = "La Ciudad se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Ciudad para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Ciudad> ciudadOptional = ciudadService.buscarPorId(id);

        if (ciudadOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ninguna Ciudad");
        }
        if (!productoRepository.findByCiudad_Id(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id : " + id + ", no puede eliminarse ya que cuenta con Productos asociados");
        }
        if (!usuarioRepository.findByCiudad_Id(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id : " + id + ", no puede eliminarse ya que cuenta con Usuarios asociados");
        }
        ciudadService.eliminar(id);
        return ResponseEntity.ok().body("Ciudad con Id: " + id + " se eliminó con éxito");
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Ciudad> ciudadOptional = ciudadService.buscarPorId(id);

        return ciudadOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ninguna Ciudad")
                : ResponseEntity.ok(ciudadOptional);
    }

    @GetMapping("/buscarCiudad/{ciudad}")
    public ResponseEntity<?> buscarCiudad(@PathVariable String ciudad){

        Optional<Ciudad> ciudadOptional= ciudadService.buscarPorCiudad(ciudad);

        return ciudadOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ciudad con Nombre : " + ciudad+ ", no pertenece a ninguna Ciudad")
                : ResponseEntity.ok(ciudadOptional);
    }

}
