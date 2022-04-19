
package pds.wabbit.Entidades;

import lombok.Data;

@Data
public class ResponseJson {
    private String message;

    public ResponseJson(String message) {
        this.message = message;
    }
    
}
