package ImagenLIst.demo.service;

import ImagenLIst.demo.Dto.UsuarioDto;
import ImagenLIst.demo.entidades.Cliente;
import ImagenLIst.demo.entidades.Usuario;
import ImagenLIst.demo.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final CiudadService ciudadService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ByteArrayInputStream generarExcel() throws IOException {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Usuario> clientes = usuarioRepository.listarClientes();

        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Sheet sheet = workbook.createSheet("Usuarios y Clientes"); //Nombre de pestania
        Row headerRow = sheet.createRow(2);

        String[] columnas = {"Id","Nombre", "Apellido", "Email", "Rol_Nombre", "Ciudad_Nombre", "Tipo"};
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
        }

        int rowNum = 3; // Empezamos en la fila 2
        for (Usuario usuario : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(usuario.getId());
            row.createCell(1).setCellValue(usuario.getNombre());
            row.createCell(2).setCellValue(usuario.getApellido());
            row.createCell(3).setCellValue(usuario.getEmail());
            row.createCell(4).setCellValue(usuario.getRol().getNombre());
            row.createCell(5).setCellValue(usuario.getCiudad().getNombre());
            row.createCell(6).setCellValue("Usuario");
        }

        rowNum = 3 + usuarios.size(); // Empezamos en la fila siguiente a los usuarios
        for (Usuario cliente : clientes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cliente.getId());
            row.createCell(1).setCellValue(cliente.getNombre());
            row.createCell(2).setCellValue(cliente.getApellido());
            row.createCell(3).setCellValue(cliente.getEmail());
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue(cliente.getCiudad().getNombre());
            row.createCell(6).setCellValue("Cliente");
        }

        // Agregamos las filas para la cantidad de usuarios y clientes
        Row cantidadUsuariosRow = sheet.createRow(0);
        cantidadUsuariosRow.createCell(0).setCellValue("Usuario: " + usuarios.size());
        Row cantidadClientesRow = sheet.createRow(1);
        cantidadClientesRow.createCell(0).setCellValue("Cliente: " + clientes.size());

        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public Long getLastUsuarioId() {
        return usuarioRepository.findLastId();
    }

    public String generarConfirmacionUsuarioHtml(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).get();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        return   "<html>" +
                "<head>" +
                "<style>" +
                "table {" +
                "  border-collapse: collapse;" +
                "  width: 100%;" +
                "}" +
                "th, td {" +
                "  text-align: left;" +
                "  padding: 8px;" +
                "}" +
                "th {" +
                "  background-color: #dddddd;" +
                "  color: #333333;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1 style='color: #007bff;'>Confirmación de registro</h1>" +
                "<p>Estimado/a " + usuario.getNombre() + ",</p>" +
                "<p>Por favor, revise los detalles de su Registro en la siguiente tabla:</p>" +
                "<table>" +
                "<tr>" +
                "<th>Nombre</th>" +
                "<th>Apellido</th>" +
                "<th>Ciudad</th>" +
                "<th>Creada</th>" +
                "</tr>" +
                "<tr>" +
                "<td>" + usuario.getNombre() + "</td>" +
                "<td>" + usuario.getApellido() + "</td>" +
                "<td>" + usuario.getCiudad().getNombre() + "</td>" +
                "<td>" + formattedDateTime+ "</td>" +
                "</tr>" +
                "</table>" +
                "<p>Esperamos darle la bienvenida en nuestro establecimiento y le agradecemos su preferencia.</p>" +
                "<p>Atentamente,</p>" +
                "<p>Digital Booking</p>" +
                "</body>" +
                "</html>";
    }


    public Usuario agregar(UsuarioDto usuarioDTO) {

        Usuario usuario = new Usuario();

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());

        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isEmpty()) usuario.setEmail(usuarioDTO.getEmail());

        String encodedPassword = this.passwordEncoder.encode(usuarioDTO.getPassword());
        usuario.setPassword(encodedPassword);

        usuario.setRol(rolService.buscarPorId(usuarioDTO.getRolId()).get());
        usuario.setCiudad(ciudadService.buscarPorId(usuarioDTO.getId_Ciudad()).get());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.listar();
    }

    public List<Usuario> listarClientes() {
        return usuarioRepository.listarClientes();
    }

    public void modificar(UsuarioDto usuarioDto, Long id) {

        var usuarioGuardada = usuarioRepository.findById(id);

        var nombre= usuarioDto.getNombre();
        var apellido = usuarioDto.getApellido();
        var email = usuarioDto.getEmail();
        var contrasenia = usuarioDto.getPassword();
        var emailRepetido = usuarioRepository.findByEmail(usuarioDto.getEmail());

        if (usuarioGuardada.isPresent() ) {
            var usuario = usuarioGuardada.get();
            if(nombre != null && !nombre.equals("")) usuario.setNombre(usuarioDto.getNombre());
            if(apellido != null && !apellido.equals("")) usuario.setApellido(usuarioDto.getApellido());
            if(email != null && emailRepetido.isEmpty()) usuario.setEmail(email);
            if(contrasenia != null && !contrasenia.equals("")) {
                String encodedPassword = this.passwordEncoder.encode(contrasenia);
                usuario.setPassword(encodedPassword);
            }
            usuarioRepository.save(usuario);
        }
    }

    public void modificarRol(Long idRol, Long id) {

        var usuarioGuardado = usuarioRepository.findById(id);

        if (usuarioGuardado.isPresent()){
            var usuarioNuevoRol = usuarioGuardado.get();

            if (rolService.buscarPorId(idRol).isPresent())
                usuarioNuevoRol.setRol(rolService.buscarPorId(idRol).get());
            usuarioRepository.save(usuarioNuevoRol);
        }
    }

    public void modificarCiudad(Long idCiudad, Long id) {

        var usuarioGuardado = usuarioRepository.findById(id);

        if (usuarioGuardado.isPresent()){
            var usuarioNuevoCiudad = usuarioGuardado.get();

            if (ciudadService.buscarPorId(idCiudad).isPresent())
                usuarioNuevoCiudad.setCiudad(ciudadService.buscarPorId(idCiudad).get());
            usuarioRepository.save(usuarioNuevoCiudad);
        }
    }

    public void eliminar(Long  id) {
        usuarioRepository.deleteById(id);
    }

    public void eliminarClienteId(Long  id) {
        usuarioRepository.deleteByIdCliente(id);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<List<Usuario>> buscarNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    public Optional<Usuario> buscarEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<List<Usuario>> buscarApellido(String apellido) {
        return usuarioRepository.findByApellido(apellido);
    }

    public List<Usuario> findByRol_Id(Long idRol){
        return usuarioRepository.findByRol_Id(idRol);
    }

    public List<Usuario> findByRol_Nombre(String rolNombre){
        return usuarioRepository.findByRol_Nombre(rolNombre);
    }

    public List<Usuario> findByCiudadId(Long idCiudad){
        return usuarioRepository.findByCiudad_Id(idCiudad);
    }

    public List<Usuario> findByCiudadNombre(String rolNombre){
        return usuarioRepository.findByCiudad_Nombre(rolNombre);
    }

    public boolean esCliente(Long idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        return usuario.isPresent() && usuario.get() instanceof Cliente;
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent() && usuario.get() instanceof Cliente) {
            return Optional.of((Cliente) usuario.get());
        } else {
            return Optional.empty();
        }
    }



}

