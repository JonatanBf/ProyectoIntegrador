package ImagenLIst.demo.Service;

import ImagenLIst.demo.entidades.Ciudad;
import ImagenLIst.demo.repository.CiudadRepository;
import ImagenLIst.demo.service.CiudadService;
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
public class CiudadServiceTest {

    @Mock
    private CiudadRepository ciudadRepository;

    @InjectMocks
    private CiudadService ciudadService;

    private Ciudad ciudad = new Ciudad();

    private Long ciudadId = 1L;

    private String nombre = "Bogota";

    @BeforeEach
    void setUp(){
        ciudad.setId(1L);
        ciudad.setNombre("Bogota");
        ciudad.setPais("Colombia");
    }

    @Test
    void testAgregarCiudad(){
        //Given
        given(ciudadRepository.save(ciudad)).willReturn(ciudad);
        //When
        ciudadService.agregar(ciudad);
        //Then
        verify(ciudadRepository,times(1)).save(ciudad);
    }

    @Test
    void testListarCiudad(){
        //Given
        List<Ciudad> ciudadList = new ArrayList<>();
        ciudadList.add(ciudad);
        ciudadList.add(ciudad);
        ciudadList.add(ciudad);
        given(ciudadRepository.findAll()).willReturn(ciudadList);
        //When
        var ciudadesList = ciudadService.listar();
        //Then
        assertThat(ciudadesList).isNotNull();
        assertThat(ciudadesList.size()).isEqualTo(3);
    }
    @Test
    void testModificarCiudad(){
        //Given
        given(ciudadRepository.findById(ciudadId)).willReturn(Optional.of(ciudad));
        given(ciudadRepository.findByCiudad(ciudad.getNombre())).willReturn(Optional.empty());
        given(ciudadRepository.save(ciudad)).willReturn(ciudad);
        ciudad.setPais("Argentina");
        //When
        ciudadService.modificar(ciudad,ciudadId);
        //Then
        verify(ciudadRepository,times(1)).save(ciudad);
    }
    @Test
    void testEliminarCiudad(){
        //Given
        willDoNothing().given(ciudadRepository).deleteById(ciudadId);
        //When
        ciudadService.eliminar(ciudadId);
        //Then
        verify(ciudadRepository,times(1)).deleteById(ciudadId);
    }
    @Test
    void testBuscarCiudadPorId(){
        //Given
        given(ciudadRepository.findById(ciudadId)).willReturn(Optional.of(ciudad));
        //When
        var ciudadEncontrada = ciudadService.buscarPorId(ciudadId);
        //Then
        assertThat(ciudadEncontrada).isNotNull();
        assertThat(ciudadEncontrada.get().getId()).isEqualTo(1);
        assertThat(ciudadEncontrada.get().getNombre()).isEqualTo("Bogota");
    }
    @Test
    void testBuscarCiudadPorNombre(){
        //Given
        given(ciudadRepository.findByCiudad(nombre)).willReturn(Optional.of(ciudad));
        //When
        var ciudadEncontrada = ciudadService.buscarPorCiudad(nombre);
        //Then
        assertThat(ciudadEncontrada).isNotNull();
        assertThat(ciudadEncontrada.get().getNombre()).isEqualTo(nombre);
    }





}
