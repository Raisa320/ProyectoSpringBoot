package pds.wabbit.Controladores;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Enumeraciones.EstadosProyecto;
import pds.wabbit.Enumeraciones.Lenguajes;
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

}
