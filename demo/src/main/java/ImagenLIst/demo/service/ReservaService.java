package ImagenLIst.demo.service;

import ImagenLIst.demo.Dto.ReservaDto;
import ImagenLIst.demo.entidades.*;
import ImagenLIst.demo.repository.ClienteRepository;
import ImagenLIst.demo.repository.ReservaRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.xmlbeans.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private JavaMailSender javaMailSender;

    public ByteArrayInputStream generarExcel() throws IOException {
        List<Reserva> reservas = reservaRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Sheet sheet = workbook.createSheet("Reservas"); //Nombre de pestania
        Row headerRow = sheet.createRow(1);

        String[] columnas = {"IdReserva","Ingreso", "Egreso", "Hotel", "Direccion", "Categoria", "Cliente_Nombre","Cliente_Apellido","Email"};
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
        }

        int rowNum = 2; // Empezamos en la fila 2
        for (Reserva reserva : reservas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(reserva.getId());
            row.createCell(1).setCellValue(reserva.getFechaInicial()+" - Hora: "+reserva.getHoraComienzo());
            row.createCell(2).setCellValue(reserva.getFechaFinal()+" - Hora: "+reserva.getHoraFinal());
            row.createCell(3).setCellValue(reserva.getProducto().getNombre());
            row.createCell(4).setCellValue(reserva.getProducto().getDireccion());
            row.createCell(5).setCellValue(reserva.getProducto().getCategoria().getTitulo());
            row.createCell(6).setCellValue(reserva.getCliente().getNombre());
            row.createCell(7).setCellValue(reserva.getCliente().getApellido());
            row.createCell(8).setCellValue(reserva.getCliente().getEmail());
        }

        // Agregamos las filas para la cantidad de usuarios y clientes
        Row cantidadUsuariosRow = sheet.createRow(0);
        cantidadUsuariosRow.createCell(0).setCellValue("Reservas: " + reservas.size());

        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public void enviarNotificacionDeReservaProxima(Reserva reserva) throws MessagingException {
        Cliente cliente = reserva.getCliente();
        String asunto = "Recordatorio de reserva";
        String mensaje =  "<html>" +
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
                "<p>Estimado/a " + reserva.getCliente().getNombre() + ", recuerde que tiene una reserva : "+",</p>" +
                "<table>" +
                "<tr>" +
                "<th>Hotel</th>" +
                "<th>Check-In</th>" +
                "<th>Check-Out</th>" +
                "<th>Hora Entrada</th>" +
                "<th>Hora Salida</th>" +
                "</tr>" +
                "<tr>" +
                "<td>" + reserva.getProducto().getNombre() + "</td>" +
                "<td>" + reserva.getFechaInicial().toString() + "</td>" +
                "<td>" + reserva.getFechaFinal().toString() + "</td>" +
                "<td>" + reserva.getHoraComienzo().toString() + "</td>" +
                "<td>" + reserva.getHoraFinal().toString()+ "</td>" +
                "</tr>" +
                "</table>" +
                "<p>Esperamos darle la bienvenida en nuestro establecimiento y le agradecemos su preferencia.</p>" +
                "<p>Atentamente,</p>" +
                "<p>Digital Booking</p>" +
                "</body>" +
                "</html>";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(cliente.getEmail());
        helper.setSubject(asunto);
        helper.setText(mensaje, true);

        javaMailSender.send(message);
    }

    public void enviarNotificacionesDeReservasProximas() throws MessagingException {
        LocalDate fechaActualMasUnDia = LocalDate.now().plusDays(1);
        List<Reserva> reservasProximas = reservaRepository.findByFechaInicial(fechaActualMasUnDia);

        for (Reserva reserva : reservasProximas) {
            enviarNotificacionDeReservaProxima(reserva);
        }
    }

    public void enviarNotificacionesDeProductoCancelado(Long idProducto, String motivo) throws MessagingException {
        List<Reserva> reservasPorProductoCancelado = buscarReservasPorProductoCancelado(idProducto);

        for (Reserva reserva : reservasPorProductoCancelado) {
            notificacionEliminacionDeProducto(reserva, motivo);
        }
    }

    public List<Reserva> buscarReservasPorProductoCancelado(Long idProducto) {
        // Obtener la fecha y hora actual
        LocalDate fechaActual = LocalDate.now();
        LocalTime horaActual = LocalTime.now();

        // Filtrar las reservas por el idProducto y la fecha actual
        List<Reserva> reservas = reservaRepository.buscarReservasPorProductoYFecha(idProducto, fechaActual);

        // Si la fecha es la de hoy, eliminar las reservas cuya horaComienzo sea menor que la hora actual
        if (fechaActual.equals(LocalDate.now())) {
            reservas.removeIf(reserva -> reserva.getHoraComienzo().isBefore(horaActual));
        }
        return reservas;
    }

    public void notificacionEliminacionDeProducto(Reserva reserva, String motivo) throws MessagingException {
        Cliente cliente = reserva.getCliente();
        String asunto = "Cancelacion de Reserva por: "+motivo;
        String mensaje =  "<html>" +
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
                "<p>Estimado " + cliente.getNombre() + " " + cliente.getApellido() + ",</p>" +
                "<p>Lamentamos informarle que su reserva para el <span style='color: #007bff'>" + reserva.getProducto().getNombre() + "</span> ha sido cancelada debido a <span style=\"color:red\">" + motivo + "</span>.</p>" +
                "<table>" +
                "<tr>" +
                "<th>Check-In</th>" +
                "<th>Check-Out</th>" +
                "<th>Hora Entrada</th>" +
                "<th>Hora Salida</th>" +
                "<th>Cancelacion</th>" +
                "</tr>" +
                "<tr>" +
                "<td>" + reserva.getFechaInicial().toString() + "</td>" +
                "<td>" + reserva.getFechaFinal().toString() + "</td>" +
                "<td>" + reserva.getHoraComienzo().toString() + "</td>" +
                "<td>" + reserva.getHoraFinal().toString()+ "</td>" +
                "<td>" +"Motivo: <span style=\"color:red\">" + motivo + "</span></td>" +
                "</tr>" +
                "</table>" +
                "<p>Por favor, póngase en contacto con nosotros para más información.</p>" +
                "<p>Atentamente,</p>" +
                "<p>Digital Booking</p>" +
                "</body>" +
                "</html>";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(cliente.getEmail());
        helper.setSubject(asunto);
        helper.setText(mensaje, true);

        javaMailSender.send(message);
    }

    public void agregar(ReservaDto reservaDto){

        var hora_Comienzo = reservaDto.getHora_Comienzo();
        var fecha_Inicial = reservaDto.getFecha_Inicial();
        var fecha_Final = reservaDto.getFecha_Final();
        var id_Producto= reservaDto.getId_Producto();
        var id_Cliente =reservaDto.getId_Cliente();
        LocalTime hora_Final = LocalTime.of(new Random().nextInt(15) + 8, 0); // Genera una hora aleatoria entre las 08:00 y las 22:00
        reservaDto.setHora_Final(hora_Final);
        var horaF = reservaDto.getHora_Final();
        Reserva reserva = new Reserva();
        reserva.setHoraFinal(horaF);
        reserva.setHoraComienzo(hora_Comienzo);
        reserva.setFechaInicial(fecha_Inicial);
        reserva.setFechaFinal(fecha_Final);

        var producto = productoService.buscarPorId(id_Producto).get();
        reserva.setProducto(producto);

        if (usuarioService.esCliente(id_Cliente)){
            var cliente = usuarioService.buscarClientePorId(id_Cliente);
            reserva.setCliente(cliente.get());
        } else {
            var usuario = usuarioService.buscarPorId(id_Cliente).get();
            var correoRepetido = clienteService.findByEmail(usuario.getEmail());
            if (correoRepetido.isEmpty()){
                Cliente cliente = new Cliente(usuario.getNombre(), usuario.getApellido(), usuario.getEmail(), usuario.getPassword(), usuario.getRol(), usuario.getCiudad());
                clienteRepository.save(cliente);
                cliente.getReservas().add(reserva); // agregar relación con la reserva
                reserva.setCliente(cliente); // establecer cliente en la reserva
            }else{
                reserva.setCliente(usuarioService.buscarClientePorId(correoRepetido.get().getId()).get());
            }
        }
        reservaRepository.save(reserva);
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public void modificar(ReservaDto reservaDto, Long id) {

        var reservaGuardada = reservaRepository.findById(id);

        var hComienzo = reservaDto.getHora_Comienzo();
        var fechaInicial = reservaDto.getFecha_Inicial();
        var fechaFinal = reservaDto.getFecha_Final();

        if (reservaGuardada.isPresent() ) {

            var reservaUpdate = reservaGuardada.get();

            if (hComienzo != null ) reservaUpdate.setHoraComienzo(hComienzo);
            if (fechaInicial != null) reservaUpdate.setFechaInicial(fechaInicial);
            if ( fechaFinal != null) reservaUpdate.setFechaFinal(fechaFinal);
            reservaRepository.save(reservaUpdate);
        }
    }

    public void modificarProducto(Long idProducto, Long id) {

        var reservaGuardado = reservaRepository.findById(id);

        if (reservaGuardado.isPresent()){

            var reservaNuevoProducto = reservaGuardado.get();

            if (productoService.buscarPorId(idProducto).isPresent())
                reservaNuevoProducto.setProducto(productoService.buscarPorId(idProducto).get());
            reservaRepository.save(reservaNuevoProducto);
        }
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> buscarClientePorId(Long id) {
        return reservaRepository.getByClienteId(id);
    }


    public List<Reserva> buscarProductoId(Long id) {
        return reservaRepository.getByProductoId(id);
    }


    public void eliminar(Long  id) {
        reservaRepository.deleteById(id);
    }

    public boolean existsByProductoRangoFechas(Producto producto, LocalDate fechaInicial, LocalDate fechaFinal){
        return reservaRepository.existsByProductoRangoFechas(producto,fechaInicial,fechaFinal);
    }

    public List<Producto> findProductosDisponiblesByCiudadAndFechas(String nombreCiudad, LocalDate fechaInicio, LocalDate fechaFin){
        return reservaRepository.findProductosDisponiblesByCiudadAndFechas(nombreCiudad,fechaInicio,fechaFin);
    }

    public List<Producto> findProductosDisponiblesByFechas(LocalDate fechaInicio, LocalDate fechaFin){
        return reservaRepository.findProductosDisponiblesByFechas(fechaInicio,fechaFin);
    }

    public Long getLastReservaId() {
        return reservaRepository.findLastId();
    }

    public byte[] agregarReservaPDF(Long idReserva, Long idCliente) throws DocumentException, IOException {

        Optional<Reserva>  reservaOptional = reservaRepository.findById(idReserva);
        Optional<Cliente>  clienteOptional = usuarioService.buscarClientePorId(idCliente);

        if (reservaOptional.isPresent() && clienteOptional.isPresent()) {
            // Crear el documento PDF
            Document document = new Document();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            // Generar un UUID aleatorio
            UUID uuid = UUID.randomUUID();

            // Tomar los primeros 8 caracteres de la representación en cadena del UUID
            String codigoComprobante = uuid.toString().substring(0, 8);

            Image headerImg = Image.getInstance("src/main/resources/Logos/logo5.png");
            headerImg.setAlignment(Element.ALIGN_LEFT);
            headerImg.scaleAbsoluteWidth(100);
            headerImg.scaleAbsoluteHeight(30);
            headerImg.setSpacingAfter(10);
            document.add(headerImg);

            // Detalle de Reserva
            Paragraph detalleR = new Paragraph("Detalle de Reserva ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK));
            detalleR.setAlignment(Element.ALIGN_CENTER);
            detalleR.setSpacingAfter(10);
            document.add(detalleR);

            // Código de comprobante
            Paragraph codigoC = new Paragraph("Código de comprobante: "+codigoComprobante, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK));
            codigoC.setAlignment(Element.ALIGN_RIGHT);
            codigoC.setSpacingAfter(10);
            document.add(codigoC);

            // Agregar datos del Huesped
            Paragraph encabezadoH = new Paragraph("Informacion del Huesped: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE));
            encabezadoH.setAlignment(Element.ALIGN_LEFT);
            encabezadoH.setSpacingAfter(10);
            document.add(encabezadoH);

            // Agregar datos del huésped y reserva
            Paragraph huespedNombre = new Paragraph("Nombre : " + clienteOptional.get().getNombre() , FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph huespedApellido = new Paragraph("Apellido : " + clienteOptional.get().getApellido(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            huespedNombre.setAlignment(Element.ALIGN_LEFT);
            huespedApellido.setAlignment(Element.ALIGN_LEFT);
            document.add(huespedNombre);
            document.add(huespedApellido);

            Paragraph fechaInicio = new Paragraph("Check-In : " + reservaOptional.get().getFechaInicial().toString() + " Hora: " + reservaOptional.get().getHoraComienzo().toString() , FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph fechaFinal = new Paragraph("Check-Out : " + reservaOptional.get().getFechaFinal().toString() + " Hora: " + reservaOptional.get().getHoraFinal().toString() , FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            fechaFinal.setSpacingAfter(10);

            fechaInicio.setAlignment(Element.ALIGN_LEFT);
            fechaFinal.setAlignment(Element.ALIGN_LEFT);
            document.add(fechaInicio);
            document.add(fechaFinal);

            // Agregar encabezado del hotel
            Paragraph encabezadoHotel = new Paragraph("Informacion del Hotel", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE));
            encabezadoHotel.setSpacingAfter(10);

            Paragraph hotel = new Paragraph("Hotel: "+reservaOptional.get().getProducto().getNombre(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph categoria = new Paragraph("Categoria: "+reservaOptional.get().getProducto().getCategoria().getTitulo(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph direccion = new Paragraph("Direccion: "+reservaOptional.get().getProducto().getDireccion(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph contacto = new Paragraph("Contacto: "+reservaOptional.get().getProducto().getNombre().replaceAll("\\s+", "")+"@gmail.com", FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph ciudad = new Paragraph("Ciudad: "+reservaOptional.get().getProducto().getCiudad().getNombre(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            ciudad.setSpacingAfter(20);

            encabezadoHotel.setAlignment(Element.ALIGN_LEFT);
            hotel.setAlignment(Element.ALIGN_LEFT);
            categoria.setAlignment(Element.ALIGN_LEFT);
            direccion.setAlignment(Element.ALIGN_LEFT);
            ciudad.setAlignment(Element.ALIGN_LEFT);

            document.add(encabezadoHotel);
            document.add(hotel);
            document.add(categoria);
            document.add(direccion);
            document.add(contacto);
            document.add(ciudad);

            // Agregar encabezado Politicas
            Paragraph politicas = new Paragraph("Politicas de Cancelacion: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE));
            politicas.setSpacingAfter(10);
            document.add(politicas);

            Politicas politica = null;
            for (Politicas p : reservaOptional.get().getProducto().getPoliticas()) {
                if (p.getNombre().equals("Politica de cancelacion")) {
                    politica = p;
                    break;
                }
            }

            if (politica != null) {
                int index = 1;
                for (String descripcion : politica.getDescripcion()) {
                    String text = index + ") " + descripcion;
                    Paragraph itemIteracion = new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.DARK_GRAY));
                    itemIteracion.setAlignment(Element.ALIGN_LEFT);
                    itemIteracion.setSpacingAfter(10);
                    document.add(itemIteracion);
                    index++;
                }
            } else {
                Paragraph itemNull = new Paragraph("El producto no posee políticas asignadas", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, BaseColor.DARK_GRAY));
                itemNull.setAlignment(Element.ALIGN_LEFT);
                itemNull.setSpacingAfter(20);
                document.add(itemNull);
            }

            Paragraph cReserva = new Paragraph("Para confirmar su reserva, se le solicitará que proporcione una tarjeta de crédito válida en el momento del check-in. Si tiene alguna pregunta o desea realizar cambios en su reserva, no dude en ponerse en contacto con nuestro servicio de atención al cliente en cualquier momento. ", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, BaseColor.DARK_GRAY));
            cReserva.setAlignment(Element.ALIGN_LEFT);
            cReserva.setSpacingAfter(15);
            document.add(cReserva);

            Paragraph fFinal = new Paragraph("Gracias por elegir nuestra aplicación de reservas de alojamiento en hoteles en DigitalBooking. Esperamos darle la bienvenida en "+ reservaOptional.get().getProducto().getNombre()+" pronto.",FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLUE));
            fFinal.setAlignment(Element.ALIGN_LEFT);
            fFinal.setSpacingAfter(80);
            document.add(fFinal);

            // Agregar Hora de Impresion
            Paragraph hora = new Paragraph("Impresion: "+formattedDateTime, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK));
            hora.setAlignment(Element.ALIGN_RIGHT);
            document.add(hora);

            document.close();
            return (byteArrayOutputStream.toByteArray());
    }
        return null;
    }

    public byte[] eliminarReservaPDF(Long idReserva, Long idCliente) throws DocumentException, IOException {

        Optional<Reserva> reservaOptional = reservaRepository.findById(idReserva);
        Optional<Cliente> clienteOptional = usuarioService.buscarClientePorId(idCliente);
        if (reservaOptional.isPresent() && clienteOptional.isPresent()) {
            // Crear el documento PDF
            Document document = new Document();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            Image headerImg = Image.getInstance("src/main/resources/Logos/logo5.png");
            headerImg.setAlignment(Element.ALIGN_LEFT);
            headerImg.scaleAbsoluteWidth(100);
            headerImg.scaleAbsoluteHeight(30);
            headerImg.setSpacingAfter(30);
            document.add(headerImg);

            // Generar un UUID aleatorio
            UUID uuid = UUID.randomUUID();

            // Tomar los primeros 8 caracteres de la representación en cadena del UUID
            String codigoComprobante = uuid.toString().substring(0, 8);

            // Detalle de Cancelacion
            Paragraph detalleR = new Paragraph("Detalle de Cancelacion", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.RED));
            detalleR.setAlignment(Element.ALIGN_CENTER);
            detalleR.setSpacingAfter(15);
            document.add(detalleR);

            // Código de comprobante
            Paragraph codigoC = new Paragraph("Código de comprobante: "+codigoComprobante, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            codigoC.setAlignment(Element.ALIGN_RIGHT);
            codigoC.setSpacingAfter(20);
            document.add(codigoC);

            // Agregar datos del Huesped
            Paragraph encabezadoH = new Paragraph("Informacion del Huesped: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE));
            encabezadoH.setAlignment(Element.ALIGN_LEFT);
            encabezadoH.setSpacingAfter(10);
            document.add(encabezadoH);

            // Agregar datos del huésped y reserva
            Paragraph huespedNombre = new Paragraph("Nombre : " + clienteOptional.get().getNombre() , FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph huespedApellido = new Paragraph("Apellido : " + clienteOptional.get().getApellido(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            huespedNombre.setAlignment(Element.ALIGN_LEFT);
            huespedApellido.setAlignment(Element.ALIGN_LEFT);
            document.add(huespedNombre);
            document.add(huespedApellido);

            Paragraph fechaInicio = new Paragraph("Check-In : " + reservaOptional.get().getFechaInicial().toString() + " Hora: " + reservaOptional.get().getHoraComienzo().toString() , FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph fechaFinal = new Paragraph("Check-Out : " + reservaOptional.get().getFechaFinal().toString() + " Hora: " + reservaOptional.get().getHoraFinal().toString() , FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            fechaFinal.setSpacingAfter(10);

            fechaInicio.setAlignment(Element.ALIGN_LEFT);
            fechaFinal.setAlignment(Element.ALIGN_LEFT);
            document.add(fechaInicio);
            document.add(fechaFinal);

            // Agregar encabezado del hotel
            Paragraph encabezadoHotel = new Paragraph("Informacion del Hotel", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE));
            encabezadoHotel.setSpacingAfter(10);

            Paragraph hotel = new Paragraph("Hotel: "+reservaOptional.get().getProducto().getNombre(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph categoria = new Paragraph("Categoria: "+reservaOptional.get().getProducto().getCategoria().getTitulo(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph direccion = new Paragraph("Direccion: "+reservaOptional.get().getProducto().getDireccion(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph contacto = new Paragraph("Contacto: "+reservaOptional.get().getProducto().getNombre().replaceAll("\\s+", "")+"@gmail.com", FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            Paragraph ciudad = new Paragraph("Ciudad: "+reservaOptional.get().getProducto().getCiudad().getNombre(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
            ciudad.setSpacingAfter(20);

            encabezadoHotel.setAlignment(Element.ALIGN_LEFT);
            hotel.setAlignment(Element.ALIGN_LEFT);
            categoria.setAlignment(Element.ALIGN_LEFT);
            direccion.setAlignment(Element.ALIGN_LEFT);
            ciudad.setAlignment(Element.ALIGN_LEFT);

            document.add(encabezadoHotel);
            document.add(hotel);
            document.add(categoria);
            document.add(direccion);
            document.add(contacto);
            document.add(ciudad);

            // Agregar encabezado Cancelacion
            Paragraph horaCancelacion = new Paragraph("Impresion: "+formattedDateTime, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK));
            horaCancelacion.setAlignment(Element.ALIGN_RIGHT);
            horaCancelacion.setSpacingAfter(10);
            document.add(horaCancelacion);

            document.close();
            return (byteArrayOutputStream.toByteArray());
        }
        return null;
    }

    public String generarConfirmacionReservaHtml(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva).get();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        return  "<html>" +
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
                "<h1 style='color: #007bff;'>Confirmación de reserva</h1>" +
                "<p>Estimado/a " + reserva.getCliente().getNombre() + ",</p>" +
                "<p>Por favor, revise los detalles de su reserva en la siguiente tabla:</p>" +
                "<table>" +
                "<tr>" +
                "<th>Producto</th>" +
                "<th>Check-In</th>" +
                "<th>Check-Out</th>" +
                "<th>Hora Entrada</th>" +
                "<th>Hora Salida</th>" +
                "<th>Creada</th>" +
                "</tr>" +
                "<tr>" +
                "<td>" + reserva.getProducto().getNombre() + "</td>" +
                "<td>" + reserva.getFechaInicial().toString() + "</td>" +
                "<td>" + reserva.getFechaFinal().toString() + "</td>" +
                "<td>" + reserva.getHoraComienzo().toString() + "</td>" +
                "<td>" + reserva.getHoraFinal().toString()+ "</td>" +
                "<td>" + formattedDateTime+ "</td>" +
                "</tr>" +
                "</table>" +
                "<p>Esperamos darle la bienvenida en nuestro establecimiento y le agradecemos su preferencia.</p>" +
                "<p>Atentamente,</p>" +
                "<p>Digital Booking</p>" +
                "</body>" +
                "</html>";
    }

    public String generarCancelacionReservaHtml(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva).get();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        return  "<html>" +
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
                "<h1 style='color: #007bff;'>Confirmación de Cancelacion</h1>" +
                "<p>Estimado/a " + reserva.getCliente().getNombre() + ",</p>" +
                "<p>Por favor, revise los detalles de su Cancelacion en la siguiente tabla:</p>" +
                "<table>" +
                "<tr>" +
                "<th>Hotel</th>" +
                "<th>Check-In</th>" +
                "<th>Check-Out</th>" +
                "<th>Hora Entrada</th>" +
                "<th>Hora Salida</th>" +
                "<th>Cancelada</th>" +
                "</tr>" +
                "<tr>" +
                "<td>" + reserva.getProducto().getNombre() + "</td>" +
                "<td>" + reserva.getFechaInicial().toString() + "</td>" +
                "<td>" + reserva.getFechaFinal().toString() + "</td>" +
                "<td>" + reserva.getHoraComienzo().toString() + "</td>" +
                "<td>" + reserva.getHoraFinal().toString()+ "</td>" +
                "<td>" + formattedDateTime+ "</td>" +
                "</tr>" +
                "</table>" +
                "<p>Esperamos volver a verlo pronto en nuestro establecimiento y le agradecemos su preferencia.</p>" +
                "<p>Atentamente,</p>" +
                "<p>Digital Booking</p>" +
                "</body>" +
                "</html>";
    }



}
