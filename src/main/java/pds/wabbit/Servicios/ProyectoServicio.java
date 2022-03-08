package pds.wabbit.Servicios;


import java.util.Calendar;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Enumeraciones.Temas;
import pds.wabbit.Repositorios.IProyectoRepositorio;
import pds.wabbit.errores.ErrorServicio;

@Service
public class ProyectoServicio {

    @Autowired
    private IProyectoRepositorio proyectoRepositorio;


    public Integer cantidadProyectosPorUsuario(String idUsuario) {
        return proyectoRepositorio.cantidadProyectosPorUsuario(idUsuario);
    }

    
    
    @Transactional
    public List<Proyecto> allProjects() {
        return proyectoRepositorio.allProjects();
    }
    
    @Transactional
    public List<Proyecto> projectsRecientes() {
        return proyectoRepositorio.allRecents();
    }
    
    

    public Proyecto buscarProyectoPorId(String id) throws ErrorServicio {
        Proyecto proyecto = proyectoRepositorio.findById(id).get();
        if (proyecto != null) {
            return proyecto;
        } else {
            throw new ErrorServicio("No hay proyectos con este id");
        }
    }

    @Transactional
    public Proyecto crearProyecto(Proyecto proyecto) {
        proyecto.setFechaCreacion(Calendar.getInstance());
        return proyectoRepositorio.save(proyecto);
    }
    
    @Transactional
    public List<Proyecto> proyectosPorUsuario(String idUsuario) {
        return proyectoRepositorio.proyectosPorUsuario(idUsuario);
    }

    

    //Warning
    @Transactional
    public List<Proyecto> proyectosPorTema(Temas temaP) {
        return proyectoRepositorio.proyectosPorTema(temaP);
    }
    
}
