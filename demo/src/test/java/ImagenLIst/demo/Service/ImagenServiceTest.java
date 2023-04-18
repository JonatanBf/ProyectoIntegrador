package ImagenLIst.demo.Service;

import ImagenLIst.demo.entidades.Imagen;
import ImagenLIst.demo.repository.ImagenRepository;
import ImagenLIst.demo.service.ImagenService;
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
public class ImagenServiceTest {

    @Mock
    private ImagenRepository imagenRepository;

    @InjectMocks
    private ImagenService imagenService;

    private Imagen imagen = new Imagen();

    private Long imagenId = 1L;

    private String titulo = "Imagen 1";

    @BeforeEach
    void setUp(){
        imagen.setId(1L);
        imagen.setTitulo("Imagen 1");
        imagen.setUrl("Imagen 1.url");
    }


    @Test
    void testListarImagen(){
        //Given
        List<Imagen> imagenList = new ArrayList<>();
        imagenList.add(imagen);
        imagenList.add(imagen);
        imagenList.add(imagen);
        given(imagenRepository.findAll()).willReturn(imagenList);
        //When
        var lista = imagenService.listar();
        //Then
        assertThat(lista).isNotNull();
        assertThat(lista.size()).isEqualTo(3);
    }
    @Test
    void testModificarImagen(){
        //Given
        given(imagenRepository.findById(imagenId)).willReturn(Optional.of(imagen));
        given(imagenRepository.save(imagen)).willReturn(imagen);
        //When
        imagenService.modificar(imagen, imagenId);
        //Then
        verify(imagenRepository, times(1)).save(imagen);
    }
    @Test
    void testEliminarImagen(){
        //Given
        willDoNothing().given(imagenRepository).deleteById(imagenId);
        //When
        imagenService.eliminar(imagenId);
        //Then
        verify(imagenRepository, times(1)).deleteById(imagenId);
    }
    @Test
    void testBuscarImagenPorId(){
        //Given
        given(imagenRepository.findById(imagenId)).willReturn(Optional.of(imagen));
        //When
        var imagenEncontrada = imagenService.buscarPorId(imagenId);
        //Then
        assertThat(imagenEncontrada).isNotNull();
        assertThat(imagenEncontrada.get().getId()).isEqualTo(1);
    }
    @Test
    void testBuscarImagenPorTitulo(){
        //Given
        given(imagenRepository.findByTitulo(titulo)).willReturn(List.of(imagen));
        //When
        var imagenList = imagenService.buscarTitulo(titulo);
        //Then
        assertThat(imagenList).isNotNull();
        assertThat(imagenList.size()).isEqualTo(1);
    }


}

