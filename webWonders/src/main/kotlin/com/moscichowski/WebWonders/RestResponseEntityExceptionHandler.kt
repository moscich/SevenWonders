package com.moscichowski.WebWonders

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Error

class AwesomeException: Exception() {

}

@EnableWebMvc
@ControllerAdvice
public class RestResponseEntityExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(Error::class)
    public fun handleError(ex: Error, request: WebRequest): ResponseEntity<String> {
        return ResponseEntity.badRequest().body("message + ${ex.localizedMessage}")
    }
}