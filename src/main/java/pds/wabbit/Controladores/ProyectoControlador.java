package pds.wabbit.Controladores;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pds.wabbit.Entidades.Postulacion;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Enumeraciones.EstadosProyecto;
import pds.wabbit.Enumeraciones.Lenguajes;
import pds.wabbit.Enumeraciones.Temas;
import pds.wabbit.Repositorios.IProyectoRepositorio;
import pds.wabbit.Servicios.EmailServicio;
import pds.wabbit.Servicios.ProyectoServicio;
import pds.wabbit.Servicios.RecursoServicio;
import pds.wabbit.Servicios.UsuarioServicio;
import pds.wabbit.errores.ErrorServicio;

@Controller
@RequestMapping("/proyecto")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class ProyectoControlador {

    @Autowired
    private ProyectoServicio proyectoServicio;
    
    @Autowired
    private RecursoServicio recursoServicio;

    @Autowired
    private IProyectoRepositorio proyectoRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private EmailServicio mailServicio;

    @Value("${my.property.baseUrl}")
    private String url;

    @GetMapping("/crear")
    public String generarProyecto(Model modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Proyecto");
        Usuario perfil = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("lenguajes", Lenguajes.values());
        modelo.addAttribute("estados", EstadosProyecto.values());
        modelo.addAttribute("proyecto", new Proyecto());
        modelo.addAttribute("temas", Temas.values());
        return "proyecto/crearProyecto.html";
    }

    @GetMapping("/editar/{proyectoid}")
    public String editarProyecto(Model modelo, HttpSession session, @PathVariable String proyectoid) throws ErrorServicio {
        modelo.addAttribute("titulo", "Proyecto");
        Usuario perfil = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("lenguajes", Lenguajes.values());
        modelo.addAttribute("estados", EstadosProyecto.values());
        modelo.addAttribute("proyecto", proyectoServicio.buscarProyectoPorId(proyectoid));
        modelo.addAttribute("temas", Temas.values());
        modelo.addAttribute("modoEditar", "true");
        return "proyecto/crearProyecto.html";
    }

    @GetMapping("/misProyectos")
    public String misProyectos(Model modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Proyecto");
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("proyectos", proyectoServicio.proyectosPorUsuario(usuario.getId()));
        modelo.addAttribute("temas", Temas.class);
        modelo.addAttribute("activarItem", "mproyect");
        return "proyecto/misProyectos.html";
    }

    @GetMapping("/participante")
    public String participante(Model modelo, HttpSession session, RedirectAttributes redirectAttrs) {
        try {
            modelo.addAttribute("titulo", "Proyecto");
            Usuario usuario = (Usuario) session.getAttribute("usuariosession");
            modelo.addAttribute("proyectos", proyectoServicio.proyectosDondeParticipa(usuario.getId()));
            modelo.addAttribute("temas", Temas.class);
            modelo.addAttribute("activarItem", "mproyect");
            return "proyecto/proyectosParticipante.html";
        } catch (ErrorServicio ex) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Error en el id de usuario")
                    .addFlashAttribute("clase", "danger");
            return "redirect:/escritorio";
        }
    }

    @GetMapping("/proyectos/{idUsuario}")
    public String proyectosUsuario(Model modelo, HttpSession session, @PathVariable String idUsuario) throws ErrorServicio {
        modelo.addAttribute("titulo", "Proyectos");
        modelo.addAttribute("usuario", usuarioServicio.buscarUsuarioPorId(idUsuario));
        modelo.addAttribute("modoPerfil", "true");
        modelo.addAttribute("proyectos", proyectoServicio.proyectosPorUsuario(idUsuario));
        modelo.addAttribute("temas", Temas.class);
        return "proyecto/misProyectos.html";
    }

    @PostMapping("/create")
    public String postCrear(@Valid @ModelAttribute("proyecto") Proyecto proyecto, BindingResult result, ModelMap modelo, RedirectAttributes redirectAttrs, HttpSession session) {
        try {
            proyecto.setUsuarioCreador((Usuario) session.getAttribute("usuariosession"));
            Proyecto proyect = proyectoServicio.crearProyecto(proyecto);
            redirectAttrs
                    .addFlashAttribute("proyectoPublicado", proyect)
                    .addFlashAttribute("mensaje", "Proyecto Publicado correctamente")
                    .addFlashAttribute("clase", "success");
            return "redirect:/escritorio";
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
            return "redirect:/escritorio";
        }
    }

    @GetMapping("/temas")
    public String temasProyectos(Model modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Temas");
        Usuario user = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("temas", proyectoServicio.temasFront(user.getId()));
        modelo.addAttribute("activarItem", "temas");
        return "proyecto/temas.html";
    }

    @GetMapping("/lenguajes")
    public String lenguajesProyectos(Model modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Lenguajes");
        Usuario user = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("lenguajes", proyectoServicio.lenguajesFront(user.getId()));
        modelo.addAttribute("activarItem", "lenguajes");
        return "proyecto/lenguajes.html";
    }

    public Model baseModeloListar(String titulo, String item, String tema, Usuario user, Model modelo) {
        modelo.addAttribute("titulo", titulo);
        modelo.addAttribute("activarItem", item);
        modelo.addAttribute("listaPostulado", proyectoServicio.idProyectosPorUsuario(user.getId()));
        modelo.addAttribute("temaUrl", tema);
        return modelo;
    }

    public Model baseModeloPage(Map<String, Object> params, Page<Proyecto> pageProyectos, int page, Model modelo) {
        modelo.addAttribute("total", pageProyectos.getTotalElements());
        int totalPage = pageProyectos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            modelo.addAttribute("pages", pages);
        }
        modelo.addAttribute("proyectos", pageProyectos.getContent());
        modelo.addAttribute("mostrando", pageProyectos.getContent().size());
        modelo.addAttribute("current", page + 1);
        modelo.addAttribute("next", page + 2);
        modelo.addAttribute("prev", page);
        modelo.addAttribute("last", totalPage);
        return modelo;
    }

    @GetMapping("/listado/{tema}")
    public String proyectoTemas(Model modelo, @PathVariable String tema, @RequestParam Map<String, Object> params, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuariosession");
        baseModeloListar("Temas Proyectos", "temas", tema, user, modelo);
        modelo.addAttribute("lenguajesEnum", Lenguajes.class);
        modelo.addAttribute("temaTexto", Temas.valueOf(tema).getValue());
        int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        PageRequest pageRequest = PageRequest.of(page, 12);
        Page<Proyecto> pageProyectos = proyectoServicio.getPorTema(Temas.valueOf(tema), pageRequest);
        baseModeloPage(params, pageProyectos, page, modelo);
        return "proyecto/listadoTemas";
    }

    @GetMapping("/lenguaje/{leng}")
    public String proyectosLenguaje(Model modelo, @PathVariable String leng, @RequestParam Map<String, Object> params, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuariosession");
        baseModeloListar("Lenguajes Proyectos", "lenguajes", leng, user, modelo);
        modelo.addAttribute("temasEnum", Temas.class);
        modelo.addAttribute("temaTexto", Lenguajes.valueOf(leng).getValue());
        int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        PageRequest pageRequest = PageRequest.of(page, 12);
        Page<Proyecto> pageProyectos = proyectoServicio.getPorLenguaje(leng, pageRequest);
        baseModeloPage(params, pageProyectos, page, modelo);
        return "proyecto/listadoLenguajes";
    }

    @PostMapping("/buscar")
    public String buscarProyecto(@RequestParam Map<String, Object> params, Model modelo) {
        if (params.containsKey("listaTema") && params.get("listaTema") != "") {
            if (params.get("nombreP") == "" || params.get("nombreP") == null) {
                if (params.get("temaUrl") == "" || params.get("temaUrl") == null) {
                    return "redirect:/proyecto/" + params.get("listaTema");
                } else {
                    if (params.get("listaTema").toString().equals("temas")) {
                        return "redirect:/proyecto/listado/" + params.get("temaUrl");
                    } else if (params.get("listaTema").toString().equals("lenguajes")) {
                        return "redirect:/proyecto/lenguaje/" + params.get("temaUrl");
                    }
                }
            } else if (params.get("temaUrl") == "" || params.get("temaUrl") == null) {
                return "redirect:/proyecto/" + params.get("listaTema");
            } else {
                return "redirect:/proyecto/filter?nombreP=" + params.get("nombreP").toString() + "&temaUrl=" + params.get("temaUrl").toString() + "&listaTema=" + params.get("listaTema").toString();
            }
        }
        return "redirect:/escritorio";
    }

    @GetMapping("/filter")
    public String buscarListado(@RequestParam Map<String, Object> params, Model modelo, HttpSession session) {
        if (!params.isEmpty() && params.size() >= 3) {
            if (params.get("nombreP") == "" || params.get("temaUrl") == "" || params.get("listaTema") == "") {
                return "redirect:/escritorio";
            }
            Usuario user = (Usuario) session.getAttribute("usuariosession");
            int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
            PageRequest pageRequest = PageRequest.of(page, 12);
            Page<Proyecto> pageProyectos = Page.empty(pageRequest);
            String retorna = "redirect:/escritorio";
            if (params.get("listaTema").toString().equals("temas")) {
                pageProyectos = proyectoServicio.filtroTema(params.get("nombreP").toString(), Temas.valueOf(params.get("temaUrl").toString()), pageRequest);
                baseModeloListar("Temas Proyectos", "temas", params.get("temaUrl").toString(), user, modelo);
                modelo.addAttribute("lenguajesEnum", Lenguajes.class);
                modelo.addAttribute("temaTexto", Temas.valueOf(params.get("temaUrl").toString()).getValue());
                retorna = "proyecto/listadoTemas";
            } else if (params.get("listaTema").toString().equals("lenguajes")) {
                pageProyectos = proyectoServicio.filtroLenguaje(params.get("nombreP").toString(), params.get("temaUrl").toString(), pageRequest);
                baseModeloListar("Lenguajes Proyectos", "lenguajes", params.get("temaUrl").toString(), user, modelo);
                modelo.addAttribute("temasEnum", Temas.class);
                modelo.addAttribute("temaTexto", Lenguajes.valueOf(params.get("temaUrl").toString()).getValue());
                retorna = "proyecto/listadoLenguajes";
            }
            String url = "";
            modelo.addAttribute("nombreP", params.get("nombreP").toString());
            url = "/proyecto/filter?nombreP=" + params.get("nombreP").toString() + "&temaUrl=" + params.get("temaUrl").toString() + "&listaTema=" + params.get("listaTema").toString();

            baseModeloPage(params, pageProyectos, page, modelo);
            modelo.addAttribute("url", url);
            return retorna;
        }
        return "redirect:/escritorio";
    }

    @GetMapping("/guardados")
    public String gurdados(Model modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Proyecto");
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("proyectos", proyectoServicio.proyectosGuardadosPorUsuario(usuario.getId()));
        modelo.addAttribute("temas", Temas.class);
        modelo.addAttribute("activarItem", "mproyect");
        return "proyecto/proyectosGuardados.html";
    }

    @GetMapping("/proceso")
    public String proceso(ModelMap modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Proyectos");
        Usuario perfil = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("lenguajesEnum", Lenguajes.class);
        modelo.addAttribute("proyectos", proyectoServicio.projectsProceso());
        return "proyecto/listadoReportes";
    }

    @GetMapping("/vacios")
    public String vacio(ModelMap modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Proyectos");
        Usuario perfil = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("lenguajesEnum", Lenguajes.class);
        modelo.addAttribute("proyectos", proyectoServicio.projectsVacio());
        return "proyecto/listadoReportes";
    }

    @GetMapping("/postulantes")
    public String conPostulantes(ModelMap modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Proyectos");
        Usuario perfil = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("lenguajesEnum", Lenguajes.class);
        modelo.addAttribute("proyectos", proyectoServicio.projectsPostulantes());
        return "proyecto/listadoReportes";
    }

    @GetMapping("/eliminar-proyecto/{idProyecto}")
    public String eliminar(@PathVariable String idProyecto, RedirectAttributes redirectAttrs, HttpSession session) {
        try {
            Usuario perfil = (Usuario) session.getAttribute("usuariosession");
            proyectoServicio.eliminar(idProyecto, perfil);
            redirectAttrs
                    .addFlashAttribute("mensaje", "Proyecto eliminado correctamente")
                    .addFlashAttribute("clase", "success");
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
        }
        return "redirect:/proyecto/misProyectos";
    }

    @GetMapping("/eliminar-guardado/{idProyecto}")
    public String eliminarGuardado(@PathVariable String idProyecto, RedirectAttributes redirectAttrs, HttpSession session) {
        try {
            Usuario perfil = (Usuario) session.getAttribute("usuariosession");
            proyectoServicio.eliminarGuardado(idProyecto, perfil);
            redirectAttrs
                    .addFlashAttribute("mensaje", "Proyecto eliminado de la lista correctamente")
                    .addFlashAttribute("clase", "success");
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
        }
        return "redirect:/proyecto/guardados";
    }

    @GetMapping("/ver-proyecto/{idProyecto}")
    public String verProyecto(Model modelo, HttpSession session, @PathVariable String idProyecto) throws ErrorServicio {
        modelo.addAttribute("titulo", "Proyecto");
        Usuario perfil = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("lenguajesProgra", Lenguajes.class);
        modelo.addAttribute("temasEnum", Temas.class);
        modelo.addAttribute("estados", EstadosProyecto.values());
        modelo.addAttribute("proyecto", proyectoServicio.buscarProyectoPorId(idProyecto));
        String unido = "false";
        for (Usuario participante : proyectoServicio.buscarProyectoPorId(idProyecto).getParticipantes()) {
            if (participante.getId().equals(perfil.getId())) {
                unido = "true";
                break;
            }
        }
        if (!proyectoServicio.postuladosProyectoUsuario(perfil.getId(), idProyecto).isEmpty()) {
            modelo.addAttribute("unirse", "pendiente");
        }
        if (!proyectoServicio.votosProyectoUsuario(perfil.getId(), idProyecto).isEmpty()) {
            modelo.addAttribute("like", "true");
        }
        modelo.addAttribute("unido", unido);
        modelo.addAttribute("recursosLista", recursoServicio.recursosProyecto(idProyecto));
        //modelo.addAttribute("temas", Temas.values());
        return "proyecto/vistaProyecto.html";
    }

    @RequestMapping(value = "/like/{idProyecto}/{idUsuario}", method = RequestMethod.GET)
    @ResponseBody
    public String likeProyectoAjax(@PathVariable String idProyecto, @PathVariable String idUsuario) throws JSONException, ErrorServicio {
        return proyectoServicio.likeProyectoAjax(idProyecto, idUsuario);
    }

    @RequestMapping(value = "/dislike/{idProyecto}/{idUsuario}", method = RequestMethod.GET)
    @ResponseBody
    public String dislikeProyectoAjax(@PathVariable String idProyecto, @PathVariable String idUsuario) throws JSONException, ErrorServicio {
        return proyectoServicio.dislikeProyectoAjax(idProyecto, idUsuario);
    }

    @RequestMapping(value = "/guardar/{idProyecto}/{idUsuario}", method = RequestMethod.GET)
    @ResponseBody
    public String guardarProyectoAjax(@PathVariable String idProyecto, @PathVariable String idUsuario) throws JSONException, ErrorServicio {
        return proyectoServicio.guardarProyectoAjax(idProyecto, idUsuario);
    }

    @GetMapping("/finalizar/{idProyecto}")
    public String finalizar(Model modelo, @PathVariable String idProyecto) throws ErrorServicio {
        modelo.addAttribute("titulo", "Finalizar Proyecto");
        Proyecto proyecto = proyectoServicio.buscarProyectoPorId(idProyecto);
        proyectoServicio.finalizar(proyecto);
        return "redirect:/proyecto/ver-proyecto/" + idProyecto;
    }
    
    @RequestMapping(value = "/finalizarProyecto", method = RequestMethod.POST)
    @ResponseBody
    public String finalizarProyectoUrl(@RequestParam String id,@RequestParam String urlFinal) throws JSONException, ErrorServicio {
        return proyectoServicio.finalizarUrlProyecto(id, urlFinal);
    }

    @GetMapping("/rechazar/{proyectoId}/{usuarioId}")
    public String rechazar(Model modelo, @PathVariable String proyectoId, @PathVariable String usuarioId, RedirectAttributes redirectAttrs) {
        proyectoServicio.rechazarPostulacion(proyectoId, usuarioId);
        redirectAttrs
                .addFlashAttribute("mensaje", "Solicitud Rechazada y eliminada.")
                .addFlashAttribute("clase", "danger");
        return "redirect:/escritorio";
    }

    @GetMapping("/aceptar/{proyectoId}/{usuarioId}")
    public String aceptar(Model modelo, @PathVariable String proyectoId, @PathVariable String usuarioId, RedirectAttributes redirectAttrs) throws ErrorServicio {
        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(usuarioId);
            proyectoServicio.aceptarPostulacion(proyectoId, usuario);
            /////VARIABLES  QUE VAN AL HTML DE ENVIO DE EMAIL
            Map<String, Object> model = new HashMap<>();
            model.put("usuario", usuario);
            model.put("titulo", "Tu solicitud ha sido aceptada!");
            model.put("url", url + "/proyecto/ver-proyecto/" + proyectoId);
            model.put("tituloBoton", "Ver Proyecto");
            model.put("cuerpo", "Estamos emocionados de notificarte que tu solicitud para unirte a un proyecto, ha sido aceptada. Para ver el proyecto, simplemente presione el botón de abajo. Envía un correo al dueño del proyecto para poder comunicarse.");
            //FIN DE VARIABLES
            mailServicio.sendEmail(usuario, model);
            redirectAttrs
                    .addFlashAttribute("mensaje", "Usuario agregado al proyecto.")
                    .addFlashAttribute("clase", "success");
            return "redirect:/escritorio";
        } catch (MessagingException | IOException | TemplateException ex) {
            Logger.getLogger(ProyectoControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "redirect:/escritorio";
    }

    @GetMapping("/calificar/{idProyecto}")
    public String calificar(Model modelo, @PathVariable String idProyecto, HttpSession session) throws ErrorServicio {
        modelo.addAttribute("titulo", "Proyecto");
        Proyecto proyecto = proyectoServicio.buscarProyectoPorId(idProyecto);
        if (proyecto.getEstadosProyecto().name().equalsIgnoreCase("Finalizado")) {
            return "redirect:/escritorio";
        } else {
            //PRIMERO FINALIZAR EL PROYECTO DE PASO :v
            proyectoServicio.finalizar(proyecto);
            //ENVIO PROYECTO :c
            modelo.addAttribute("proyecto", proyecto);
            //modelo.addAttribute("temas", Temas.values());
            return "proyecto/calificacionPart.html";
        }
    }

    @GetMapping(value = {"/unirme/{proyectoId}/{usuarioId}/{mensaje}", "/unirme/{proyectoId}/{usuarioId}"})
    public String unirmeProyecto(Model modelo, HttpSession session, @PathVariable String proyectoId, @PathVariable String usuarioId,@PathVariable(required = false) String mensaje, RedirectAttributes redirectAttrs) throws ErrorServicio {
        try {
            modelo.addAttribute("titulo", "Proyecto");
            modelo.addAttribute("unirse", "true");
            Map<String, Object> model = proyectoServicio.postulacionProyecto(proyectoId, usuarioId,mensaje);
            redirectAttrs
                    .addFlashAttribute("mensaje", "Se ha enviado la solicitud satisfactoriamente.")
                    .addFlashAttribute("clase", "success");
            mailServicio.sendEmail(proyectoServicio.buscarProyectoPorId(proyectoId).getUsuarioCreador(), model);
            return "redirect:/proyecto/ver-proyecto/" + proyectoId;
        } catch (ErrorServicio | MessagingException | IOException | TemplateException ex) {
            Logger.getLogger(ProyectoControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "redirect:/proyecto/ver-proyecto/" + proyectoId;
    }
    
    @GetMapping("/ver-solicitud/{proyectoId}/{usuarioId}")
    public String vistaSolicitud(Model modelo, HttpSession session, @PathVariable String proyectoId, @PathVariable String usuarioId, RedirectAttributes redirectAttrs) throws ErrorServicio {
        modelo.addAttribute("titulo", "Solicitud");
        List<Postulacion> postulacions=proyectoServicio.postuladosProyectoUsuario(usuarioId, proyectoId);
        if (!postulacions.isEmpty()) {
            Usuario perfil = usuarioServicio.buscarUsuarioPorId(usuarioId);
            modelo.addAttribute("usuario", perfil);
            modelo.addAttribute("mensajeSolicitud", postulacions.get(0).getMensaje());
            modelo.addAttribute("lenguajesEnum", Lenguajes.class);
            modelo.addAttribute("estados", EstadosProyecto.values());
            modelo.addAttribute("proyecto", proyectoServicio.buscarProyectoPorId(proyectoId));
            return "proyecto/solicitud.html";
        }
        redirectAttrs
                .addFlashAttribute("mensaje", "La solicitud ya no está disponible.")
                .addFlashAttribute("clase", "danger");
        return "redirect:/escritorio";
    }
    
    @GetMapping("/misSolicitudes")
    public String misPostulados(Model modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Proyecto");
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("proyectos", proyectoServicio.proyectosPostulados(usuario.getId()));
        modelo.addAttribute("activarItem", "msolicitud");
        return "proyecto/misPostulaciones.html";
    }
    
    @GetMapping("/participantes/{idProyecto}")
    public String participantes(Model modelo, @PathVariable String idProyecto) throws ErrorServicio {
        modelo.addAttribute("titulo", "Proyecto Participantes");
        Proyecto proyecto = proyectoServicio.buscarProyectoPorId(idProyecto);
        modelo.addAttribute("participantes", proyecto.getParticipantes());
        modelo.addAttribute("id", proyecto.getId());
        modelo.addAttribute("creador", proyecto.getUsuarioCreador().getId());
        modelo.addAttribute("lenguajesEnum", Lenguajes.class);
        return "proyecto/participantes";
    }
    
    @GetMapping("/eliminar-participante/{idProyecto}")
    public String eliminarP(@PathVariable String idProyecto, RedirectAttributes redirectAttrs, HttpSession session) {
        try {
            Proyecto proyecto=proyectoServicio.buscarProyectoPorId(idProyecto);
            proyecto.getParticipantes().remove(0);
            proyecto.setCupos(proyecto.getCupos()+1);
            proyectoRepositorio.save(proyecto);
            redirectAttrs
                    .addFlashAttribute("mensaje", "Usuario eliminado correctamente")
                    .addFlashAttribute("clase", "success");
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
        }
        return "redirect:/proyecto/misProyectos";
    }
    
    @GetMapping("/colab/{idUsuario}")
    public String participanteUsuario(Model modelo, HttpSession session, RedirectAttributes redirectAttrs, @PathVariable String idUsuario) {
        try {
            modelo.addAttribute("titulo", "Proyectos");
            modelo.addAttribute("usuario", usuarioServicio.buscarUsuarioPorId(idUsuario));
            modelo.addAttribute("modoPerfil", "true");
            modelo.addAttribute("proyectos", proyectoServicio.proyectosDondeParticipa(idUsuario));
            modelo.addAttribute("temas", Temas.class);
            return "proyecto/proyectosParticipante.html";
        } catch (ErrorServicio ex) {
            redirectAttrs
                    .addFlashAttribute("mensaje", "Error en el id de usuario")
                    .addFlashAttribute("clase", "danger");
            return "redirect:/escritorio";
        }
    }
    
}
