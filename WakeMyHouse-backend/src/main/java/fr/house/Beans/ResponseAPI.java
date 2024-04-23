package fr.house.Beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ResponseAPI {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String type;
    private List<String> errors_msg;
}
