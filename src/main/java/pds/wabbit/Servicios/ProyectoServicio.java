package pds.wabbit.Servicios;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Enumeraciones.Lenguajes;
import pds.wabbit.Enumeraciones.Temas;
import pds.wabbit.Repositorios.IPostulacionRepositorio;
import pds.wabbit.Repositorios.IProyectoRepositorio;
import pds.wabbit.errores.ErrorServicio;

@Service
public class ProyectoServicio {

    @Autowired
    private IProyectoRepositorio proyectoRepositorio;

    @Autowired
    private IPostulacionRepositorio postulacionRepositorio;

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

    public Map<String, List<String>> temasFront(String idUser) {
        Map<String, List<String>> temas = new HashMap<>();
        //String colores[]={"#663259","#6610f2","#1B283F","#181C32"};
        String svg[] = {"abstract-4.svg", "abstract-2.svg", "abstract-3.svg", "abstract-1.svg"};
        int c = 0;
        for (Temas value : Temas.values()) {
            List<String> valores = new ArrayList<>();
            valores.add(value.getDescrip());
            //valores.add(colores[c]);
            valores.add(svg[c]);
            Integer numeroProyectos = proyectoRepositorio.cantidadProyectosPorTema(value, idUser);
            valores.add(numeroProyectos.toString());
            valores.add(value.toString());
            temas.put(value.getValue(), valores);
            c++;
            if (c == 4) {
                c = 0;
            }
        }
        return temas;
    }

    @Transactional
    public List<Proyecto> proyectosPorIds(List<String> idProyectos) {
        return proyectoRepositorio.findAllById(idProyectos);
    }

    public Map<String, List<String>> lenguajesFront(String idUser) {
        Map<String, List<String>> lenguajes = new HashMap<>();
        //String colores[]={"#663259","#6610f2","#1B283F","#181C32"};
        String svg[] = {"abstract-4.svg", "abstract-2.svg", "abstract-3.svg", "abstract-1.svg"};
        int c = 0;
        //Query nativeQuery = em.createNativeQuery("SELECT COUNT(*) FROM proyecto_lenguajes p WHERE p.lenguajes=", Solicitud.class);
        //List<Solicitud> results = (List<Solicitud>) nativeQuery.getResultList();
        for (Lenguajes value : Lenguajes.values()) {
            List<String> valores = new ArrayList<>();
            valores.add(value.getDescrip());
            //valores.add(colores[c]);
            valores.add(svg[c]);
            Integer numeroProyectos = 0;
            numeroProyectos = proyectosPorIds(proyectoRepositorio.idProyectosPorLenguaje(value.toString())).stream().filter(proyectosPorId -> (proyectosPorId.getUsuarioCreador().getId() == null ? idUser != null : !proyectosPorId.getUsuarioCreador().getId().equals(idUser))).map(_item -> 1).reduce(numeroProyectos, Integer::sum);
            valores.add(numeroProyectos.toString());
            valores.add(value.toString());
            lenguajes.put(value.getValue(), valores);
            c++;
            if (c == 4) {
                c = 0;
            }
        }
        return lenguajes;
    }

    public List<String> idProyectosPorUsuario(String idUsuario) {
        return postulacionRepositorio.idProyectosPostuladosPorUsuario(idUsuario);
    }
    
    @Transactional
    public Page<Proyecto> getPorTema(Temas temaP, Pageable pageable) {
        List<Proyecto> proyectos = proyectoRepositorio.proyectosPorTema(temaP);
        return listConvertToPage1(proyectos, pageable);
    }
    
    
    public Page<Proyecto> filtroTema(String filtro, Temas temaP, Pageable pageable) {
        List<Proyecto> copy = new ArrayList<Proyecto>(proyectoRepositorio.proyectosPorTema(temaP));
        copy.removeIf(o
                -> (!o.getNombre().toLowerCase().contains(filtro))
        );
        return listConvertToPage1(copy, pageable);
    }
    
    public Page<Proyecto> filtroLenguaje(String filtro,String lenguaje, Pageable pageable) {
        List<String> id = proyectoRepositorio.idProyectosPorLenguaje(lenguaje);
        List<Proyecto> proyectos = proyectoRepositorio.findAllById(id);
        List<Proyecto> copy = new ArrayList<Proyecto>(proyectos);
        copy.removeIf(o
                -> (!o.getNombre().toLowerCase().contains(filtro))
        );
       return listConvertToPage1(copy, pageable);
    }
    
    @Transactional
    public Page<Proyecto> getPorLenguaje(String leng, Pageable pageable) {
        List<String> id = proyectoRepositorio.idProyectosPorLenguaje(leng);
        List<Proyecto> proyectos = proyectoRepositorio.findAllById(id);
        return listConvertToPage1(proyectos, pageable);
    }

    public static <T> Page<T> listConvertToPage1(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

}
