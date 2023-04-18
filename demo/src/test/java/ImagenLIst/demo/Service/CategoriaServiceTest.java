package ImagenLIst.demo.Service;

import ImagenLIst.demo.entidades.Categoria;
import ImagenLIst.demo.repository.CategoriaRepository;
import ImagenLIst.demo.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository  categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria = new Categoria();

    @BeforeEach
    void setUP(){
        categoria.setId(1L);
        categoria.setTitulo("Hotel");
        categoria.setUrl("Hotel.url");
        categoria.setDescripcion("Es un hotel");
    }


    @Test
    void testAgregarCategoria(){
        //Given
        given(categoriaRepository.save(categoria)).willReturn(categoria);
        //When
        var categoriaGuardada = categoriaService.agregar(categoria);
        //Then
        assertThat(categoriaGuardada).isNotNull();
        assertThat(categoriaGuardada.getId()).isGreaterThan(0);
    }
    @Test
    void testListarCategoria(){
        //Given
        List<Categoria> categoriaList = new ArrayList<>();
        categoriaList.add(categoria);
        categoriaList.add(categoria);
        categoriaList.add(categoria);
        given(categoriaRepository.findAll()).willReturn(categoriaList);
        //When
        var listaCategoria = categoriaService.listar();
        //Then
        assertThat(listaCategoria).isNotNull();
        assertThat(listaCategoria.size()).isEqualTo(3);
    }
    @Test
    void testBuscarCategoriaPorTitulo(){
        //Given
        String nombreCategoria = "Hotel";
        given(categoriaRepository.findByTitulo(nombreCategoria)).willReturn(Optional.of(categoria));
        //When
        var categoria = categoriaService.buscarTitulo(nombreCategoria);
        //Then
        assertThat(categoria).isNotNull();
        assertThat(categoria.get().getTitulo()).isEqualTo(nombreCategoria);
    }
    @Test
    void testBuscarCategoriaPorDescripcion(){
        //Given
        String descripcion = "Es un hotel";
        given(categoriaRepository.findByDescripcion(descripcion)).willReturn(Optional.of(categoria));
        //When
        var categoria = categoriaService.buscarDescripcion(descripcion);
        //Then
        assertThat(categoria).isNotNull();
        assertThat(categoria.get().getDescripcion()).isEqualTo(descripcion);
    }
    @Test
    void testModificarCategoria(){
        //Given
        Long categoriaId = 1L;
        given(categoriaRepository.findById(categoriaId)).willReturn(Optional.of(categoria));
        given(categoriaRepository.save(categoria)).willReturn(categoria);
        //When
        categoriaService.modificar(categoria, categoriaId);
        //Then
        verify(categoriaRepository,times(1)).save(categoria);
    }
    @Test
    void testEliminarCategoria(){
        //Given
        Long categoriaId = 1L;
        willDoNothing().given(categoriaRepository).deleteById(categoriaId);
        //When
        categoriaService.eliminar(categoriaId);
        //Then
        verify(categoriaRepository,times(1)).deleteById(categoriaId);


    }
    @Test
    void testBuscarCategoriaPorId(){
        //Given
        Long categoriaId = 1L;
        given(categoriaRepository.findById(categoriaId)).willReturn(Optional.of(categoria));
        //When
        var categoria = categoriaService.buscarPorId(categoriaId);
        //Then
        assertThat(categoria).isNotNull();
        assertThat(categoria.get().getTitulo()).isEqualTo("Hotel");
        assertThat(categoria.get().getUrl()).isEqualTo("Hotel.url");
        assertThat(categoria.get().getDescripcion()).isEqualTo("Es un hotel");
    }


}
