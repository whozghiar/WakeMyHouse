package fr.house.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DockerException extends RuntimeException {

    private String message;
    private Throwable cause;

}