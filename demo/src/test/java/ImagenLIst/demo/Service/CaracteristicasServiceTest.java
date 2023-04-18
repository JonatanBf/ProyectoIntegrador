package ImagenLIst.demo.Service;

import ImagenLIst.demo.entidades.Caracteristicas;
import ImagenLIst.demo.repository.CaracteristicasRepository;
import ImagenLIst.demo.service.CaracteristicasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CaracteristicasServiceTest {

    @Mock
    private CaracteristicasRepository caracteristicasRepository;

    @InjectMocks
    private CaracteristicasService caracteristicasService;


    private Caracteristicas caracteristicas = new Caracteristicas();

    @BeforeEach
    void setUp(){
        caracteristicas.setId(1L);
        caracteristicas.setNombre("Wifi");
        caracteristicas.setIcono("Wifi.icono");
    }


    @Test
    void testAgregarCaracteristicas(){
        //Given
        given(caracteristicasRepository.save(caracteristicas)).willReturn(caracteristicas);
        //When
        var caracteristicasGuarda = caracteristicasService.agregar(caracteristicas);
        //Then
        assertThat(caracteristicasGuarda).isNotNull();
        assertThat(caracteristicasGuarda.getId()).isGreaterThan(0);
    }
    @Test
    void testListarCaracteristicas(){
        //Given
        List<Caracteristicas> caracteristicasList = new ArrayList<>();
        caracteristicasList.add(caracteristicas);
        caracteristicasList.add(caracteristicas);
        caracteristicasList.add(caracteristicas);
        given(caracteristicasRepository.findAll()).willReturn(caracteristicasList);
        //When
        var lista = caracteristicasService.listar();
        //Then
        assertThat(lista).isNotNull();
        assertThat(lista.size()).isEqualTo(3);
    }
    @Test
    void testModificarCaracteristicas(){
        //Given
        Long caracteristicaId = 1L;
        given(caracteristicasRepository.findById(caracteristicaId)).willReturn(Optional.of(caracteristicas));
        given(caracteristicasRepository.findByNombre(caracteristicas.getNombre())).willReturn(Optional.empty());
        given(caracteristicasRepository.save(caracteristicas)).willReturn(caracteristicas);
        //When
        caracteristicasService.modificar(caracteristicas,caracteristicaId);
        //Then
        verify(caracteristicasRepository,times(1)).save(caracteristicas);
    }
    @Test
    void testELiminarCaracteristicas(){
        //Given
        Long caracteristicaId = 1L;
        willDoNothing().given(caracteristicasRepository).deleteById(caracteristicaId);
        //When
        caracteristicasService.eliminar(caracteristicaId);
        //Then
        verify(caracteristicasRepository,times(1)).deleteById(caracteristicaId);

    }
    @Test
    void testBuscarCaracteristicasPorId(){
        //Given
        Long  caracterisiticaID = 1L;
        given(caracteristicasRepository.findById(caracterisiticaID)).willReturn(Optional.of(caracteristicas));
        //When
        var caracteristicaEncontrada = caracteristicasService.buscarPorId(caracterisiticaID);
        //Then
        assertThat(caracteristicaEncontrada).isNotNull();
        assertThat(caracteristicaEncontrada.get().getId()).isEqualTo(1);
    }
    @Test
    void testBuscarCaracteristicasPorNombre(){
        //Given
        String nombre = "Wifi";
        given(caracteristicasRepository.findByNombre(nombre)).willReturn(Optional.of(caracteristicas));
        //When
        var caracteristicaEncontrada = caracteristicasService.buscarPorNombre(nombre);
        //Then
        assertThat(caracteristicaEncontrada).isNotNull();
        assertThat(caracteristicaEncontrada.get().getNombre()).isEqualTo(nombre);
    }

}
