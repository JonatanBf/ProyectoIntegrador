package ImagenLIst.demo.controller;

import ImagenLIst.demo.entidades.Rol;
import ImagenLIst.demo.service.RolService;
import ImagenLIst.demo.service.UsuarioService;
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
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;
    private final UsuarioService usuarioService;

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody Rol rol) {

        Optional<Rol> rolOptional= rolService.getByNombre(rol.getNombre());

        if (rolOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nombre: " + rol.getNombre() + " ya existen en nuestros registros.");
        }
        rolService.agregar(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body("Rol se guardó correctamente.");

    }

    @GetMapping("/listar")
    public List<Rol> listar(){
        return rolService.listar();
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@RequestBody Rol rol, @PathVariable Long id) {

        Optional<Rol> rolOptional = rolService.buscarPorId(id);

        if (rolOptional.isPresent()) {

            Optional<Rol> rolConMismoNombre= rolService.getByNombre(rol.getNombre());

            if (rolConMismoNombre.isPresent()) {
                String mensajeDeError = "Rol con Nombre : " + rol.getNombre() + " ya existe en nuestros registros";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(mensajeDeError);
            }
            rolService.modificar(rol, id);
            String mensajeDeExito = "Rol se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Rol para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Rol> rolOptional = rolService.buscarPorId(id);

        if (rolOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ningun Rol");
        }
        if(!usuarioService.findByRol_Id(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id : " + id + ", no puede eliminarse ya que cuenta con Usuarios asociados");
        }
        rolService.eliminar(id);
        return ResponseEntity.ok().body("Rol con Id: " + id + " se eliminó con éxito");
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Rol> rolOptional = rolService.buscarPorId(id);

        return rolOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ningun Rol")
                : ResponseEntity.ok(rolOptional);
    }

    @GetMapping("/buscarNombre/{nombre}")
    public ResponseEntity<?> buscarNombre(@PathVariable String nombre){

        Optional<Rol> rolOptional= rolService.getByNombre(nombre);

        return rolOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nombre : " + nombre+ ", no pertenece a ningun Rol")
                : ResponseEntity.ok(rolOptional);
    }
}
