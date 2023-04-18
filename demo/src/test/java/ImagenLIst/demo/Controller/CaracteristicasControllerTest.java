package ImagenLIst.demo.Controller;

import ImagenLIst.demo.entidades.Caracteristicas;
import ImagenLIst.demo.service.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class CaracteristicasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private CaracteristicasService caracteristicasService;

    @MockBean
    private CiudadService ciudadService;

    @MockBean
    private ImagenService imagenService;

    @MockBean
    private PoliticasService politicasService;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private PuntuacionService puntuacionService;

    @MockBean
    private UsuarioService usuarioService;

    private Caracteristicas caracteristicas = new Caracteristicas();
    private Long caracteristicaId = 1L;
    private String nombre = "Wifi";

    @BeforeEach
    void setUp(){
        caracteristicas.setId(1L);
        caracteristicas.setNombre("Wifi");
        caracteristicas.setIcono("Wifi.icono");
    }


    @Test
    void testAgregarCaracteristica() throws Exception {
        //Given
        given(caracteristicasService.buscarPorNombre(nombre)).willReturn(Optional.empty());
        given(caracteristicasService.agregar(caracteristicas)).willReturn(caracteristicas);
        //When
        ResultActions response = mockMvc.perform(post("/caracteristicas/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(caracteristicas)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    void testListarCaracteristica() throws Exception {
        //Given
        List<Caracteristicas> caracteristicasList = new ArrayList<>();
        caracteristicasList.add(caracteristicas);
        caracteristicasList.add(caracteristicas);
        caracteristicasList.add(caracteristicas);
        given(caracteristicasService.listar()).willReturn(caracteristicasList);
        //When
        ResultActions response = mockMvc.perform(get("/caracteristicas/listar"));
        //Then
        response.andExpect(jsonPath("$.size()").value(3))
                .andDo(print());
    }
    @Test
    void testModificarCaracteristica() throws Exception {
        //Given
        given(caracteristicasService.buscarPorId(caracteristicaId)).willReturn(Optional.of(caracteristicas));
        given(caracteristicasService.buscarPorNombre(nombre)).willReturn(Optional.empty());
        willDoNothing().given(caracteristicasService).modificar(caracteristicas,caracteristicaId);
        caracteristicas.setIcono("Pileta.icono");
        //When
        ResultActions response = mockMvc.perform(put("/caracteristicas/modificar/{id}", caracteristicaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(caracteristicas)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    void testModificarCaracteristicaNoExistente() throws Exception {
        //Given
        given(caracteristicasService.buscarPorId(caracteristicaId)).willReturn(Optional.empty());
        given(caracteristicasService.buscarPorNombre(nombre)).willReturn(Optional.empty());
        caracteristicas.setIcono("Pileta.icono");
        //When
        ResultActions response = mockMvc.perform(put("/caracteristicas/modificar/{id}", caracteristicaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(caracteristicas)));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testELiminarCaracteristica() throws Exception {
        //Given
        given(caracteristicasService.buscarPorId(caracteristicaId)).willReturn(Optional.of(caracteristicas));
        willDoNothing().given(caracteristicasService).eliminar(caracteristicaId);
        //When
        ResultActions response = mockMvc.perform(delete("/caracteristicas/eliminar/{id}", caracteristicaId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void testEliminarCaracteristicaNoExistente() throws Exception {
        //Given
        given(caracteristicasService.buscarPorId(caracteristicaId)).willReturn(Optional.empty());
        willDoNothing().given(caracteristicasService).eliminar(caracteristicaId);
        //When
        ResultActions response = mockMvc.perform(delete("/caracteristicas/eliminar/{id}", caracteristicaId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testBuscarCaracteristicaPorId() throws Exception {
        //Given
        given(caracteristicasService.buscarPorId(caracteristicaId)).willReturn(Optional.of(caracteristicas));
        //When
        ResultActions response = mockMvc.perform(get("/caracteristicas/buscar/{id}", caracteristicaId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1));
    }
    @Test
    void testBuscarCaracteristicaPorIdNoExistente() throws Exception {
        //Given
        given(caracteristicasService.buscarPorId(caracteristicaId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/caracteristicas/buscar/{id}", caracteristicaId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testBuscarCaracteristicaPorNombre() throws Exception {
        //Given
        given(caracteristicasService.buscarPorNombre(nombre)).willReturn(Optional.of(caracteristicas));
        //When
        ResultActions response = mockMvc.perform(get("/caracteristicas/buscarNombre/{nombre}", nombre));
        //Then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre").value(nombre));
    }

    @Test
    void testBuscarCaracteristicaPorNombreNoExistente() throws Exception {
        //Given
        given(caracteristicasService.buscarPorNombre(nombre)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/caracteristicas/buscarNombre/{nombre}", nombre));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
}
