
package pds.wabbit.errores;

import java.io.IOException;
import java.net.MalformedURLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import pds.wabbit.Entidades.ResponseJson;

@ControllerAdvice
public class FileUploadExceptionAdvice {
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseJson> handleMaxSizeException(MaxUploadSizeExceededException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseJson("Verifica Tamaño de archivos"));
    }
    
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseJson> handleException(IOException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseJson("Error de IO"));
    }
    
    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<ResponseJson> handleMalformedURLException(MalformedURLException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseJson("Error en la forma de la url"));
    }
    
}
