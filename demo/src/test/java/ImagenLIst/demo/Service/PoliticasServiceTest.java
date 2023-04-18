package ImagenLIst.demo.Service;

import ImagenLIst.demo.entidades.Politicas;
import ImagenLIst.demo.repository.PoliticasRepository;
import ImagenLIst.demo.service.PoliticasService;
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
public class PoliticasServiceTest {

    @Mock
    private PoliticasRepository politicasRepository;

    @InjectMocks
    private PoliticasService politicasService;

    private Politicas politicas = new Politicas();

    private String nombre = "Normas";
    private Long politicaId = 1L;

    @BeforeEach
    void setup(){
        politicas.setId(1L);
        politicas.setNombre("Normas");
        politicas.setDescripcion(List.of("Ducharse", "Comer"));
    }

    @Test
    void testAgregarPolitica(){
        //Given
        given(politicasRepository.save(politicas)).willReturn(politicas);
        //When
        var politicaGuardada  = politicasService.agregar(politicas);
        //Then
        assertThat(politicaGuardada).isNotNull();
        assertThat(politicaGuardada.getId()).isEqualTo(1L);
    }
    @Test
    void testListarPolitica(){
        //Given
        List<Politicas> politicasList = new ArrayList<>();
        politicasList.add(politicas);
        politicasList.add(politicas);
        politicasList.add(politicas);
        given(politicasRepository.findAll()).willReturn(politicasList);
        //When
        var lista = politicasService.listar();
        //Then
        assertThat(lista).isNotNull();
        assertThat(lista.size()).isEqualTo(3);
    }
    @Test
    void testBuscarPoliticaPorNombre(){
        //Given
        given(politicasRepository.findByNombre(nombre)).willReturn(Optional.of(politicas));
        //When
        var politicaNombre = politicasService.buscarNombre(nombre);
        //Then
        assertThat(politicaNombre).isNotNull();
        assertThat(politicaNombre.get().getNombre()).isEqualTo(nombre);
    }

    @Test
    void testModificarPolitica(){
        //Given
        given(politicasRepository.findById(politicaId)).willReturn(Optional.of(politicas));
        given(politicasRepository.save(politicas)).willReturn(politicas);
        //When
        politicasService.modificar(politicas,politicaId);
        //Then
        verify(politicasRepository,times(1)).save(politicas);
    }
    @Test
    void testEliminarPolitica(){
        //Given
        willDoNothing().given(politicasRepository).deleteById(politicaId);
        //When
        politicasService.eliminar(politicaId);
        //Then
        verify(politicasRepository,times(1)).deleteById(politicaId);

    }
    @Test
    void testBuscarPoliticaPorId(){
        //Given
        given(politicasRepository.findById(politicaId)).willReturn(Optional.of(politicas));
        //When
        var politica = politicasService.buscarPorId(politicaId);
        //Then
        assertThat(politica).isNotNull();
        assertThat(politica.get().getId()).isEqualTo(politicaId);
    }
}
