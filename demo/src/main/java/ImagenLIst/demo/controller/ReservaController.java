package ImagenLIst.demo.controller;

import ImagenLIst.demo.Dto.ReservaDto;
import ImagenLIst.demo.entidades.*;
import ImagenLIst.demo.service.ClienteService;
import ImagenLIst.demo.service.ProductoService;
import ImagenLIst.demo.service.ReservaService;
import ImagenLIst.demo.service.UsuarioService;
import com.itextpdf.text.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.stream.LongStream;


@AllArgsConstructor
@RestController
@CrossOrigin(origins = { "http://127.0.0.1:5173", "http://localhost:5173", "http://equipo7-tomi.s3-website.us-east-2.amazonaws.com", "http://localhost:8080"})
@RestControllerAdvice
@Validated
@RequestMapping("/reservas")
public class ReservaController {

    private ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    private final ClienteService clienteService;

    private JavaMailSender javaMailSender;

    @GetMapping("/Excel-Reservas")
    public ResponseEntity<InputStreamResource> getReservasExcel() throws IOException {

        ByteArrayInputStream stream = reservaService.generarExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Reservas.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(stream));
    }

    @PostMapping("/notificar-ProximasReservas")
    public ResponseEntity<String> notificarReservasProximas() throws MessagingException {
        reservaService.enviarNotificacionesDeReservasProximas();
        return ResponseEntity.ok("Notificaciones enviadas");
    }

    @PostMapping("/enviarNotificacionesDeProductoCancelado")
    public ResponseEntity<?> notificarEliminacionHotel(@RequestParam Long idProducto, @RequestParam String motivo) throws MessagingException {
        reservaService.enviarNotificacionesDeProductoCancelado(idProducto, motivo);
        return ResponseEntity.ok("Notificaciones enviadas");
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody ReservaDto reservaDto) throws MessagingException, DocumentException, IOException {

        Long idProducto = reservaDto.getId_Producto();
        Long idCliente = reservaDto.getId_Cliente();

        Optional<Producto> productoOptional = productoService.buscarPorId(idProducto);
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(idCliente);

        if (productoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("idProducto : " + idProducto + " no pertenece a ningun producto");
        }
        if (reservaService.existsByProductoRangoFechas(productoOptional.get(), reservaDto.getFecha_Inicial(), reservaDto.getFecha_Final())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ya existe una reserva para el mismo producto y un rango de fechas que se superpone");
        }
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("idUsuario : " + idCliente + " no pertenece a ningun Usuario");
        }
        reservaService.agregar(reservaDto);
        // Obtener los datos del PDF generado
        var idReserva= reservaService.getLastReservaId();//Capturar ultima reserva
        var cliente = reservaService.buscarPorId(idReserva).get().getCliente();
        var pdf = reservaService.agregarReservaPDF(idReserva,cliente.getId());

        // Crear el mensaje de correo electrónico

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(usuarioOptional.get().getEmail());
        helper.setSubject("¡Confirmacion de Reserva!");
        String htmlMsg = reservaService.generarConfirmacionReservaHtml(idReserva);

        // Adjuntar el archivo PDF al correo electrónico
        helper.addAttachment("Reserva_"+cliente.getApellido()+"_"+cliente.getNombre()+".pdf", new ByteArrayResource(pdf));

        helper.setText(htmlMsg, true);

        javaMailSender.send(message);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Reserva se guardó con éxito");
    }

    @GetMapping("/agregarReservaPDF")
    public ResponseEntity<?> comprobanteAgregarPDF(@RequestParam Long idReserva, @RequestParam Long idCliente) throws DocumentException, IOException {

        byte[] pdfBytes = reservaService.agregarReservaPDF(idReserva, idCliente);

        Optional<Cliente>  usuarioOptional = usuarioService.buscarClientePorId(idCliente);

        if (pdfBytes.length > 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Reserva_"+usuarioOptional.get().getApellido()+"_"+usuarioOptional.get().getNombre()+".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la reserva o el cliente solicitado");
        }
    }

    @GetMapping("/eliminarReservaPDF")
    public ResponseEntity<?> ComprobanteEliminarPDF(@RequestParam Long idReserva, @RequestParam Long idCliente) throws DocumentException, IOException {

        byte[] pdfBytes = reservaService.eliminarReservaPDF(idReserva, idCliente);

        Optional<Cliente>  clienteOptional = usuarioService.buscarClientePorId(idCliente);

        if (pdfBytes.length > 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Reserva_"+clienteOptional.get().getApellido()+"_"+clienteOptional.get().getNombre()+".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la reserva o el cliente solicitado");
        }
    }

    @PostMapping("/agregarReservaRandom")
    public ResponseEntity<?> agregarReservaRandom() {
        LocalDate fechaHoy = LocalDate.now();
        LocalDate fechaInicialMin = fechaHoy.plusDays(1); // la fecha inicial debe ser mayor o igual a la fecha de hoy
        LocalDate fechaInicialMax = fechaHoy.plusDays(30); // la fecha inicial debe ser dentro de los próximos 30 días
        List<Long> idProductos = LongStream.rangeClosed(1, productoService.listar().size()).boxed().toList();
        List<Long> idClientes = LongStream.rangeClosed(1, usuarioService.listar().size()).boxed().toList();
        Random random = new Random();
        int reservasCreadas = 0; // variable de conteo
        for (int i = 0; i < 100 && reservasCreadas < 20; i++) { // agregar límite máximo de iteraciones y condición de salida
            Long idProducto = idProductos.get(random.nextInt(idProductos.size())); // seleccionar un id_Producto aleatorio
            Long idCliente = idClientes.get(random.nextInt(idClientes.size())); // seleccionar un id_Cliente aleatorio
            LocalDate fechaInicial = fechaInicialMin.plusDays(random.nextInt(fechaInicialMax.getDayOfYear() - fechaInicialMin.getDayOfYear() + 1)); // seleccionar una fecha inicial aleatoria dentro del rango permitido
            LocalDate fechaFinal = fechaInicial.plusDays(random.nextInt(3) + 1); // seleccionar una fecha final aleatoria dentro de los próximos 3 días a partir de la fecha inicial
            LocalTime horaComienzo = LocalTime.of(new Random().nextInt(15) + 8, 0); // Genera una hora aleatoria
            ReservaDto reservaDto = new ReservaDto();
            reservaDto.setHora_Comienzo(horaComienzo);
            reservaDto.setId_Producto(idProducto);
            reservaDto.setId_Cliente(idCliente);
            reservaDto.setFecha_Inicial(fechaInicial);
            reservaDto.setFecha_Final(fechaFinal);
            if (!reservaService.existsByProductoRangoFechas(productoService.buscarPorId(idProducto).get(), reservaDto.getFecha_Inicial(), reservaDto.getFecha_Final())){
                reservaService.agregar(reservaDto);
                reservasCreadas++; // incrementar variable de conteo
                System.out.println("Reserva "+reservasCreadas+" creada con éxito\n");
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Se crearon "+reservasCreadas+" reservas random con éxito");
    }

    @GetMapping("/posiblesReservas")
    public ResponseEntity<List<Map<String, Object>>> obtenerPosiblesReservas(@RequestParam Long idProducto, @RequestParam Long idUsuario) {
        LocalDate fechaHoy = LocalDate.now();
        LocalDate fechaInicialMin = fechaHoy.plusDays(1); // la fecha inicial debe ser mayor o igual a la fecha de hoy
        LocalDate fechaInicialMax = fechaHoy.plusDays(30); // la fecha inicial debe ser dentro de los próximos 30 días
        List<Map<String, Object>> posiblesReservas = new ArrayList<>();
        Random random = new Random();
        int reservasCreadas = 0; // variable de conteo
        boolean reservaExiste = true;
        for (int i = 0; i < 50 && reservasCreadas < 4; i++) { // agregar límite máximo de iteraciones y condición de salida
            LocalDate fechaInicial = fechaInicialMin.plusDays(random.nextInt(fechaInicialMax.getDayOfYear() - fechaInicialMin.getDayOfYear() + 1)); // seleccionar una fecha inicial aleatoria dentro del rango permitido
            LocalDate fechaFinal = fechaInicial.plusDays(random.nextInt(3) + 1); // seleccionar una fecha final aleatoria dentro de los próximos 3 días a partir de la fecha inicial
            LocalTime horaComienzo = LocalTime.of(random.nextInt(15) + 8, 0); // Genera una hora aleatoria
            Map<String, Object> reserva = new HashMap<>();
            reserva.put("hora_Comienzo", horaComienzo.toString());
            reserva.put("fecha_Inicial", fechaInicial.toString());
            reserva.put("fecha_Final", fechaFinal.toString());
            reserva.put("id_Producto", idProducto);
            reserva.put("id_Cliente", idUsuario);
            if (!reservaService.existsByProductoRangoFechas(productoService.buscarPorId(idProducto).get(), fechaInicial, fechaFinal)) {
                posiblesReservas.add(reserva);
                reservasCreadas++; // incrementar variable de conteo
                System.out.println("Reserva " + reservasCreadas + " creada con éxito\n");
            }
        }
        return ResponseEntity.ok(posiblesReservas);
    }

    @GetMapping("/listar")
    public List<Reserva> listar(){
        return reservaService.listar();
    }

    @PatchMapping("/modificarProducto/{id}")
    public ResponseEntity<?> modificarProducto(@RequestBody ReservaDto reservaDto, @PathVariable Long id) {

        var reservaOptional = reservaService.buscarPorId(id);

        if (reservaOptional.isPresent()) {

            var reservaUpdate = reservaOptional.get();

            var productoNuevo = reservaDto.getId_Producto();

            var producto = productoService.buscarPorId(productoNuevo);

            if (producto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe Producto con Id : " + productoNuevo);
            }
            if (reservaService.existsByProductoRangoFechas(producto.get(), reservaUpdate.getFechaInicial(), reservaUpdate.getFechaFinal())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe una reserva para el mismo producto y un rango de fechas que se superpone");
            }
            reservaService.modificarProducto(productoNuevo,id);
            String mensajeDeExito = "Producto se modifico con exito para Reserva_Id: "+id;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Reserva para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@RequestBody ReservaDto reservaDto, @PathVariable Long id) {

        Optional<Reserva> reservaOptional = reservaService.buscarPorId(id);

        if (reservaOptional.isPresent()) {
            if (reservaService.existsByProductoRangoFechas(reservaOptional.get().getProducto(), reservaDto.getFecha_Inicial(), reservaDto.getFecha_Final())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Ya existe una reserva para el mismo producto y un rango de fechas que se superpone");
            }
            reservaService.modificar(reservaDto,id);
            String mensajeDeExito = "Reserva se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Reservapara el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) throws MessagingException, DocumentException, IOException {

        Optional<Reserva> reservaOptional = reservaService.buscarPorId(id);

        if (reservaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ninguna Reserva");
        }

        // Obtener los datos del PDF generado
        byte[] pdf = reservaService.eliminarReservaPDF(id,reservaOptional.get().getCliente().getId());

        // Crear el mensaje de correo electrónico

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(reservaOptional.get().getCliente().getEmail());
        helper.setSubject("¡Cancelacion de Reserva Exitosa!");
        String htmlMsg = reservaService.generarCancelacionReservaHtml(id);

        // Adjuntar el archivo PDF al correo electrónico
        helper.addAttachment("Reserva_"+reservaOptional.get().getCliente().getApellido()+"_"+reservaOptional.get().getCliente().getNombre()+".pdf", new ByteArrayResource(pdf));

        helper.setText(htmlMsg, true);
        javaMailSender.send(message);
        reservaService.eliminar(id);
        return ResponseEntity.ok().body("Reserva con Id: " + id + " se eliminó con éxito");
    }

    @DeleteMapping("/eliminarByCliente_Id/{id}")
    public ResponseEntity<String> eliminarByUsuarioId(@PathVariable Long id){

        List<Reserva> reservaList = reservaService.buscarClientePorId(id);

        if (reservaList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente_Id : " + id+ ", aun no ha realizado ninguna reserva");
        }
        for (Reserva reserva : reservaList){
            reservaService.eliminar(reserva.getId());
            clienteService.eliminar(id);
        }
        return ResponseEntity.ok().body("Reservas con Cliente_Id: " + id + " se eliminó con éxito");
    }

    @DeleteMapping("/eliminarByProducto_Id/{id}")
    public ResponseEntity<String> eliminarByProductoId(@PathVariable Long id){

        List<Reserva> productoList = reservaService.buscarProductoId(id);

        if (productoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto_Id : " + id+ ", aun no posee ninguna reserva");
        }
        for (Reserva reserva : productoList){
            reservaService.eliminar(reserva.getId());
        }
        return ResponseEntity.ok().body("Reserva con Producto_Id: " + id + " se eliminó con éxito");
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Reserva> reservaOptional = reservaService.buscarPorId(id);

        return reservaOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ninguna Reserva")
                : ResponseEntity.ok(reservaOptional);
    }

    @GetMapping("/buscarClienteId/{id}")
    public ResponseEntity<?> buscarClienteId(@PathVariable Long id){

        List<Reserva> reservaOptional = reservaService.buscarClientePorId(id);
        var array = new ArrayList<>();

        return reservaOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(array)
                : ResponseEntity.ok(reservaOptional);
    }

    @GetMapping("/buscarCorreoCliente/{email}")
    public ResponseEntity<?> buscarCorreoCliente(@PathVariable String email){

        var array = new ArrayList<>();
        Optional<Cliente> cliente = clienteService.findByEmail(email);
        if (cliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(array);
        }
        List<Reserva> reservaOptional = reservaService.buscarClientePorId(cliente.get().getId());

        return reservaOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(array)
                : ResponseEntity.ok(reservaOptional);
    }

    @GetMapping("/buscarProductoId/{id}")
    public ResponseEntity<?> buscarProductoId(@PathVariable Long id){

        List<Reserva> reservaOptional = reservaService.buscarProductoId(id);

        return reservaOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("idProducto : " + id+ ", aun no posee Reservas")
                : ResponseEntity.ok(reservaOptional);
    }

    @GetMapping("/productos-disponibles")
    public ResponseEntity<?> findProductosDisponiblesByCiudadAndFechas(
            @RequestParam String nombreCiudad,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<Producto> reservaOptional = productoService.getByCiudad(nombreCiudad);
        List<Producto> filtroporCiudadFechas = reservaService.findProductosDisponiblesByCiudadAndFechas(nombreCiudad,fechaInicio,fechaFin);

        return reservaOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("nombreCiudad : " + nombreCiudad+ ", no pertenece a ningun producto")
                : ResponseEntity.ok(filtroporCiudadFechas);
    }

    @GetMapping("/productos-fechas")
    public ResponseEntity<?> findProductosDisponiblesByFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<Producto> filtroporFechas = reservaService.findProductosDisponiblesByFechas(fechaInicio,fechaFin);

        return filtroporFechas.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existen productos disponibles para esas fechas")
                : ResponseEntity.ok(filtroporFechas);
    }

}
