package com.moscichowski.WebWonders

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Error

data class RestError(val message: String)

class InvalidTokenError: Error()
class ForbiddenError: Error()

@ControllerAdvice
public class RestResponseEntityExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(InvalidTokenError::class)
    public fun handleAuthError(ex: InvalidTokenError, request: WebRequest): ResponseEntity<RestError> {
        return ResponseEntity.status(401).body(RestError("Jo nie trybi"))
    }

    @ExceptionHandler(ForbiddenError::class)
    public fun handleForbiddenError(ex: ForbiddenError, request: WebRequest): ResponseEntity<RestError> {
        return ResponseEntity.status(403).body(RestError("Nie Twoja kolej ino!"))
    }

    @ExceptionHandler(Error::class)
    public fun handleError(ex: Error, request: WebRequest): ResponseEntity<RestError> {
        return ResponseEntity.badRequest().body(RestError(ex.localizedMessage))

    }
}