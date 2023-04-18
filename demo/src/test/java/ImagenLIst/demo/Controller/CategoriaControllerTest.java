package ImagenLIst.demo.Controller;

import ImagenLIst.demo.entidades.Categoria;
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
public class CategoriaControllerTest {

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


    private Categoria categoria = new Categoria();

    @BeforeEach
    void setUP(){
        categoria.setTitulo("Hotel");
        categoria.setUrl("Hotel.url");
        categoria.setDescripcion("Es un hotel");
    }

    @Test
    void testAgregarCategoria() throws Exception {
        //Given
        given(categoriaService.buscarTitulo(categoria.getTitulo())).willReturn(Optional.empty());
        given(categoriaService.agregar(categoria)).willReturn(categoria);
        //When
        ResultActions response = mockMvc.perform(post("/categoria/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }


    @Test
    void testAgregarCategoriaYaExistente() throws Exception {
        //Given
        given(categoriaService.buscarTitulo(categoria.getTitulo())).willReturn(Optional.of(categoria));
        given(categoriaService.agregar(categoria)).willReturn(categoria);
        //When
        ResultActions response = mockMvc.perform(post("/categoria/agregar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)));
        //Then
        response.andExpect(status().isBadRequest())
                .andDo(print());
    }
    @Test
    void testModificarCategoria() throws Exception {
        //Given
        Long categoriaId = 1L;
        given(categoriaService.buscarPorId(categoriaId)).willReturn(Optional.of(categoria));
        willDoNothing().given(categoriaService).modificar(categoria, categoriaId);
        //When
        ResultActions response = mockMvc.perform(put("/categoria/modificar/{id}", categoriaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }@Test
    void testModificarCategoriaNoExistente() throws Exception {
        //Given
        Long categoriaId = 1L;
        given(categoriaService.buscarPorId(categoriaId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(put("/categoria/modificar/{id}", categoriaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }@Test
    void testListarCategoria() throws Exception {
        //Given
        List<Categoria> categoriaList = new ArrayList<>();
        categoriaList.add(categoria);
        categoriaList.add(categoria);
        categoriaList.add(categoria);
        given(categoriaService.listar()).willReturn(categoriaList);
        //When
        ResultActions response = mockMvc.perform(get("/categoria/listar"));
        //Then
        response.andExpect(jsonPath("$.size()").value(3))
                .andDo(print());
    }
    @Test
    void testEliminarCategoria() throws Exception {
        //Given
        Long categoriaId = 1L;
        given(categoriaService.buscarPorId(categoriaId)).willReturn(Optional.of(categoria));
        willDoNothing().given(categoriaService).eliminar(categoriaId);
        //When
        ResultActions response = mockMvc.perform(delete("/categoria/eliminar/{id}", categoriaId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void testEliminarCategoriaNoExistente() throws Exception {
        //Given
        Long categoriaId = 1L;
        given(categoriaService.buscarPorId(categoriaId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(delete("/categoria/eliminar/{id}", categoriaId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testBuscarCategoriaPorId() throws Exception {
        //Given
        Long categoriaId = 1L;
        given(categoriaService.buscarPorId(categoriaId)).willReturn(Optional.of(categoria));
        //When
        ResultActions response = mockMvc.perform(get("/categoria/buscar/{id}", categoriaId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.titulo").value("Hotel"))
                .andExpect(jsonPath("$.url").value("Hotel.url"))
                .andExpect(jsonPath("$.descripcion").value("Es un hotel"));
    }
    @Test
    void testBuscarCategoriaPorIdNoExistente() throws Exception {
        //Given
        Long categoriaId = 1L;
        given(categoriaService.buscarPorId(categoriaId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/categoria/buscar/{id}", categoriaId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testBuscarCategoriaPorTitulo() throws Exception {
        //Given
        String categoriaNombre = "Hotel";
        given(categoriaService.buscarTitulo(categoriaNombre)).willReturn(Optional.of(categoria));
        //When
        ResultActions response = mockMvc.perform(get("/categoria/buscarTitulo/{titulo}", categoriaNombre));
        //Then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.titulo").value("Hotel"))
                .andExpect(jsonPath("$.url").value("Hotel.url"))
                .andExpect(jsonPath("$.descripcion").value("Es un hotel"));

    }
    @Test
    void testBuscarCategoriaPorTituloNoExistente() throws Exception {
        //Given
        String categoriaNombre = "Hotel";
        given(categoriaService.buscarTitulo(categoriaNombre)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/categoria/buscarTitulo/{titulo}", categoriaNombre));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void testBuscarCategoriaPorDescripcion() throws Exception {
        //Given
        String categoriaDescripcion = "Es un hotel";
        given(categoriaService.buscarDescripcion(categoriaDescripcion)).willReturn(Optional.of(categoria));
        //When
        ResultActions response = mockMvc.perform(get("/categoria/buscarDescripcion/{descripcion}", categoriaDescripcion));
        //Then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.titulo").value("Hotel"))
                .andExpect(jsonPath("$.url").value("Hotel.url"))
                .andExpect(jsonPath("$.descripcion").value("Es un hotel"));

    }
    @Test
    void testBuscarCategoriaPorDescripcionNoExistente() throws Exception {
        //Given
        String categoriaDescripcion = "Es un hotel";
        given(categoriaService.buscarDescripcion(categoriaDescripcion)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/categoria/buscarDescripcion/{descripcion}", categoriaDescripcion));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }


}
