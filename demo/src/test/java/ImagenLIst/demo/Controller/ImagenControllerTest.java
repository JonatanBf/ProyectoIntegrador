package ImagenLIst.demo.Controller;

import ImagenLIst.demo.entidades.Imagen;
import ImagenLIst.demo.repository.ImagenRepository;
import ImagenLIst.demo.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class ImagenControllerTest {

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
    void testListarImagen() throws Exception {
        //Given
        List<Imagen> imagenList = new ArrayList<>();
        imagenList.add(imagen);
        imagenList.add(imagen);
        imagenList.add(imagen);
        given(imagenService.listar()).willReturn(imagenList);
        //When
        ResultActions response = mockMvc.perform(get("/imagen/listar"));
        //Then
        response.andExpect(jsonPath("$.size()").value(3))
                .andDo(print());
    }
    @Test
    void testModificarImagen() throws Exception {
        //Given
        given(imagenService.buscarPorId(imagenId)).willReturn(Optional.of(imagen));
        willDoNothing().given(imagenService).modificar(imagen,imagenId);
        //When
        ResultActions response = mockMvc.perform(put("/imagen/modificar/{id}", imagenId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imagen)));
        //Then
        response.andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    void testModificarImagenNoExistente() throws Exception {
        //Given
        given(imagenService.buscarPorId(imagenId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(put("/imagen/modificar/{id}", imagenId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imagen)));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void testEliminarImagen() throws Exception {
        //Given
        given(imagenService.buscarPorId(imagenId)).willReturn(Optional.of(imagen));
        //When
        ResultActions response = mockMvc.perform(delete("/imagen/eliminar/{id}",imagenId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    void testEliminarImagenNoExistente() throws Exception {
        //Given
        given(imagenService.buscarPorId(imagenId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(delete("/imagen/eliminar/{id}",imagenId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void  testBuscarImagenPorId() throws Exception {
        //Given
        given(imagenService.buscarPorId(imagenId)).willReturn(Optional.of(imagen));
        //When
        ResultActions response = mockMvc.perform(get("/imagen/buscar/{id}",imagenId));
        //Then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.titulo").value("Imagen 1"));
    }
    @Test
    void  testBuscarImagenPorIdNoExistente() throws Exception {
        //Given
        given(imagenService.buscarPorId(imagenId)).willReturn(Optional.empty());
        //When
        ResultActions response = mockMvc.perform(get("/imagen/buscar/{id}",imagenId));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());

    }
    @Test
    void  testBuscarImagenPorTitulo() throws Exception {
        //Given
        given(imagenService.buscarTitulo(titulo)).willReturn(List.of(imagen));
        //When
        ResultActions response = mockMvc.perform(get("/imagen/buscarTitulo/{titulo}",titulo));
        //Then
        response.andExpect(status().isOk())
                .andDo(print());

    }
    @Test
    void  testBuscarImagenPorTituloNoExistente() throws Exception {
        //Given
        given(imagenService.buscarTitulo(titulo)).willReturn(List.of());
        //When
        ResultActions response = mockMvc.perform(get("/imagen/buscarTitulo/{titulo}",titulo));
        //Then
        response.andExpect(status().isNotFound())
                .andDo(print());

    }
}
