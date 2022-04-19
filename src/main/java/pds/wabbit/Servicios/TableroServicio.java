package pds.wabbit.Servicios;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Entidades.Tablero;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Repositorios.ITableroRepositorio;
import pds.wabbit.errores.ErrorServicio;

@Service
public class TableroServicio {

    @Autowired
    private ITableroRepositorio tableroRepositorio;

    @Autowired
    private ProyectoServicio proyectoServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    public String listarJSON(String proyectoID) throws JSONException, ErrorServicio {
        JSONArray results = new JSONArray();
        List<Tablero> tareas = tableroRepositorio.tareasProyecto(proyectoID);
        String[] id = {"_todo", "_working", "_done", "_notes"};
        String[] title = {"Por Hacer", "Trabajando", "Hecho", "Notas"};
        String[] classe = {"light-primary", "light-warning", "light-success", "light-danger"};
        String[] classItem = {"primary toDo", "warning work", "success Done", "danger Not"};
        for (int i = 0; i < 4; i++) {
            JSONObject objetoData = new JSONObject();
            objetoData.put("id", id[i]);
            objetoData.put("title", title[i]);
            objetoData.put("class", classe[i]);
            JSONArray item = new JSONArray();
            for (Tablero tarea : tareas) {
                if (id[i].equals(tarea.getTipo())) {
                    JSONObject itemObject = new JSONObject();
                    itemObject.put("title", tarea.getTitulo() + "<br> <span class='label label-inline label-light-success font-weight-bold'>" + tarea.getUsuario().getNombre() + " " + tarea.getUsuario().getApellido() + "</span>");
                    itemObject.put("tarea", tarea.getId());
                    switch (tarea.getTipo()) {
                        case "_todo":
                            itemObject.put("class", classItem[0]);
                            break;
                        case "_working":
                            itemObject.put("class", classItem[1]);
                            break;
                        case "_done":
                            itemObject.put("class", classItem[2]);
                            break;
                        case "_notes":
                            itemObject.put("class", classItem[3]);
                            break;
                    }
                    item.put(itemObject);
                }
            }
            objetoData.put("item", item);
            results.put(objetoData);
        }
        return results.toString();
    }

    public String crearTarea(Map<String, Object> params) throws JSONException, ErrorServicio {
        JSONArray results = new JSONArray();
        JSONObject objetoData = new JSONObject();
        Tablero tablero = new Tablero();
        Proyecto proyecto = proyectoServicio.buscarProyectoPorId(params.get("idPt").toString());
        Usuario usuario = usuarioServicio.buscarUsuarioPorId(params.get("idUs").toString());
        tablero.setProyecto(proyecto);
        tablero.setUsuario(usuario);
        tablero.setTipo(params.get("valueTipo").toString());
        tablero.setTitulo(params.get("tarea").toString());
        tableroRepositorio.save(tablero);
        objetoData.put("status", "exito");
        objetoData.put("mensaje", "Bien");
        return results.toString();
    }
    
    public String cambioTarea(String tareaID,String tipo) throws JSONException, ErrorServicio {
        JSONArray results = new JSONArray();
        JSONObject objetoData = new JSONObject();
        Tablero tablero = buscarTareaPorId(tareaID);
        tablero.setTipo(tipo);
        tableroRepositorio.save(tablero);
        objetoData.put("status", "exito");
        objetoData.put("mensaje", "Bien");
        return results.toString();
    }
    
    public Tablero buscarTareaPorId(String id) throws ErrorServicio {
        Tablero tablero = tableroRepositorio.findById(id).get();
        if (tablero != null) {
            return tablero;
        } else {
            throw new ErrorServicio("No hay tareas con ese nombre");
        }
    }
}
