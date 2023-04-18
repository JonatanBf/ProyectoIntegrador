package ImagenLIst.demo.controller;

import ImagenLIst.demo.Dto.UsuarioDto;
import ImagenLIst.demo.entidades.*;
import ImagenLIst.demo.service.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.LongStream;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = { "http://127.0.0.1:5173", "http://localhost:5173", "http://equipo7-tomi.s3-website.us-east-2.amazonaws.com", "http://localhost:8080"})
@RestControllerAdvice
@Validated
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PuntuacionService puntuacionService;
    private RolService rolService;
    private CiudadService ciudadService;

    private JavaMailSender javaMailSender;
    private final ClienteService clienteService;

    private final ReservaService reservaService;

    @GetMapping("/Excel-Usuario-Clientes")
    public ResponseEntity<InputStreamResource> getUsuariosYClientesExcel() throws IOException {

        ByteArrayInputStream stream = usuarioService.generarExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Usuarios&Clientes.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(stream));
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@Valid @RequestBody UsuarioDto usuarioDto) throws MessagingException {

        String email = usuarioDto.getEmail();
        Optional<Usuario> usuarioMismoCorreo = usuarioService.buscarEmail(email);

        if (usuarioMismoCorreo.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email : " + email + " ya existe en nuestros registros.");
        }
        Optional<Rol> rol = rolService.buscarPorId(usuarioDto.getRolId());

        if (rol.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe Rol con Id : " + usuarioDto.getRolId());
        }
        Optional<Ciudad> ciudad = ciudadService.buscarPorId(usuarioDto.getId_Ciudad());

        if (ciudad.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe Ciudad con Id : " + usuarioDto.getId_Ciudad());
        }
        Usuario nuevoUsuario = usuarioService.agregar(usuarioDto);
        // Crear el mensaje de correo electrónico
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(nuevoUsuario.getEmail());
        helper.setSubject("¡Creacion de registro Exitosa!");
        // Obtener los datos del PDF generado
        var idUsuario= usuarioService.getLastUsuarioId();//Capturar ultima reserva
        String htmlMsg =usuarioService.generarConfirmacionUsuarioHtml(idUsuario);

        helper.setText(htmlMsg, true);
        javaMailSender.send(message);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario se guardo correctamente.");
    }

    @PostMapping("/agregarUsuarioRandom")
    public ResponseEntity<List<Map<String, String>>> agregarUsuarioRandom() {
        Random rand = new Random();
        List<String> nombres = Arrays.asList("Jonatan","Franco","Tomas","Silvestre","Santiago","Juan", "Pedro", "Maria", "Ana", "Luis", "Carlos", "Laura", "Julia", "Diego", "Valentina", "Mariana", "Andres", "Isabella", "Camila");
        List<String> apellidos = Arrays.asList("Bordon","Alberdi","Ortigoza","Novaro","Dopazo","Garcia", "Perez", "Rodriguez", "Gonzalez", "Martinez", "Sanchez", "Diaz", "Lopez", "Torres", "Castro", "Ramirez", "Vargas", "Hernandez", "Fernandez", "Sosa");
        List<Long> idRol = LongStream.rangeClosed(1, rolService.listar().size()).boxed().toList();
        List<Long> idCiudad = LongStream.rangeClosed(1, ciudadService.listar().size()).boxed().toList();
        List<String> correos = List.of("digitalBooking.com");

        List<Map<String, String>> usuariosCreados = new ArrayList<>();
        while (usuariosCreados.size() < 15) {
            UsuarioDto usuarioDto = new UsuarioDto();
            usuarioDto.setNombre(nombres.get(rand.nextInt(nombres.size())));
            usuarioDto.setApellido(apellidos.get(rand.nextInt(apellidos.size())));
            usuarioDto.setEmail(usuarioDto.getNombre().toLowerCase() + "." + usuarioDto.getApellido().toLowerCase() + "@" + correos.get(rand.nextInt(correos.size())));
            String password = usuarioDto.getNombre()+usuarioDto.getApellido();
            usuarioDto.setPassword(password);
            Long rolId = idRol.get(rand.nextInt(idRol.size()));
            Long ciudadId = idCiudad.get(rand.nextInt(idCiudad.size()));
            usuarioDto.setRolId(rolId);
            usuarioDto.setId_Ciudad(ciudadId);

            Optional<Usuario> usuarioMismoCorreo = usuarioService.buscarEmail(usuarioDto.getEmail());
            if (usuarioMismoCorreo.isEmpty()) {

                usuarioService.agregar(usuarioDto);

                // agregar usuario creado a la lista
                Map<String, String> usuarioCreado = new HashMap<>();
                usuarioCreado.put("email", usuarioDto.getEmail());
                usuarioCreado.put("password", password);
                usuariosCreados.add(usuarioCreado);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuariosCreados);
    }

    @GetMapping("/listar")
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/listarClientes")
    public List<Usuario> listarClientes() {
        return usuarioService.listarClientes();
    }

    @PatchMapping("/modificarRol/{id}")
    public ResponseEntity<?> modificarRol(@RequestBody UsuarioDto usuarioDto, @PathVariable Long id) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        if (usuarioOptional.isPresent()) {

            Long rolNuevo = usuarioDto.getRolId();

            Optional<Rol> rol = rolService.buscarPorId(rolNuevo);

            if (rol.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe Rol con Id : " + rolNuevo);
            }
            usuarioService.modificarRol(rolNuevo,id);
            String mensajeDeExito = "Rol se modifico con exito para el Usuario_Id: "+id;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Usuario para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificarCiudad/{id}")
    public ResponseEntity<?> modificarCiudad(@RequestBody UsuarioDto usuarioDto, @PathVariable Long id) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        if (usuarioOptional.isPresent()) {

            Long ciudadNuevo = usuarioDto.getId_Ciudad();

            Optional<Ciudad> ciudad = ciudadService.buscarPorId(ciudadNuevo);

            if (ciudad.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe Ciudad con Id : " + ciudadNuevo);
            }
            usuarioService.modificarCiudad(ciudadNuevo,id);
            String mensajeDeExito = "Ciudad se modifico con exito para el Usuario_Id: "+id;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Usuario para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @PatchMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@RequestBody UsuarioDto usuarioDto, @PathVariable Long id) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        if (usuarioOptional.isPresent()) {

            String email = usuarioDto.getEmail();

            Optional<Usuario> usuarioMismoCorreo = usuarioService.buscarEmail(email);

            if (usuarioMismoCorreo.isPresent()) {
                String mensajeDeError = "Usuario con Email : " + email + " ya existe en nuestros registros";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(mensajeDeError);
            }
            usuarioService.modificar(usuarioDto, id);
            String mensajeDeExito = "Usuario se modifico con exito";
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mensajeDeExito);
        }
        String mensajeDeError = "No existe Usuario para el Id: " + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mensajeDeError);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id : " + id+ ", no tiene asignado ningun Usuario");
        }
        List<Puntuacion> puntuaciones = puntuacionService.buscarUsuarioId(id);
            for (Puntuacion puntuacion: puntuaciones){
                puntuacionService.eliminar(puntuacion.getId());
            }
        Optional<Cliente> cliente = clienteService.findByEmail(usuarioOptional.get().getEmail());
        cliente.ifPresent(value -> clienteService.eliminar(value.getId()));
        usuarioService.eliminar(id);
        return ResponseEntity.ok().body("Usuario con Id: " + id + " se eliminó con éxito");
    }

    @DeleteMapping("/eliminarClienteId/{id}")
    public ResponseEntity<?> eliminarClienteId(@PathVariable Long id) {

        List<Reserva> reservas = reservaService.buscarClientePorId(id);
        for (Reserva reserva: reservas){
            reservaService.eliminar(reserva.getId());
        }
        usuarioService.eliminarClienteId(id);
        return ResponseEntity.ok().body("Cliente con Id: " + id + " se eliminó con éxito");
    }


    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);

        return usuarioOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ningun Usuario")
                : ResponseEntity.ok(usuarioOptional);
    }

    @GetMapping("/buscarClientePorId/{id}")
    public ResponseEntity<?> buscarClientePorId(@PathVariable Long id){

        Optional<Cliente> clienteOptional = usuarioService.buscarClientePorId(id);

        return clienteOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id: " + id + ", no pertenece a ningun Cliente")
                : ResponseEntity.ok(clienteOptional);
    }

    @GetMapping("/buscarEmail/{email}")
    public ResponseEntity<?> buscarEmail(@PathVariable String email){

        Optional<Usuario> usuarioOptional = usuarioService.buscarEmail(email);

        return usuarioOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con Email : " + email+ ", no pertenece a ningun Usuario")
                : ResponseEntity.ok(usuarioOptional);
    }

    @GetMapping("/buscarNombre/{nombre}")
    public ResponseEntity<?> buscarNombre(@PathVariable String nombre){

        Optional<List<Usuario>> usuarioOptional = usuarioService.buscarNombre(nombre);

        return usuarioOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con Nombre : " + nombre+ ", no pertenece a ningun Usuario")
                : ResponseEntity.ok(usuarioOptional);
    }

    @GetMapping("/buscarApellido/{apellido}")
    public ResponseEntity<?> buscarApellido(@PathVariable String apellido){

        Optional<List<Usuario>> usuarioOptional = usuarioService.buscarApellido(apellido);

        return usuarioOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con Apellido : " + apellido+ ", no pertenece a ningun Usuario")
                : ResponseEntity.ok(usuarioOptional);
    }

    @GetMapping("/buscarRol_Id/{id}")
    public ResponseEntity<?> buscarRolId(@PathVariable Long id){

        List<Usuario> usuarioOptional = usuarioService.findByRol_Id(id);

        return usuarioOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con Rol_Id : " + id+ ", no pertenece a ningun Usuario")
                : ResponseEntity.ok(usuarioOptional);
    }

    @GetMapping("/buscarRol_Nombre/{nombreRol}")
    public ResponseEntity<?> buscarRolNombre(@PathVariable String nombreRol){

        List<Usuario> usuarioOptional = usuarioService.findByRol_Nombre(nombreRol);

        return usuarioOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con Rol_Nombre : " + nombreRol+ ", no pertenece a ningun Usuario")
                : ResponseEntity.ok(usuarioOptional);
    }

    @GetMapping("/buscarCiudad_Id/{id}")
    public ResponseEntity<?> buscarCiudadById(@PathVariable Long id){

        List<Usuario> usuarioOptional = usuarioService.findByCiudadId(id);

        return usuarioOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ciudad_Id : " + id+ ", no pertenece a ningun Usuario")
                : ResponseEntity.ok(usuarioOptional);
    }

    @GetMapping("/buscarCiudad_Nombre/{nombreCiudad}")
    public ResponseEntity<?> buscarCiudadByNombre(@PathVariable String nombreCiudad){

        List<Usuario> usuarioOptional = usuarioService.findByCiudadNombre(nombreCiudad);

        return usuarioOptional.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ciudad_Nombre : " + nombreCiudad+ ", no pertenece a ningun Usuario")
                : ResponseEntity.ok(usuarioOptional);
    }


}
