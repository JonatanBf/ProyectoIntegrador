package ImagenLIst.demo.excepciones;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private HttpStatus status;
    private String message;
    private List<String> errors;

}
