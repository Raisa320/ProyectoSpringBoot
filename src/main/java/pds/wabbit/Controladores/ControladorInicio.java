package pds.wabbit.Controladores;

import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Enumeraciones.Lenguajes;
import pds.wabbit.Enumeraciones.Nivel;
import pds.wabbit.Enumeraciones.Rol;
import pds.wabbit.Enumeraciones.Sexo;
import pds.wabbit.Enumeraciones.Temas;
import pds.wabbit.Servicios.ProyectoServicio;
import pds.wabbit.Servicios.UsuarioServicio;
import pds.wabbit.errores.ErrorServicio;

@Controller
public class ControladorInicio {
    
     @Autowired
    private ProyectoServicio proyectoServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private HttpSession session;
    
    private Boolean verificarSession(HttpSession session) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            session.invalidate();
            return true;
        }
        return false;
    }

//    @GetMapping("/")
//    public String inicio(ModelMap modelo,  HttpSession session) {
//        try {
//            modelo.addAttribute("titulo", "Wabbi");
//            return "landing.html";   
//        } catch (Exception e) {
//            System.out.println("EXCEPCION:" +e);
//            return "redirect:/logout";
//        }
//        
//    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/")
    public String escritorio(ModelMap modelo,  HttpSession session) {
        try {
            modelo.addAttribute("titulo", "Escritorio");
            modelo.addAttribute("activarItem", "escritorio");
            Usuario perfil = (Usuario) session.getAttribute("usuariosession");
            modelo.addAttribute("usuario", perfil);
            modelo.addAttribute("lenguajesEnum", Lenguajes.class);
            modelo.addAttribute("proyectos", proyectoServicio.projectsRecientes());
            return "dashboard2.html";   
        } catch (Exception e) {
            System.out.println("EXCEPCION:" +e);
            return "redirect:/logout";
        }
        
    }

    @GetMapping("/login")
    public String login(Model modelo, @RequestParam(required = false) String error, @RequestParam(required = false) String logout) {
        modelo.addAttribute("usuario", new Usuario());
        modelo.addAttribute("titulo", "Login");
        if (error != null) {
            modelo.addAttribute("mensaje", " Nombre de usuario o clave incorrectos.");
            modelo.addAttribute("clase", "danger");
        }
        if (logout != null) {
            modelo.addAttribute("mensaje", " Se ha cerrado sesión.");
            modelo.addAttribute("clase", "success");
        }
        return "signin.html";
    }

    @GetMapping("/forgot")
    public String forgot(Model modelo, @RequestParam(required = false) String error) {
        modelo.addAttribute("usuario", new Usuario());
        modelo.addAttribute("titulo", "Contraseña");
        if (error != null) {
            modelo.addAttribute("error", " Correo no registrado en el sistema.");
        }
        return "forgot.html";
    }

    @GetMapping("/registrar")
    public String registrar(Model modelo) {
        modelo.addAttribute("usuario", new Usuario());
        modelo.addAttribute("titulo", "Registrar");
        modelo.addAttribute("temas", Temas.values());
        modelo.addAttribute("sexos", Sexo.values());
        modelo.addAttribute("niveles", Nivel.values());
        modelo.addAttribute("roles", Rol.values());
        modelo.addAttribute("lenguajes", Lenguajes.values());
        return "signup.html";
    }
//
    @PostMapping("/registro")
    public String altaUsuarioPost(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result, ModelMap modelo, RedirectAttributes redirectAttrs, @RequestParam List<String> lenguajes) throws ErrorServicio {
        try {
            Usuario user = usuarioServicio.agregarUsuario(usuario, lenguajes);
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
            return "redirect:/login";
        }
        redirectAttrs
                .addFlashAttribute("mensaje", "Se ha registrado correctamente. Inicie sesión.")
                .addFlashAttribute("clase", "success");
        return "redirect:/login";
    }
    
    @RequestMapping(value = "/verificaEmail/{email}", method = RequestMethod.GET)
    @ResponseBody
    public String verificarEmail(@PathVariable String email) {
        return usuarioServicio.verificarEmail(email);
    }

}
