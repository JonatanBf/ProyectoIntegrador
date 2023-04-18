package ImagenLIst.demo.service;

import ImagenLIst.demo.Dto.ProductoDto;
import ImagenLIst.demo.entidades.*;
import ImagenLIst.demo.repository.ProductoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;
    private final PoliticasService politicasService;
    private final CiudadService ciudadService;
    private final CaracteristicasService caracteristicasService;

    private final ImagenService imagenService;

    public Producto agregarDto(ProductoDto productoDto) {

        String nombre = productoDto.getNombre();
        String descripcion = productoDto.getDescripcion();
        String latitud = productoDto.getLatitud();
        String longitud = productoDto.getLongitud();
        String direccion = productoDto.getDireccion();

        List<Imagen> imagenes = productoDto.getImagenes();

        Long id_ciudad = productoDto.getId_Ciudad();
        Long id_categoria = productoDto.getId_Categoria();

        List<Long> caracteristicasDto = productoDto.getCaracteristicas();
        List<Politicas> politicasDto = productoDto.getPoliticas();

        Optional<Ciudad> ciudadId = ciudadService.buscarPorId(id_ciudad);
        Optional<Categoria> categoriaId = categoriaService.buscarPorId(id_categoria);

        Producto productNew = new Producto();

        if (nombre != null && !nombre.equals("")) productNew.setNombre(nombre);

        if (descripcion != null && !descripcion.equals("")) productNew.setDescripcion(descripcion);

        if (latitud != null && !latitud.equals("")) productNew.setLatitud(latitud);

        if (longitud != null && !longitud.equals("")) productNew.setLongitud(longitud);

        if (direccion != null && !direccion.equals("")) productNew.setDireccion(direccion);

        if (imagenes != null && imagenes.size()>0) productNew.setImagenes(imagenes);

        ciudadId.ifPresent(productNew::setCiudad);

        categoriaId.ifPresent(productNew::setCategoria);

        if (caracteristicasDto != null && caracteristicasDto.size()>0)productNew.setCaracteristicas(filtrarCaracteristicasPorId(caracteristicasDto));

        if (politicasDto != null && politicasDto.size()>0)productNew.setPoliticas(politicasDto);

        productoRepository.save(productNew);
        return productNew;
    }

    public void modificarProducto(Long id, String nombre, String descripcion, String latitud, String longitud, String direccion) {

        Producto producto = productoRepository.findById(id).get();

        if (nombre != null && !nombre.equals("")) producto.setNombre(nombre);

        if (descripcion != null && !descripcion.equals("")) producto.setDescripcion(descripcion);

        if (latitud != null && !latitud.equals("")) producto.setLatitud(latitud);

        if (longitud != null && !longitud.equals("")) producto.setLongitud(longitud);

        if (direccion != null && !direccion.equals("")) producto.setDireccion(direccion);
        productoRepository.save(producto);
    }

    public void modificarProductoCompleto(ProductoDto productoDto, Long id) {

        Producto productNew = productoRepository.findById(id).get();

        String nombre = productoDto.getNombre();
        String descripcion = productoDto.getDescripcion();
        String latitud = productoDto.getLatitud();
        String longitud = productoDto.getLongitud();
        String direccion = productoDto.getDireccion();

        List<Imagen> imagenes = productoDto.getImagenes();

        Long id_ciudad = productoDto.getId_Ciudad();
        Long id_categoria = productoDto.getId_Categoria();

        List<Long> caracteristicasDto = productoDto.getCaracteristicas();
        List<Politicas> politicasDto = productoDto.getPoliticas();

        Optional<Ciudad> ciudadId = ciudadService.buscarPorId(id_ciudad);
        Optional<Categoria> categoriaId = categoriaService.buscarPorId(id_categoria);

        if (nombre != null && !nombre.equals("")) productNew.setNombre(nombre);

        if (descripcion != null && !descripcion.equals("")) productNew.setDescripcion(descripcion);

        if (latitud != null && !latitud.equals("")) productNew.setLatitud(latitud);

        if (longitud != null && !longitud.equals("")) productNew.setLongitud(longitud);

        if (direccion != null && !direccion.equals("")) productNew.setDireccion(direccion);

        if (imagenes != null && imagenes.size()>0) productNew.setImagenes(imagenes);

        ciudadId.ifPresent(productNew::setCiudad);

        categoriaId.ifPresent(productNew::setCategoria);

        if (caracteristicasDto != null && caracteristicasDto.size()>0)productNew.setCaracteristicas(filtrarCaracteristicasPorId(caracteristicasDto));

        if (politicasDto != null && politicasDto.size()>0)productNew.setPoliticas(politicasDto);

        productoRepository.save(productNew);
    }

    public void modificarCaracteristicas(List<Long> idCaracteristicas, Long id) {

        var productoGuardado = productoRepository.findById(id);

        if (productoGuardado.isPresent()){
            var productoNuevo = productoGuardado.get();
            if (idCaracteristicas != null && idCaracteristicas.size()>0){
                productoNuevo.setCaracteristicas(filtrarCaracteristicasPorId(idCaracteristicas));
                 productoRepository.save(productoNuevo);
            }
        }
    }

    public void modificarCiudad(Long idCiudad, Long id) {

        var productoGuardado = productoRepository.findById(id);

        if (productoGuardado.isPresent()){
            var productoNuevo = productoGuardado.get();

           if (ciudadService.buscarPorId(idCiudad).isPresent())
               productoNuevo.setCiudad(ciudadService.buscarPorId(idCiudad).get());
           productoRepository.save(productoNuevo);
        }
    }

    public void modificarCategoria(Long idCategoria, Long id) {

        var productoGuardado = productoRepository.findById(id);

        if (productoGuardado.isPresent()){
            var productoNuevo = productoGuardado.get();

            if (categoriaService.buscarPorId(idCategoria).isPresent())
                productoNuevo.setCategoria(categoriaService.buscarPorId(idCategoria).get());
            productoRepository.save(productoNuevo);
        }
    }

    public List<Long> buscarIdsRepetidos(List<Long> listaIds) {
        Set<Long> idsUnicos = new HashSet<>();
        Set<Long> idsDuplicados = new HashSet<>();
        for (Long id : listaIds) {
            if (!idsUnicos.add(id)) {
                idsDuplicados.add(id);
            }
        }
        return new ArrayList<>(idsDuplicados);
    }

    public List<Politicas> filtrarPoliticasPorId(List<Long> idsPoliticas) {
        List<Politicas> resultado = new ArrayList<>();
            for (Long idP : idsPoliticas){
                if(politicasService.buscarPorId(idP).isPresent()){
                    resultado.add(politicasService.buscarPorId(idP).get());
                }
        }
        return resultado;
    }

    public List<Caracteristicas> filtrarCaracteristicasPorId(List<Long> idsCaracteristicas) {
        List<Caracteristicas> resultado = new ArrayList<>();
            for (Long idP : idsCaracteristicas){
                if(caracteristicasService.buscarPorId(idP).isPresent()){
                    resultado.add(caracteristicasService.buscarPorId(idP).get());
                }
        }
        return resultado;
    }

    @Transactional
    public void eliminarCaracteristicas(Long id, List<Long> idCaracteristicas) {
        // Buscar el producto por ID
        Producto producto = productoRepository.findById(id).get();

        // Verificar que el producto exista y tenga características asociadas
        if (producto.getCaracteristicas() != null && !producto.getCaracteristicas().isEmpty()) {
            // Crear una lista de características a eliminar
            List<Caracteristicas> caracteristicasAEliminar = new ArrayList<>();

            // Recorrer la lista de IDs de características a eliminar
            for (Long idCaracteristica : idCaracteristicas) {
                // Buscar la característica por ID
                for (Caracteristicas caracteristica : producto.getCaracteristicas()) {
                    // Si se encuentra la característica, agregarla a la lista de características a eliminar
                    if (caracteristica.getId().equals(idCaracteristica)) {
                        caracteristicasAEliminar.add(caracteristica);
                        break;
                    }
                }
            }
            producto.getCaracteristicas().removeAll(caracteristicasAEliminar);
        }
    }

    @Transactional
    public void eliminarImagenes(Long id, List<Long> idImagenes) {

        // Buscar el producto por ID
        Producto producto = productoRepository.findById(id).get();

        // Verificar que el producto exista y tenga imagenes asociadas
        if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {

            // Crear una lista de imagenes a eliminar
            List<Imagen> imagenesAEliminar = new ArrayList<>();

            // Crear una lista de IDs de imagenes eliminadas
            List<Long> idImagenesEliminadas = new ArrayList<>();

            // Recorrer la lista de IDs de Imagenes a eliminar
            for (Long idImagen : idImagenes) {
                // Buscar la Imagen por ID
                for (Imagen imagen : producto.getImagenes()) {
                    // Si se encuentra la imagen, agregarla a la lista de imagenes a eliminar
                    if (imagen.getId().equals(idImagen)) {
                        imagenesAEliminar.add(imagen);
                        idImagenesEliminadas.add(idImagen);
                        break;
                    }
                }
            }
            producto.getImagenes().removeAll(imagenesAEliminar);
            imagenService.eliminarPorIds(idImagenesEliminadas);
        }
    }

    public void eliminarPoliticasDeProducto(Long idProducto, List<Long> idPoliticas) {

        Optional<Producto> optionalProducto = productoRepository.findById(idProducto);

        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Politicas> politicas = producto.getPoliticas();
            politicas.removeIf(p -> idPoliticas.contains(p.getId()));
            producto.setPoliticas(politicas);
            productoRepository.save(producto);

            // Eliminar las políticas también desde el servicio de políticas
            for (Long idPolitica : idPoliticas) {
                politicasService.eliminar(idPolitica);
            }
        }
    }

    public void agregarImagenAProducto(Long idProducto, Imagen imagen) {
        Optional<Producto> optionalProducto = productoRepository.findById(idProducto);
        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Imagen> imagenes = producto.getImagenes();
            imagenes.add(imagen);
            producto.setImagenes(imagenes);
            productoRepository.save(producto);
        }
    }


    public List<Long> obtenerIdsInexistentesPoliticas(List<Long> idsPoliticas) {
        var politicasList = politicasService.listar();
        List<Long> idsInexistentes = new ArrayList<>();
        for (Long idP : idsPoliticas) {
            boolean encontrado = false;
            for (Politicas politicas : politicasList) {
                if (politicas.getId().equals(idP)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                idsInexistentes.add(idP);
            }
        }
        return idsInexistentes;
    }

    public List<Long> obtenerIdsInexistentesImagenes(List<Long> idsImagenes, Long id) {

        var producto = productoRepository.findById(id).get();
        var imagenList = producto.getImagenes();
        List<Long> idsInexistentes = new ArrayList<>();
        for (Long idP : idsImagenes) {
            boolean encontrado = false;
            for (Imagen imagen : imagenList) {
                if (imagen.getId().equals(idP)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                idsInexistentes.add(idP);
            }
        }
        return idsInexistentes;
    }

    public List<Long> obtenerIdsInexistentesCaracteristicas(List<Long> idsCaracteristicas) {
        var caracterisitcasList = caracteristicasService.listar();
        List<Long> idsInexistentes = new ArrayList<>();
        for (Long idP : idsCaracteristicas) {
            boolean encontrado = false;
            for (Caracteristicas caracteristicas : caracterisitcasList) {
                if (caracteristicas.getId().equals(idP)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                idsInexistentes.add(idP);
            }
        }
        return idsInexistentes;
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public List<Producto> getByCaracteristicas(String nombreCaracteristicas) {
        List<Producto> resultado = new ArrayList<>();
        var productos = productoRepository.findAll();
        for (Producto producto : productos) {
            var caracteristicasList = producto.getCaracteristicas();
            for (Caracteristicas caracteristicas1 : caracteristicasList) {
                if (caracteristicas1.getNombre().equals(nombreCaracteristicas))
                    resultado.add(producto);
            }
        }
        return resultado;
    }

    public List<Producto> buscarPorTituloCategoria(String tituloCategoria) {
        List<Producto> resultados = new ArrayList<>();
        var listaProductos = productoRepository.findAll();
        for (Producto producto : listaProductos) {
            Categoria categoria = producto.getCategoria();
            if (categoria != null && categoria.getTitulo().equals(tituloCategoria)) {
                resultados.add(producto);
            }
        }
        return resultados;
    }

    public List<Producto> buscarPorDescripcionCategoria(String descripcionCategoria) {
        List<Producto> resultados = new ArrayList<>();
        var listaProductos = productoRepository.findAll();
        for (Producto producto : listaProductos) {
            Categoria categoria = producto.getCategoria();
            if (categoria != null && categoria.getDescripcion().equals(descripcionCategoria)) {
                resultados.add(producto);
            }
        }
        return resultados;
    }

    public List<Producto> buscarPorTituloPoliticas(String tituloPoliticas) {
        List<Producto> resultados = new ArrayList<>();
        var listaProductos = productoRepository.findAll();
        for (Producto producto : listaProductos) {
            var politicaList = producto.getPoliticas();
            for (Politicas politicas : politicaList)
                if (politicas != null && politicas.getNombre().equals(tituloPoliticas)) {
                    resultados.add(producto);
                }
        }
        return resultados;
    }

    public List<Producto> buscarPorDescripcionPoliticas(String descripcionPolitica) {
        List<Producto> resultados = new ArrayList<>();
        var listaProductos = productoRepository.findAll();
        for (Producto producto : listaProductos) {
            var politicaList = producto.getPoliticas();
            for (Politicas politicas : politicaList)
                if (politicas != null && politicas.getDescripcion() != null && politicas.getDescripcion().contains(descripcionPolitica)) {
                    resultados.add(producto);
                }
        }
        return resultados;
    }


    public List<Producto> getByCiudad(String nombre) {
        return productoRepository.getByCiudad(nombre);
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarNombre(String nombre) {
        return productoRepository.getByNombre(nombre);
    }

    public List<Producto> buscarDescripcion(String descripcion) {
        return productoRepository.getByDescripcion(descripcion);
    }

    public void eliminar(Long  id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> findAllByCaracteristicas_IdIn(List<Long> idsCaracteristicas) {
        return productoRepository.findAllByCaracteristicas_IdIn(idsCaracteristicas);
    }

    public List<Producto> findAllByCategoriaIds(List<Long> idsCategorias) {
        return productoRepository.findAllByCategoriaIds(idsCategorias);
    }

    public List<Producto> findAllByCiudadIds(List<Long> idsCiudades) {
        return productoRepository.findAllByCiudadIds(idsCiudades);
    }

    public List<Producto> findRandomProductos() {
        return productoRepository.findRandomProductos();
    }

    public List<Producto> findByCiudad_Id (Long idCiudad){
        return productoRepository.findByCiudad_Id(idCiudad);
    }

    // Método para eliminar una descripción de la política
    public Producto eliminarDescripcionPolitica(Long productoId, Long politicaId, String descripcion) {
        Optional<Producto> optionalProducto = productoRepository.findById(productoId);

        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Politicas> politicas = producto.getPoliticas();

            for (Politicas politica : politicas) {
                if (politica.getId().equals(politicaId)) {
                    List<String> descripciones = politica.getDescripcion();

                    for (String d : descripciones) {
                        if (d.equals(descripcion)) {
                            descripciones.remove(d);
                            politica.setDescripcion(descripciones);
                            politicas.set(politicas.indexOf(politica), politica);
                            producto.setPoliticas(politicas);
                            return productoRepository.save(producto);
                        }
                    }
                    break;
                }
            }
        }
        return null;
    }

    // Método para actualizar una descripción de la política
    public Producto actualizarDescripcionPolitica(Long productoId, Long politicaId, String descripcion, String newDescripcion) {

        Optional<Producto> optionalProducto = productoRepository.findById(productoId);

        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Politicas> politicas = producto.getPoliticas();

            for (Politicas politica : politicas) {
                if (politica.getId().equals(politicaId)) {
                    List<String> descripciones = politica.getDescripcion();

                    for (int i = 0; i < descripciones.size(); i++) {
                        if (descripciones.get(i).equals(descripcion)) {
                            descripciones.set(i, newDescripcion);
                            politica.setDescripcion(descripciones);
                            politicas.set(politicas.indexOf(politica), politica);
                            producto.setPoliticas(politicas);
                            return productoRepository.save(producto);
                        }
                    }
                }
            }
        }

        return null;
    }

    public Producto agregarDescripcionPolitica(Long productoId, Long politicaId, String nuevaDescripcion) {
        Optional<Producto> optionalProducto = productoRepository.findById(productoId);

        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Politicas> politicas = producto.getPoliticas();

            for (Politicas politica : politicas) {
                if (politica.getId().equals(politicaId)) {
                    List<String> descripciones = politica.getDescripcion();
                    descripciones.add(nuevaDescripcion);
                    politica.setDescripcion(descripciones);
                    politicas.set(politicas.indexOf(politica), politica);
                    producto.setPoliticas(politicas);
                    return productoRepository.save(producto);
                }
            }
        }

        return null;
    }

    // Método para agregar una o varias características a un producto
    public Producto agregarCaracteristicas(Long productoId, List<Long> idCaracteristicas) {
        Optional<Producto> optionalProducto = productoRepository.findById(productoId);

        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Caracteristicas> caracteristicas = new ArrayList<>();

            for (Long idCaracteristica : idCaracteristicas) {
                Optional<Caracteristicas> optionalCaracteristica = caracteristicasService.buscarPorId(idCaracteristica);
                optionalCaracteristica.ifPresent(caracteristicas::add);
            }

            producto.getCaracteristicas().addAll(caracteristicas);
            return productoRepository.save(producto);
        }

        return null;
    }

    public List<Caracteristicas> listarCaracteristicasNoAsignadas(Long productoId) {
        Optional<Producto> optionalProducto = productoRepository.findById(productoId);

        if (optionalProducto.isPresent()) {
            Producto producto = optionalProducto.get();
            List<Caracteristicas> caracteristicas = caracteristicasService.listar();
            caracteristicas.removeAll(producto.getCaracteristicas());
            return caracteristicas;
        }
        return null;
    }

}
