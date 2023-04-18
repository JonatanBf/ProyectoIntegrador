package ImagenLIst.demo.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PuntuacionDto {

    @NotNull
    private Long id_Producto;

    @NotNull
    private Long id_Usuario;

    @NotNull
    @Min(1)
    @Max(5)
    private Long puntuacion;
}
