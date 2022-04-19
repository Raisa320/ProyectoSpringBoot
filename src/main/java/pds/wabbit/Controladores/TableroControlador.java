
package pds.wabbit.Controladores;

import java.util.Map;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pds.wabbit.Servicios.ProyectoServicio;
import pds.wabbit.Servicios.TableroServicio;
import pds.wabbit.Servicios.UsuarioServicio;
import pds.wabbit.errores.ErrorServicio;

@Controller
@RequestMapping("/tablero")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class TableroControlador {
    
    @Autowired
    private TableroServicio tableroServicio;
    
   
    
    @RequestMapping(value = "/lista/{proyectoID}", method = RequestMethod.GET)
    @ResponseBody
    public String listadoJSON( @PathVariable String proyectoID) throws JSONException, ErrorServicio {
        return tableroServicio.listarJSON(proyectoID);
    }
    
    @RequestMapping(value = "/cambio/{idTarea}/{idBoard}", method = RequestMethod.GET)
    @ResponseBody
    public String cambioTablero( @PathVariable String idTarea,@PathVariable String idBoard) throws JSONException, ErrorServicio {
        return tableroServicio.cambioTarea(idTarea,idBoard);
    }
    
    @RequestMapping(value = "/crearTarea", method = RequestMethod.POST)
    @ResponseBody
    public String guadarTarea(@RequestParam Map<String, Object> params) throws JSONException, ErrorServicio {
        return tableroServicio.crearTarea(params);
    }
}
