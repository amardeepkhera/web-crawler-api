package au.com.qantas.web

import au.com.qantas.GetWebResourceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(GetWebResourceException::class)
    fun handleGetWebResourceException(exception: GetWebResourceException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.valueOf(exception.code)).body(
            ErrorResponse(
                exception.code,
                exception.message ?: ""
            )
        )

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.message ?: ""
            )
        )

}