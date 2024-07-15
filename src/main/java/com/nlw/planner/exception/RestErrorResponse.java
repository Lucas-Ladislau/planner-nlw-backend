package com.nlw.planner.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Setter
@Getter
public class RestErrorResponse {

    private Integer status;
    private HttpStatus error;
    private String message;

}
