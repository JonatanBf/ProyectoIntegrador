package ImagenLIst.demo.Controller;

import ImagenLIst.demo.entidades.Categoria;
import ImagenLIst.demo.entidades.Ciudad;
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


import java.util.Optional;

import static org.mockito.BDDMockito.given;


@WebMvcTest
public class CiudadControllerTest {

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
    void testAgregarCiudad() throws Exception {
        //Given
        given(ciudadService.buscarPorCiudad(nombre)).willReturn(Optional.empty());
        given(ciudadService.agregar(ciudad)).willReturn(ciudad);
        //When
        ResultActions response = mockMvc.perform(post("/ciudad/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ciudad)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    void testAgregarCiudadYaExistente() throws Exception {
        //Given
        given(ciudadService.buscarPorCiudad(nombre)).willReturn(Optional.of(ciudad));
        //When
        ResultActions response = mockMvc.perform(post("/ciudad/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ciudad)));
        //Then
        response.andExpect(status().isBadRequest())
                .andDo(print());
    }
    @Test
    void testListarCiudad() throws Exception {
        //Given
        List<Ciudad> ciudadList = new ArrayList<>();
        ciudadList.add(ciudad);
        ciudadList.add(ciudad);
        ciudadList.add(ciudad);
        given(ciudadService.listar()).willReturn(ciudadList);

        //When
        ResultActions response = mockMvc.perform(get("/ciudad/listar"));
        //Then
        response.andExpect(jsonPath("$.size()").value(3))
                .andDo(print());
    }
    @Test
    void testModificarCiudad() throws Exception {
        //Given
        given(ciudadService.buscarPorId(ciudadId)).willReturn(Optional.of(ciudad));
        //When
        ResultActions response = mockMvc.perform(put("/ciudad/modificar/{id}", ciudadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ciudad)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    void testModificarCiudadNoExistente() throws Exception {
        //Given
        given(ciudadService.buscarPorId(ciudadId)).willReturn(Optional.empty());
        ResultActions response = mockMvc.perform(put("/ciudad/modificar/{id}", ciudadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ciudad)));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testBuscarCiudadPorId() throws Exception {
        //Given
        given(ciudadService.buscarPorId(ciudadId)).willReturn(Optional.of(ciudad));
        //When
        ResultActions response = mockMvc.perform(get("/ciudad/buscar/{id}", ciudadId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre").value("Bogota"))
                .andExpect(jsonPath("$.pais").value("Colombia"));
    }
    @Test
    void testBuscarCiudadPorIdNoExistente() throws Exception {
        //Given
        given(ciudadService.buscarPorId(ciudadId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/ciudad/buscar/{id}", ciudadId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testEliminarCiudad() throws Exception {
        //Given
        given(ciudadService.buscarPorId(ciudadId)).willReturn(Optional.of(ciudad));

        //When
        ResultActions response = mockMvc.perform(delete("/ciudad/eliminar/{id}", ciudadId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void testEliminarCiudadNoExistente() throws Exception {
        //Given
        given(ciudadService.buscarPorId(ciudadId)).willReturn(Optional.empty());

        //When
        ResultActions response = mockMvc.perform(delete("/ciudad/eliminar/{id}", ciudadId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testBuscarCiudadPorNombre() throws Exception {
        //Given
        given(ciudadService.buscarPorCiudad(nombre)).willReturn(Optional.of(ciudad));
        //When
        ResultActions response = mockMvc.perform(get("/ciudad/buscarCiudad/{ciudad}", nombre));
        //Then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre").value("Bogota"))
                .andExpect(jsonPath("$.pais").value("Colombia"));

    }
    @Test
    void testBuscarCiudadPorNombreNoExistente() throws Exception {
        //Given
        given(ciudadService.buscarPorCiudad(nombre)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/ciudad/buscarCiudad/{ciudad}", nombre));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
}
