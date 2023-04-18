package ImagenLIst.demo.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservaDto {

    @NotNull
    private LocalTime hora_Comienzo;

    private LocalTime hora_Final;

    @NotNull
    private LocalDate fecha_Inicial;

    @NotNull
    private LocalDate fecha_Final;

    @NotNull
    private Long id_Producto;

    @NotNull
    private Long id_Cliente;
}
