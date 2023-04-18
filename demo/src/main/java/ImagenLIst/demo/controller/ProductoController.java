package ImagenLIst.demo.controller;

import ImagenLIst.demo.Dto.ProductoDto;
import ImagenLIst.demo.entidades.*;
import ImagenLIst.demo.service.*;
import jakarta.mail.MessagingException;
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
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService productoService;
    private final CiudadService ciudadService;
    private final CategoriaService categoriaService;
    private final ImagenService imagenService;

    private final ReservaService reservaService;
    private final PuntuacionService puntuacionService;

    @PostMapping("/agregar")
    public ResponseEntity<String> agregar(@Valid @RequestBody ProductoDto productoDto) {

        List<Long> arrayCaracteristicas = productoDto.getCaracteristicas();

        Long idCiudad = productoDto.getId_Ciudad();
        Long idCategoria = productoDto.getId_Categoria();

        Optional<Ciudad> ciudad = ciudadService.buscarPorId(idCiudad);
        Optional<Categoria> categoria = categoriaService.buscarPorId(idCategoria);

        List<Long> idexistentesCaracteristicas = productoService.obtenerIdsInexistentesCaracteristicas(arrayCaracteristicas);
        List<Long> idRepetidosCaracteristicas = productoService.buscarIdsRepetidos(arrayCaracteristicas);

        if (!idRepetidosCaracteristicas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ids repetidos para Caracteristicas: " + idRepetidosCaracteristicas);
        }
        if (!idexistentesCaracteristicas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ids inexistentes para Caracteristicas: " + idexistentesCaracteristicas);
        }
        if (ciudad.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El idCiudad : " + idCiudad + " no pertenece a ninguna Ciudad");
        }
        if (categoria.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El idCategoria : " + idCategoria + " no pertenece a ninguna Categoria");
        }
        Producto nuevoProducto = productoService.agregarDto(productoDto);
        if (nuevoProducto == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al intentar guardar el Producto.");
        }
            return ResponseEntity.status(HttpStatus.CREATED)
                .body("El Producto se guardó con éxito");
    }

    @PutMapping("/modificarProductoCompleto/{id}")
    public ResponseEntity<?> modificarProductoCompleto(@RequestBody ProductoDto productoDto, @PathVariable Long id){
        Optional<Producto> productoOptional = productoService.buscarPorId(id);
        if (productoOptional.isPresent()) {
            productoService.modificarProductoCompleto(productoDto,id);
            String mensajeDeExito = "El producto se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Producto para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@RequestBody ProductoDto productoDto, @PathVariable Long id) {

        Optional<Producto> productoOptional = productoService.buscarPorId(id);

        if (productoOptional.isPresent()) {

            String nombre = productoDto.getNombre();
            String descripcion = productoDto.getDescripcion();
            String latitud = productoDto.getLatitud();
            String longitud = productoDto.getLongitud();
            String direccion = productoDto.getDireccion();
            productoService.modificarProducto(id,nombre,descripcion,latitud,longitud,direccion);
            String mensajeDeExito = "El producto se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Producto para el Id: " + id;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificarCaracteristicas/{id}")
    public ResponseEntity<?> modificarCaracteristicas(@RequestBody ProductoDto productoDto, @PathVariable Long id) {

        Optional<Producto> productoOptional = productoService.buscarPorId(id);

        if (productoOptional.isPresent()) {

            Producto productoNuevo = productoService.buscarPorId(id).get();

            List<Long> idC = productoDto.getCaracteristicas();

            List<Long> idexistentesCaracteristicas = productoService.obtenerIdsInexistentesCaracteristicas(idC);
            List<Long> idRepetidosCaracteristicas = productoService.buscarIdsRepetidos(idC);

            if (!idRepetidosCaracteristicas.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ids repetidos para Caracteristicas: " + idRepetidosCaracteristicas);
            }
            if (!idexistentesCaracteristicas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ids inexistentes para Caracteristicas: " + idexistentesCaracteristicas);
            }
            productoService.modificarCaracteristicas(idC, id);
            String mensajeDeExito = "Las caracteristicas se modificaron con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Producto para el Id: " + id;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificarCiudad/{id}")
    public ResponseEntity<?> modificarCiudad(@RequestBody ProductoDto productoDto, @PathVariable Long id) {

        Optional<Producto> productoOptional = productoService.buscarPorId(id);

        if (productoOptional.isPresent()) {

            Producto productoNuevo = productoService.buscarPorId(id).get();

            Long ciudad = productoDto.getId_Ciudad();

            if (ciudadService.buscarPorId(ciudad).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe Ciudad con Id : " + ciudad);
            }
            productoService.modificarCiudad(ciudad, id);
            String mensajeDeExito = "La Ciudad se modifico con exito para el Producto_Id: "+id;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Producto para el Id: " + id;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificarCategoria/{id}")
    public ResponseEntity<?> modificarCategoria(@RequestBody ProductoDto productoDto, @PathVariable Long id) {

        Optional<Producto> productoOptional = productoService.buscarPorId(id);

        if (productoOptional.isPresent()) {

            Producto productoNuevo = productoService.buscarPorId(id).get();

            Long categoria = productoDto.getId_Categoria();

            if (categoriaService.buscarPorId(categoria).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe Categoria con Id : " + categoria);
            }
            productoService.modificarCategoria(categoria, id);
            String mensajeDeExito = "La Categoria se modifico con exito para el Producto_Id: "+id;
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Producto para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @GetMapping("/listar")
    public List<Producto> listar() {
        return productoService.listar();
    }

    @GetMapping("/listarProductosRandom")
    public List<Producto> listarProductosRandom() {
        return productoService.findRandomProductos();
    }

    @GetMapping("/buscarCiudad/{nombre}")
    public ResponseEntity<?> buscarCiudad(@PathVariable String nombre) {
        List<Producto> productosCiudad = productoService.getByCiudad(nombre);
        return productosCiudad.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("La Ciudad con Nombre: " + nombre + ", no tiene asignado ningun producto")
                : ResponseEntity.ok(productosCiudad);
    }

    @GetMapping("/buscarCaracteristicas/{nombre}")
    public ResponseEntity<?> buscarCaracteristicas(@PathVariable String nombre) {
        List<Producto> productosConCaracteristica = productoService.getByCaracteristicas(nombre);
        return productosConCaracteristica.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Caracteristicas : " + nombre + ", no tiene asignado ningun producto")
                : ResponseEntity.ok(productosConCaracteristica);
    }
    @GetMapping("/buscarTituloCategoria/{tituloCategoria}")
    public ResponseEntity<?> buscarTituloCategoria(@PathVariable String tituloCategoria) {

        List<Producto> productosTitulo = productoService.buscarPorTituloCategoria(tituloCategoria);

        return productosTitulo.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("La Categoria con Titulo: " + tituloCategoria + ", no tiene asignado ningun producto")
                : ResponseEntity.ok(productosTitulo);
    }

    @GetMapping("/buscarDescripcionCategoria/{descripcionCategoria}")
    public ResponseEntity<?> buscarDescripcionCategoria(@PathVariable String descripcionCategoria) {

        List<Producto> productos = productoService.buscarPorDescripcionCategoria(descripcionCategoria);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("La Categoria con Descripcion: " + descripcionCategoria + ", no tiene asignado ningun producto")
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/buscarTituloPoliticas/{tituloPoliticas}")
    public ResponseEntity<?> buscarTituloPoliticas(@PathVariable String tituloPoliticas) {
        List<Producto> productos = productoService.buscarPorTituloPoliticas(tituloPoliticas);
        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Politicas con Titulo : " + tituloPoliticas + ", no tiene asignado ningun producto")
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/buscarDescripcionPoliticas/{descripcionPoliticas}")
    public ResponseEntity<?> buscarDescripcionPoliticas(@PathVariable String descripcionPoliticas) {

        List<Producto> productos = productoService.buscarPorDescripcionPoliticas(descripcionPoliticas);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Politicas con Descripcion : " + descripcionPoliticas + ", no tiene asignado ningun producto")
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Producto> productoOptional = productoService.buscarPorId(id);

        return productoOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productoOptional);
    }

    @GetMapping("/buscarNombre/{nombre}")
    public ResponseEntity<?> buscarNombre(@PathVariable String nombre){

        List<Producto> productos = productoService.buscarNombre(nombre);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nombre : " + nombre+ ", no tiene asignado ningun producto")
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/buscarDescripcion/{descripcion}")
    public ResponseEntity<?> buscarDescripcion(@PathVariable String descripcion){

        List<Producto> productos = productoService.buscarDescripcion(descripcion);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Descripcion : " + descripcion+ ", no tiene asignado ningun producto")
                : ResponseEntity.ok(productos);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@RequestParam Long idProducto, @RequestParam String motivo) throws MessagingException {

        Optional<Producto> productoOptional = productoService.buscarPorId(idProducto);

        if (productoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + idProducto+ ", no tiene asignado ningun producto");
        }
        reservaService.enviarNotificacionesDeProductoCancelado(idProducto, motivo);
        List<Reserva> productoList = reservaService.buscarProductoId(idProducto);
        if(!reservaService.buscarProductoId(idProducto).isEmpty()){
            for (Reserva reserva : productoList){
                reservaService.eliminar(reserva.getId());
            }
        }
        List<Imagen> productos = productoOptional.get().getImagenes();
        for ( Imagen imagen : productos){
            imagenService.eliminar(imagen.getId());
        }
        List<Puntuacion> puntuaciones = puntuacionService.buscarProductoId(idProducto);
        for (Puntuacion puntuacion : puntuaciones){
            puntuacionService.eliminar(puntuacion.getId());
        }
        productoService.eliminar(idProducto);
            return ResponseEntity.ok().body("El Producto con Id: " + idProducto + " se eliminó con éxito");
    }

    @DeleteMapping("/eliminarCaracteristicas/{id}")
    public ResponseEntity<String> eliminarCaracteristicas(@PathVariable Long id, @RequestBody ProductoDto productoDto){

        Optional<Producto> productoOptional = productoService.buscarPorId(id);

        if (productoOptional.isPresent()) {

            List<Long> idC = productoDto.getCaracteristicas();

            List<Long> idexistentesCaracteristicas = productoService.obtenerIdsInexistentesCaracteristicas(idC);
            List<Long> idRepetidosCaracteristicas = productoService.buscarIdsRepetidos(idC);

            if (!idRepetidosCaracteristicas.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ids repetidos para Caracteristicas: " + idRepetidosCaracteristicas);
            }
            if (!idexistentesCaracteristicas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ids inexistentes para Caracteristicas: " + idexistentesCaracteristicas);
            }
             productoService.eliminarCaracteristicas(id, idC);
                return ResponseEntity.ok().body("Las caracteristicas: "+ idC+ " se eliminaron correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ningun producto");
    }

    @DeleteMapping("/eliminarPoliticas/{id}")
    public ResponseEntity<String> eliminarPoliticas(@PathVariable Long id, @RequestParam List<Long> idPoliticas){

        Optional<Producto> productoOptional = productoService.buscarPorId(id);

        if (productoOptional.isPresent()) {

            productoService.eliminarPoliticasDeProducto(id, idPoliticas);
            return ResponseEntity.ok().body("Las Politicas: "+ idPoliticas+ " se eliminaron correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ningun producto");
    }

    @DeleteMapping("/eliminarImagenes/{id}")
    public ResponseEntity<String> eliminarImagenes(@PathVariable Long id, @RequestParam List<Long> idsImagenes){

        Optional<Producto> productoOptional = productoService.buscarPorId(id);

        if (productoOptional.isPresent()) {

            List<Long> idExistentesImagenes = productoService.obtenerIdsInexistentesImagenes(idsImagenes,id);
            List<Long> idRepetidosImagenes = productoService.buscarIdsRepetidos(idsImagenes);

            if(!idRepetidosImagenes.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ids repetidos para Imagenes: " + idRepetidosImagenes);
            }
            if (!idExistentesImagenes.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ProductoId : "+id+", no tiene asignado IdImagenes: "+  idExistentesImagenes);
            }
            productoService.eliminarImagenes(id, idsImagenes);
                return ResponseEntity.ok().body("Las Politicas: "+ idsImagenes+ " se eliminaron correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ningun producto");
    }

    @GetMapping("/buscarCaracteristicas_IdIn")
    public ResponseEntity<?> findAllByCaracteristicas_IdIn(@RequestBody List<Long> idsCaracteristicas){

       List<Producto> productos = productoService.findAllByCaracteristicas_IdIn(idsCaracteristicas);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdsCaracteristicas: " + idsCaracteristicas + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/buscarCategorias_IdIn")
    public ResponseEntity<?> findAllByCategoriasIds(@RequestBody List<Long> idsCategorias){

        List<Producto> productos = productoService.findAllByCategoriaIds(idsCategorias);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdsCategorias: " + idsCategorias + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/buscarCiudades_IdIn")
    public ResponseEntity<?> findAllByCiudadesIds(@RequestBody List<Long> idsCiudades){

        List<Producto> productos = productoService.findAllByCiudadIds(idsCiudades);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdsCiudades: " + idsCiudades + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/buscarCiudad_Id/{idCiudad}")
    public ResponseEntity<?> findAllByCiudad(@RequestBody Long idCiudad){

        List<Producto> productos = productoService.findByCiudad_Id(idCiudad);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdCiudad: " + idCiudad + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productos);
    }

    // Endpoint para eliminar un item de descripción en una política
    @DeleteMapping("/eliminarItemPolitica")
    public ResponseEntity<?> eliminarDescripcionPolitica(
            @RequestParam Long productoId,
            @RequestParam Long politicaId,
            @RequestParam String descripcion) {

        Producto producto = productoService.eliminarDescripcionPolitica(productoId, politicaId, descripcion);

        Optional<Producto> productos = productoService.buscarPorId(productoId);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdProducto: " + productoId + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productos);
    }

    // Endpoint para modificar un item en la lista de descripciones de una política
    @PutMapping("/actualizarItemPolitica")
    public ResponseEntity<?> actualizarDescripcionPolitica(
            @RequestParam Long productoId,
            @RequestParam Long politicaId,
            @RequestParam String descripcion,
            @RequestParam String newDescripcion) {

        Producto producto = productoService.actualizarDescripcionPolitica(productoId, politicaId, descripcion, newDescripcion);

        Optional<Producto> productos = productoService.buscarPorId(productoId);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdProducto: " + productoId + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productos);
    }

    // Endpoint para agregar un item a la lista de descripciones de una política
    @PostMapping("/agregarItemPolitica")
    public ResponseEntity<?> agregarDescripcionPolitica(
            @RequestParam Long productoId,
            @RequestParam Long politicaId,
            @RequestParam String descripcion) {

        Producto producto = productoService.agregarDescripcionPolitica(productoId, politicaId, descripcion);

        Optional<Producto> productos = productoService.buscarPorId(productoId);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdProducto: " + productoId + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productos);
    }

    // Endpoint para agregar un item a la lista de descripciones de una política
    @PostMapping("/agregarImagen/{idProducto}")
    public ResponseEntity<?> agregarImagen(@RequestBody Imagen imagen, @PathVariable Long idProducto) {

        Optional<Producto> producto = productoService.buscarPorId(idProducto);
        productoService.agregarImagenAProducto(idProducto,imagen);

        return producto.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdProducto: " + idProducto + ", no pertenece a ningun producto")
                : ResponseEntity.ok(producto);
    }

    @PostMapping("/agregarCaracteristica")
    public ResponseEntity<?> agregarCaracteristica(
            @RequestParam Long productoId,
            @RequestParam List<Long> idCaracteristicas) {

        Producto producto = productoService.agregarCaracteristicas(productoId, idCaracteristicas);

        Optional<Producto> productos = productoService.buscarPorId(productoId);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdCiudad: " + productoId + ", no pertenece a ningun producto")
                : ResponseEntity.ok(productos);
    }

    @GetMapping("/caracteristicas-no-asignadas/{productoId}")
    public ResponseEntity<?> listarCaracteristicasNoAgregadas(@PathVariable Long productoId) {
        List<Caracteristicas> caracteristicas = productoService.listarCaracteristicasNoAsignadas(productoId);

        Optional<Producto> productos = productoService.buscarPorId(productoId);

        return productos.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("IdCiudad: " + productoId + ", no pertenece a ningun producto")
                : ResponseEntity.ok(caracteristicas);
    }

}


