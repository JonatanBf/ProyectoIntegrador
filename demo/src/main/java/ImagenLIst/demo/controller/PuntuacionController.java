package ImagenLIst.demo.controller;

import ImagenLIst.demo.Dto.PuntuacionDto;
import ImagenLIst.demo.entidades.Producto;
import ImagenLIst.demo.entidades.Puntuacion;
import ImagenLIst.demo.entidades.Usuario;
import ImagenLIst.demo.service.ProductoService;
import ImagenLIst.demo.service.PuntuacionService;
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
@RequestMapping("/puntuacion")
public class PuntuacionController {

    private final PuntuacionService puntuacionService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody PuntuacionDto puntuacionDto) {

        Long idProducto = puntuacionDto.getId_Producto();
        Long idUsuario = puntuacionDto.getId_Usuario();

        Optional<Producto> productoOptional = productoService.buscarPorId(idProducto);
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(idUsuario);

        if (productoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El idProducto : " + idProducto + " no pertenece a ningun Producto");
        }
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El idUsuario : " + idUsuario + " no pertenece a ningun Usuario");
        }
        if (puntuacionService.existsByUsuarioAndProducto(idUsuario,idProducto)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ya existe una puntuacion del idUsuario : " + idUsuario + ", para el idProducto: "+idProducto);
        }
        puntuacionService.agregarDTO(puntuacionDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Puntuacion se guardó con éxito");
    }

    @PostMapping("/agregarPuntuacionRandom")
    public ResponseEntity<?> agregarPuntuacionRandom() {

        int maxPuntuacion = 5;

        int contador = 0; // Variable contador para llevar la cuenta de las puntuaciones creadas

        for (int i = 1; contador < 20 && i <= 100; i++) { // El ciclo for continúa mientras el contador sea menor que 20 y no se hayan hecho más de 100 iteraciones

            Long usuarioLong = (long) (Math.random() * usuarioService.listar().size()) + 1;
            Long productoLong = (long) (Math.random() * productoService.listar().size()) + 1;
            int puntuacion = (int) (Math.random() * maxPuntuacion) + 1;

            PuntuacionDto puntuacionDto = new PuntuacionDto();
            puntuacionDto.setId_Usuario(usuarioLong);
            puntuacionDto.setId_Producto(productoLong);
            puntuacionDto.setPuntuacion((long) puntuacion);

            if (!puntuacionService.existsByUsuarioAndProducto(puntuacionDto.getId_Usuario(),puntuacionDto.getId_Producto())) {
                Puntuacion nuevaPuntuacion = puntuacionService.agregarDTO(puntuacionDto);
                contador++; // Se incrementa el contador cada vez que se crea una nueva puntuación
                System.out.println("Puntuacion "+contador+" creada con éxito\n");
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Se agregaron "+contador+" puntuaciones aleatorias con éxito");
    }

    @GetMapping("/listar")
    public List<Puntuacion> listar(){
        return puntuacionService.listar();
    }

    @PatchMapping("/modificarProducto/{id}")
    public ResponseEntity<?> modificarProducto(@RequestBody PuntuacionDto puntuacionDto, @PathVariable Long id) {

        Optional<Puntuacion> puntuacionOptional = puntuacionService.buscarPorId(id);

        if (puntuacionOptional.isPresent()) {

            Puntuacion puntuacionUpdate = puntuacionService.buscarPorId(id).get();

            Long productoNuevo = puntuacionDto.getId_Producto();

            Optional<Producto> producto = productoService.buscarPorId(productoNuevo);

            if (producto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe Producto con Id : " + productoNuevo);
            }
            List<Puntuacion> productoRepetido = puntuacionService.buscarProductoId(productoNuevo);

            if (!productoRepetido.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ya existe una puntuacion del idUsuario : " + puntuacionUpdate.getUsuario().getId()+ ", para el idProducto: "+productoNuevo);
            }
            puntuacionService.modificarProducto(productoNuevo,id);
            String mensajeDeExito = "Producto se modifico con exito para Puntuacion_Id: "+id;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Puntuacion para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificarUsuario/{id}")
    public ResponseEntity<?> modificarUsuario(@RequestBody PuntuacionDto puntuacionDto, @PathVariable Long id) {

        Optional<Puntuacion> puntuacionOptional = puntuacionService.buscarPorId(id);

        if (puntuacionOptional.isPresent()) {

            Puntuacion puntuacionUpdate = puntuacionService.buscarPorId(id).get();

            Long usuarioNuevo = puntuacionDto.getId_Usuario();

            Optional<Usuario> usuario = usuarioService.buscarPorId(usuarioNuevo);

            if (usuario.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe Usuario con Id : " + usuarioNuevo);
            }

            List<Puntuacion> productoRepetido = puntuacionService.buscarProductoId(puntuacionUpdate.getProducto().getId());

            if (!productoRepetido.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ya existe una puntuacion del idUsuario : " + usuarioNuevo+ ", para el idProducto: "+puntuacionUpdate.getProducto().getId());
            }
            puntuacionService.modificarUsuario(usuarioNuevo,id);
            String mensajeDeExito = "Usuario se modifico con exito para Puntuacion_Id: "+id;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Puntuacion para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificarPuntuacion/{id}")
    public ResponseEntity<?> modificarPuntuacion( @RequestBody PuntuacionDto puntuacionDto, @PathVariable Long id) {

        Optional<Puntuacion> puntuacionOptional = puntuacionService.buscarPorId(id);

        if (puntuacionOptional.isPresent()) {
            if (puntuacionDto.getPuntuacion() ==null || puntuacionDto.getPuntuacion() > 5 || puntuacionDto.getPuntuacion()== 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Puntuacion: "+puntuacionDto.getPuntuacion()+ ", no debe ser mayor a 0 y menor que 6");
            }
            puntuacionService.modificarPuntuacion(puntuacionDto.getPuntuacion(), id);
            String mensajeDeExito = "Puntuacion se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Puntuacion para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id){

        Optional<Puntuacion> puntuacionOptional = puntuacionService.buscarPorId(id);

        if (puntuacionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ninguna Puntuacion");
        }
        puntuacionService.eliminar(id);
        return ResponseEntity.ok().body("Puntuacion con Id: " + id + " se eliminó con éxito");
    }

    @DeleteMapping("/eliminarByUsuario_Id/{id}")
    public ResponseEntity<String> eliminarByUsuarioId(@PathVariable Long id){

        List<Puntuacion> puntuacionList = puntuacionService.buscarUsuarioId(id);

        if (puntuacionList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario_Id : " + id+ ", aun no ha realizado ninguna puntuacion");
        }
        for (Puntuacion puntuacion : puntuacionList){
            puntuacionService.eliminar(puntuacion.getId());
        }
        return ResponseEntity.ok().body("Puntuacion con Usuario_Id: " + id + " se eliminó con éxito");
    }

    @DeleteMapping("/eliminarByProducto_Id/{id}")
    public ResponseEntity<String> eliminarByProductoId(@PathVariable Long id){

        List<Puntuacion> productoList = puntuacionService.buscarProductoId(id);

        if (productoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto_Id : " + id+ ", aun no posee ninguna puntuacion");
        }
        for (Puntuacion puntuacion : productoList){
            puntuacionService.eliminar(puntuacion.getId());
        }
        return ResponseEntity.ok().body("Puntuacion con Producto_Id: " + id + " se eliminó con éxito");
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Puntuacion> puntuacionOptional = puntuacionService.buscarPorId(id);

        return puntuacionOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ninguna Puntuacion")
                : ResponseEntity.ok(puntuacionOptional);
    }

    @GetMapping("/buscarUsuarioId/{id}")
    public ResponseEntity<?> buscarUsuarioId(@PathVariable Long id){

        List<Puntuacion> puntuacionOptional = puntuacionService.buscarUsuarioId(id);

        return puntuacionOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("idUsuario : " + id+ ", no realizo ninguna Puntuacion")
                : ResponseEntity.ok(puntuacionOptional);
    }

    @GetMapping("/buscarProductoId/{id}")
    public ResponseEntity<?> buscarProductoId(@PathVariable Long id){

        List<Puntuacion> puntuacionOptional = puntuacionService.buscarProductoId(id);

        return puntuacionOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("idProducto : " + id+ ", aun no posee puntuacion ")
                : ResponseEntity.ok(puntuacionOptional);
    }

    @GetMapping("/buscarPuntuacion/{puntuacion}")
    public ResponseEntity<?> buscarPuntuacion(@PathVariable Long puntuacion){

        List<Puntuacion> puntuacionOptional = puntuacionService.buscarPuntuacion(puntuacion);

        return puntuacionOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe producto con puntuacion: "+puntuacion)
                : ResponseEntity.ok(puntuacionOptional);
    }

    @GetMapping("/promedio")
    public ResponseEntity<String> obtenerPromedioAlls() {
        String resultado = puntuacionService.obtenerPromedioAlls();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/promedioPorProductoId")
    public ResponseEntity<?> obtenerPromedioPuntuacionPorProducto(@RequestParam Long productoId) {
        Double resultado = puntuacionService.obtenerPromedioPorProducto(productoId);

        return resultado== null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("El productoId: "+productoId+", aun no ha recibido puntuaciones")
                : ResponseEntity.ok(resultado);
    }

}

