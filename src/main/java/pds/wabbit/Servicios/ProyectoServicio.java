package pds.wabbit.Servicios;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pds.wabbit.Entidades.Postulacion;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Entidades.Votos;
import pds.wabbit.Enumeraciones.EstadosProyecto;
import pds.wabbit.Enumeraciones.Lenguajes;
import pds.wabbit.Enumeraciones.Temas;
import pds.wabbit.Repositorios.IPostulacionRepositorio;
import pds.wabbit.Repositorios.IProyectoRepositorio;
import pds.wabbit.Repositorios.IUsuarioRepositorio;
import pds.wabbit.Repositorios.IVotoRepositorio;
import pds.wabbit.errores.ErrorServicio;

@Service
public class ProyectoServicio {

    @Autowired
    private IProyectoRepositorio proyectoRepositorio;

    @Autowired
    private IPostulacionRepositorio postulacionRepositorio;

    @Autowired
    private IUsuarioRepositorio usuarioRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private IVotoRepositorio votoRepositorio;
    
    @Value("${my.property.baseUrl}")
    private String url;

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

    public Page<Proyecto> filtroLenguaje(String filtro, String lenguaje, Pageable pageable) {
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

    @Transactional
    public List<Proyecto> proyectosGuardadosPorUsuario(String idUsuario) {
        List<String> id = proyectoRepositorio.idProyectosGuardadosPorUsuario(idUsuario);
        List<Proyecto> proyectos = proyectoRepositorio.findAllById(id);
        return proyectos;
    }

    @Transactional
    public List<Proyecto> projectsProceso() {
        return proyectoRepositorio.projectsProceso();
    }

    @Transactional
    public List<Proyecto> projectsVacio() {
        List<Proyecto> proyectos = new ArrayList<>();
        for (Proyecto proyecto : proyectoRepositorio.allRecents()) {
            if (proyecto.getParticipantes().isEmpty()) {
                proyectos.add(proyecto);
            }
        }
        return proyectos;
    }

    @Transactional
    public List<Proyecto> projectsPostulantes() {
        List<Proyecto> proyectos = new ArrayList<>();
        for (Proyecto proyecto : proyectoRepositorio.allRecents()) {
            if (!proyecto.getParticipantes().isEmpty()) {
                proyectos.add(proyecto);
            }
        }
        return proyectos;
    }

    @Transactional
    public List<Proyecto> proyectosDondeParticipa(String idUsuario) throws ErrorServicio {
        Set<String> user = new HashSet<>();
        user.add(idUsuario);
        return proyectoRepositorio.findByParticipantesIdIn(user);
    }

    @Transactional
    public void eliminar(String idProyecto, Usuario usuario) throws ErrorServicio {
        List<String> idUsuarios = proyectoRepositorio.idUsuariosPorProyectosGuardados(idProyecto);
        usuarioRepositorio.findAllById(idUsuarios).stream().filter(user -> (user.getProyectosGuardados().contains(idProyecto))).map(user -> {
            user.getProyectosGuardados().remove(idProyecto);
            return user;
        }).forEachOrdered(_item -> {
            usuarioRepositorio.save(usuario);
        });
        Proyecto t = buscarProyectoPorId(idProyecto);
        proyectoRepositorio.delete(t);
    }

    public List<Postulacion> postuladosProyectoUsuario(String idUsuario, String idProyecto) {
        return postulacionRepositorio.postulacionProyectoUsuario(idUsuario, idProyecto);
    }
    
    @Transactional
    public List<Proyecto> proyectosPostulados(String idUsuario) {
        return postulacionRepositorio.proyectosPostuladosPorUsuario(idUsuario);
    }

    public List<Votos> votosProyectoUsuario(String idUsuario, String idProyecto) {
        return votoRepositorio.votosProyectoUsuario(idUsuario, idProyecto);
    }

    public String guardarProyectoAjax(String idProyecto, String idUsuario) throws JSONException, ErrorServicio {
        Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
        List<String> proyectos = usuarioServicio.listadoProyectosGuardados(idUsuario);
        if (!proyectos.contains(idProyecto)) {
            proyectos.add(idProyecto);
            usuario.setProyectosGuardados(proyectos);
            usuarioRepositorio.save(usuario);
        }
        JSONArray results = new JSONArray();
        JSONObject objetoData = new JSONObject();
        objetoData.put("status", "ok");
        results.put(objetoData);
        return results.toString();
    }

    public String likeProyectoAjax(String idProyecto, String idUsuario) throws JSONException, ErrorServicio {
        Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
        Proyecto proyecto = buscarProyectoPorId(idProyecto);
        Integer nroVotos = proyecto.getVotos() == null ? 0 : proyecto.getVotos();
        nroVotos = nroVotos + 1;
        proyecto.setVotos(nroVotos);
        proyectoRepositorio.save(proyecto);
        Votos voto = new Votos();
        voto.setProyecto(proyecto);
        voto.setUsuario(usuario);
        votoRepositorio.save(voto);
        JSONArray results = new JSONArray();
        JSONObject objetoData = new JSONObject();
        objetoData.put("status", "ok");
        objetoData.put("nroMegusta", nroVotos);
        results.put(objetoData);
        return results.toString();
    }

    public String dislikeProyectoAjax(String idProyecto, String idUsuario) throws JSONException, ErrorServicio {
        Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
        Proyecto proyecto = buscarProyectoPorId(idProyecto);
        Integer nroVotos = proyecto.getVotos() == null ? 0 : proyecto.getVotos();
        nroVotos = nroVotos - 1;
        proyecto.setVotos(nroVotos);
        proyectoRepositorio.save(proyecto);
        List<Votos> votos = votoRepositorio.votosProyectoUsuario(idUsuario, idProyecto);
        votoRepositorio.deleteAll(votos);
        JSONArray results = new JSONArray();
        JSONObject objetoData = new JSONObject();
        objetoData.put("status", "ok");
        objetoData.put("nroMegusta", nroVotos);
        results.put(objetoData);
        return results.toString();
    }

    @Transactional
    public void finalizar(Proyecto proyecto) {
        proyecto.setEstadosProyecto(EstadosProyecto.Finalizado);
        proyectoRepositorio.save(proyecto);
    }

    @Transactional
    public void eliminarGuardado(String idProyecto, Usuario usuario) throws ErrorServicio {
        List<String> proyectos = usuarioServicio.listadoProyectosGuardados(usuario.getId());
        if (proyectos.contains(idProyecto)) {
            proyectos.remove(idProyecto);
            usuario.setProyectosGuardados(proyectos);
            usuarioRepositorio.save(usuario);
        }
    }

    @Transactional
    public void rechazarPostulacion(String idProyecto, String idUsuario) {
        Postulacion postulacion = postulacionRepositorio.postulacionProyectoUsuario(idUsuario, idProyecto).get(0);
        if (postulacion != null) {
            postulacionRepositorio.delete(postulacion);
        }
    }

    @Transactional
    public void aceptarPostulacion(String idProyecto, Usuario usuario) throws ErrorServicio {
        Proyecto proyecto = buscarProyectoPorId(idProyecto);
        List<Usuario> usuarios = proyectoRepositorio.usuariosEnProyecto(idProyecto);
        usuarios.add(usuario);
        proyecto.setParticipantes(usuarios);
        Integer vacantes = proyecto.getCupos();
        vacantes = vacantes - 1;
        proyecto.setCupos(vacantes);
        rechazarPostulacion(idProyecto, usuario.getId());
        proyectoRepositorio.save(proyecto);
    }

    @Transactional
    public Map<String, Object> postulacionProyecto(String idProyecto, String idUsuario,String mensaje) throws ErrorServicio {
        Postulacion postulacion = new Postulacion();
        postulacion.setProyecto(buscarProyectoPorId(idProyecto));
        postulacion.setUsuario(usuarioServicio.buscarUsuarioPorId(idUsuario));
        postulacion.setMensaje(mensaje);
        postulacionRepositorio.save(postulacion);
        /////VARIABLES  QUE VAN AL HTML DE ENVIO DE EMAIL
        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuarioServicio.buscarUsuarioPorId(idUsuario));
        model.put("titulo", "Alguien desea unirse a tu proyecto!");
        model.put("url", url + "/proyecto/ver-solicitud/" + idProyecto + "/" + idUsuario);
        model.put("titulo", "Alguien desea unirse a tu proyecto!");
        model.put("tituloBoton", "Ver Solicitud");
        model.put("cuerpo", "Estamos emocionados de notificarte que un usuario desea unirse a tu proyecto. Para ver la solicitud, simplemente presione el botón de abajo.");
        //FIN DE VARIABLES
        return model;

    }

}
