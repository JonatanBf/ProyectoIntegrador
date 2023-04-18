package ImagenLIst.demo.Controller;

import ImagenLIst.demo.entidades.Politicas;
import ImagenLIst.demo.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PoliticasControllerTest {

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
    void testAgregarPolitica() throws Exception {
        //Given
        given(politicasService.buscarNombre(nombre)).willReturn(Optional.empty());
        given(politicasService.agregar(politicas)).willReturn(politicas);
        //When
        ResultActions response = mockMvc.perform(post("/politicas/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(politicas)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    void testListarPolitica() throws Exception {
        //Given
        List<Politicas> politicasList = new ArrayList<>();
        politicasList.add(politicas);
        politicasList.add(politicas);
        politicasList.add(politicas);
        given(politicasService.listar()).willReturn(politicasList);
        //When
        ResultActions response = mockMvc.perform(get("/politicas/listar"));
        //Then
        response.andExpect(jsonPath("$.size()").value(3))
                .andDo(print());
    }
    @Test
    void testModificarPolitica() throws Exception {
        //Given
        given(politicasService.buscarPorId(politicaId)).willReturn(Optional.of(politicas));
        given(politicasService.buscarNombre(nombre)).willReturn(Optional.empty());
        willDoNothing().given(politicasService).modificar(politicas, politicaId);
        //When
        ResultActions response = mockMvc.perform(put("/politicas/modificar/{id}", politicaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(politicas)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    void testModificarPoliticaNoExistente() throws Exception {
        //Given
        given(politicasService.buscarPorId(politicaId)).willReturn(Optional.empty());
        given(politicasService.buscarNombre(nombre)).willReturn(Optional.empty());
        willDoNothing().given(politicasService).modificar(politicas, politicaId);
        //When
        ResultActions response = mockMvc.perform(put("/politicas/modificar/{id}", politicaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(politicas)));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testElimnarPolitica() throws Exception {
        //Given
        given(politicasService.buscarPorId(politicaId)).willReturn(Optional.of(politicas));
        willDoNothing().given(politicasService).eliminar(politicaId);
        //When
        ResultActions response = mockMvc.perform(delete("/politicas/eliminar/{id}", politicaId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void testElimnarPoliticaNoExistente() throws Exception {
        //Given
        given(politicasService.buscarPorId(politicaId)).willReturn(Optional.empty());
        willDoNothing().given(politicasService).eliminar(politicaId);
        //When
        ResultActions response = mockMvc.perform(delete("/politicas/eliminar/{id}", politicaId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testBuscarPoliticaPorId() throws Exception {
        //Given
        given(politicasService.buscarPorId(politicaId)).willReturn(Optional.of(politicas));
        //When
        ResultActions response = mockMvc.perform(get("/politicas/buscar/{id}", politicaId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void testBuscarPoliticaPorIdNoExistente() throws Exception {
        //Given
        given(politicasService.buscarPorId(politicaId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/politicas/buscar/{id}", politicaId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());//Given
        //When
        //Then
    }
    @Test
    void testBuscarPoliticaPorNombre() throws Exception {
        //Given
        given(politicasService.buscarNombre(nombre)).willReturn(Optional.of(politicas));
        //When
        ResultActions response = mockMvc.perform(get("/politicas/buscarNombre/{nombre}", nombre));
        //Then
        response.andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void testBuscarPoliticaPorNombreNoExistente() throws Exception {
        //Given
        given(politicasService.buscarNombre(nombre)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/politicas/buscarNombre/{nombre}", nombre));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
}
