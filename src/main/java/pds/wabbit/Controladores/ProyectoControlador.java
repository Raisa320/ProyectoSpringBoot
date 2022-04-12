package pds.wabbit.Controladores;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Enumeraciones.EstadosProyecto;
import pds.wabbit.Enumeraciones.Lenguajes;
import pds.wabbit.Enumeraciones.Rol;
import pds.wabbit.Enumeraciones.Temas;
import pds.wabbit.Repositorios.IProyectoRepositorio;
import pds.wabbit.Servicios.EmailServicio;
import pds.wabbit.Servicios.ProyectoServicio;
import pds.wabbit.Servicios.UsuarioServicio;
import pds.wabbit.errores.ErrorServicio;

@Controller
@RequestMapping("/proyecto")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class ProyectoControlador {

    @Autowired
    private ProyectoServicio proyectoServicio;

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

    @GetMapping("/misProyectos")
    public String misProyectos(Model modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Proyecto");
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("proyectos", proyectoServicio.proyectosPorUsuario(usuario.getId()));
        modelo.addAttribute("temas", Temas.class);
        modelo.addAttribute("activarItem", "mproyect");
        return "proyecto/misProyectos.html";
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
            return "redirect:/";
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
            return "redirect:/";
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
        return "redirect:/";
    }

    @GetMapping("/filter")
    public String buscarListado(@RequestParam Map<String, Object> params, Model modelo, HttpSession session) {
        if (!params.isEmpty() && params.size() >= 3) {
            if (params.get("nombreP") == "" || params.get("temaUrl") == "" || params.get("listaTema") == "") {
                return "redirect:/";
            }
            Usuario user = (Usuario) session.getAttribute("usuariosession");
            int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
            PageRequest pageRequest = PageRequest.of(page, 12);
            Page<Proyecto> pageProyectos = Page.empty(pageRequest);
            String retorna = "redirect:/";
            if (params.get("listaTema").toString().equals("temas")) {
                pageProyectos = proyectoServicio.filtroTema(params.get("nombreP").toString(),Temas.valueOf(params.get("temaUrl").toString()), pageRequest);
                baseModeloListar("Temas Proyectos", "temas", params.get("temaUrl").toString(), user, modelo);
                modelo.addAttribute("lenguajesEnum", Lenguajes.class);
                modelo.addAttribute("temaTexto", Temas.valueOf(params.get("temaUrl").toString()).getValue());
                retorna = "proyecto/listadoTemas";
            } else if (params.get("listaTema").toString().equals("lenguajes")) {
                pageProyectos = proyectoServicio.filtroLenguaje(params.get("nombreP").toString(),params.get("temaUrl").toString(), pageRequest);
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
        return "redirect:/";
    }

}
