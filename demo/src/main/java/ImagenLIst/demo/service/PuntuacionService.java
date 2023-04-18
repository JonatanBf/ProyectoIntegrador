package ImagenLIst.demo.service;

import ImagenLIst.demo.Dto.PuntuacionDto;
import ImagenLIst.demo.entidades.Producto;
import ImagenLIst.demo.entidades.Puntuacion;
import ImagenLIst.demo.entidades.Usuario;
import ImagenLIst.demo.repository.PuntuacionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PuntuacionService {

    private final PuntuacionRepository puntuacionRepository;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public Puntuacion agregarDTO (PuntuacionDto puntuacionDto){

        Long usuarioLong = puntuacionDto.getId_Usuario();
        Long productoLong = puntuacionDto.getId_Producto();
        Long puntuacion = puntuacionDto.getPuntuacion();

        Usuario usuario = usuarioService.buscarPorId(usuarioLong).get();
        Producto producto = productoService.buscarPorId(productoLong).get();

        Puntuacion nuevaPuntuacion = new Puntuacion();
        nuevaPuntuacion.setUsuario(usuario);
        nuevaPuntuacion.setProducto(producto);
        nuevaPuntuacion.setPuntuacion(puntuacion);
        return puntuacionRepository.save(nuevaPuntuacion);
    }

    public List<Puntuacion> listar() {
        return puntuacionRepository.findAll();
    }

    public void modificarProducto(Long idProducto, Long id) {

        var puntuacionGuardado = puntuacionRepository.findById(id);

        if (puntuacionGuardado.isPresent()){

            var puntuacionNuevoProducto = puntuacionGuardado.get();

            if (productoService.buscarPorId(idProducto).isPresent())
                puntuacionNuevoProducto.setProducto(productoService.buscarPorId(idProducto).get());
            puntuacionRepository.save(puntuacionNuevoProducto);
        }
    }

    public void modificarUsuario(Long idUsuario, Long id) {

        var puntuacionGuardado = puntuacionRepository.findById(id);

        if (puntuacionGuardado.isPresent()){

            var puntuacionNuevoUsuario = puntuacionGuardado.get();

            if (usuarioService.buscarPorId(idUsuario).isPresent())
                puntuacionNuevoUsuario.setUsuario(usuarioService.buscarPorId(idUsuario).get());
            puntuacionRepository.save(puntuacionNuevoUsuario);
        }
    }

    public void modificarPuntuacion(Long puntaje, Long id) {

        var puntuacionGuardado = puntuacionRepository.findById(id);

        if (puntuacionGuardado.isPresent()){

            var puntuacionNuevoPuntaje = puntuacionGuardado.get();

            if (puntaje != null && puntaje>0 && puntaje<6)
                puntuacionNuevoPuntaje.setPuntuacion(puntaje);
            puntuacionRepository.save(puntuacionNuevoPuntaje);
        }
    }


    public String obtenerPromedioAlls() {
        var resultados = puntuacionRepository.obtenerPromedioAlls();

        String resultadoFinal = "";
        for (Object[] resultado : resultados) {
            Long cantidadUsuarios = (Long) resultado[0];
            Long idProducto = (Long) resultado[1];
            Double promedioPuntuacion = (Double) resultado[2];

            resultadoFinal += "\n"+"Cantidad de Usuarios: " + cantidadUsuarios + "; "
                    + "Producto: " + idProducto + ", "
                    + "Puntuacion: " + promedioPuntuacion + "\n";
        }
        return resultadoFinal;
    }

    public Optional<Puntuacion> buscarPorId(Long id) {
        return puntuacionRepository.findById(id);
    }

    public List<Puntuacion> buscarUsuarioId(Long id) {
        return puntuacionRepository.getByUsuarioId(id);
    }

    public List<Puntuacion> buscarProductoId(Long id) {
        return puntuacionRepository.getByProductoId(id);
    }

    public List<Puntuacion> buscarPuntuacion(Long puntuacion) {
        return puntuacionRepository.getByPuntuacion(puntuacion);
    }

    public void eliminar(Long  id) {
        puntuacionRepository.deleteById(id);
    }

    public Double obtenerPromedioPorProducto(Long idProducto){
        return puntuacionRepository.obtenerPromedioPorProducto(idProducto);
    }

    public boolean existsByUsuarioAndProducto(Long idUsuario, Long idProducto){
        return puntuacionRepository.existsByUsuarioAndProducto(idUsuario,idProducto);
    }
}
