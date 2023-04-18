package ImagenLIst.demo.controller;

import ImagenLIst.demo.entidades.Imagen;
import ImagenLIst.demo.service.ImagenService;
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
@RequestMapping("/imagen")
public class ImagenController {

    private final ImagenService imagenService;

    @GetMapping("/listar")
    public List<Imagen> listar(){
        return imagenService.listar();
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@RequestBody Imagen imagen, @PathVariable Long id) {

        Optional<Imagen> imagenOptional = imagenService.buscarPorId(id);

        if (imagenOptional.isPresent()) {
            imagenService.modificar(imagen, id);
            String mensajeDeExito = "La Imagen se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Imagen para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Imagen> imagenOptional = imagenService.buscarPorId(id);

        return imagenOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ninguna Imagen de Producto")
                : ResponseEntity.ok(imagenOptional);
    }

    @GetMapping("/buscarTitulo/{titulo}")
    public ResponseEntity<?> buscarTitulo(@PathVariable String titulo){

        List<Imagen> imagenList= imagenService.buscarTitulo(titulo);

        return imagenList.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Imagen con Titulo : " + titulo+ ", no pertenece a ninguna Imagen de Producto")
                : ResponseEntity.ok(imagenList);
    }

}
