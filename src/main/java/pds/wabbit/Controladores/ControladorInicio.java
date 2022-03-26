package pds.wabbit.Controladores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
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
import pds.wabbit.Entidades.TokenPassword;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Enumeraciones.Lenguajes;
import pds.wabbit.Enumeraciones.Nivel;
import pds.wabbit.Enumeraciones.Rol;
import pds.wabbit.Enumeraciones.Sexo;
import pds.wabbit.Enumeraciones.Temas;
import pds.wabbit.Servicios.EmailServicio;
import pds.wabbit.Servicios.ProyectoServicio;
import pds.wabbit.Servicios.TokenPasswordServicio;
import pds.wabbit.Servicios.UsuarioServicio;
import pds.wabbit.errores.ErrorServicio;

@Controller
public class ControladorInicio {

    @Autowired
    private ProyectoServicio proyectoServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private EmailServicio mailServicio;

    @Autowired
    private HttpSession session;

    @Autowired
    private TokenPasswordServicio secureTokenServicio;

    @Value("${my.property.baseUrl}")
    private String url;

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
    public String escritorio(ModelMap modelo, HttpSession session) {
        try {
            modelo.addAttribute("titulo", "Escritorio");
            modelo.addAttribute("activarItem", "escritorio");
            Usuario perfil = (Usuario) session.getAttribute("usuariosession");
            modelo.addAttribute("usuario", perfil);
            modelo.addAttribute("lenguajesEnum", Lenguajes.class);
            modelo.addAttribute("proyectos", proyectoServicio.projectsRecientes());
            return "dashboard2.html";
        } catch (Exception e) {
            System.out.println("EXCEPCION:" + e);
            return "redirect:/logout";
        }

    }

    @GetMapping("/login")
    public String login(Model modelo, @RequestParam(required = false) String error, @RequestParam(required = false) String logout,
            @RequestParam(required = false) String exito, @RequestParam(required = false) String tk) {
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

        if (exito != null) {
            modelo.addAttribute("mensaje", " Se ha enviado un correo electrónico para la recuperación de su contraseña.");
            modelo.addAttribute("clase", "success");
        }
        if (tk != null) {
            if (!secureTokenServicio.tokenValido(tk)) {
                modelo.addAttribute("mensaje", "El tiempo de espera para realizar el cambio ya ha expirado");
                modelo.addAttribute("clase", "danger");
            } else {
                try {
                    usuarioServicio.verificarCuenta(tk);
                    modelo.addAttribute("mensaje", " Se ha verificado con éxito el usuario, puede iniciar sesión.");
                    modelo.addAttribute("clase", "success");
                } catch (ErrorServicio e) {
                    modelo.addAttribute("mensaje", e.getMessage());
                    modelo.addAttribute("clase", "danger");
                }

            }
        }
        return "signin.html";
    }

    @GetMapping("/forgot")
    public String forgot(Model modelo, @RequestParam(required = false) String error) {
        modelo.addAttribute("titulo", "Contraseña");
        if (error != null) {
            modelo.addAttribute("mensaje", " Correo no registrado en el sistema.");
            modelo.addAttribute("clase", "danger");
        }

        return "forgot.html";
    }

    @PostMapping("/forgot-post")
    public String forgotPost(Model modelo, @RequestParam String email) throws ErrorServicio {
        if (usuarioServicio.verificarEmail(email).equals("no")) {
            TokenPassword secureToken = secureTokenServicio.crearSecureToken(usuarioServicio.buscarUsuarioPorMail(email));
            Map<String, Object> model = new HashMap<>();
            try {

                model.put("titulo", "Recuperación de contraseña");
                model.put("url", url + "/cambiar-password/" + secureToken.getToken());
                model.put("titulo", "Recuperación de contraseña");
                model.put("tituloBoton", "Cambiar Password");
                model.put("cuerpo", "Usted ha recibido este email debido a que se ha solicitado el cambio de password para su cuenta. ");
                //FIN DE VARIABLES
                mailServicio.sendEmail(usuarioServicio.buscarUsuarioPorMail(email), model);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "redirect:/login?exito";
        } else {
            return "redirect:/forgot?error";
        }
    }

    @GetMapping(value = {"/cambiar-password/{token}", "/cambiar-password"})
    public String changePassword(@PathVariable(required = false) String token, final RedirectAttributes redirAttr, final Model model) {
        model.addAttribute("titulo", "Recuperación de contraseña");
        if (!StringUtils.hasLength(token)) {
            redirAttr
                    .addFlashAttribute("mensaje", "El token no se reconoce. Por favor asegúrese de copiar la URL completa.")
                    .addFlashAttribute("clase", "danger");
            return "redirect:/login";
        }

        if (!secureTokenServicio.tokenValido(token)) {
            redirAttr
                    .addFlashAttribute("mensaje", "El tiempo de espera para realizar el cambio ya ha expirado")
                    .addFlashAttribute("clase", "danger");
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        return "form_password.html";
    }

    @PostMapping("/change-post")
    public String changePass(@RequestParam String newPassword, @RequestParam String confirmPassword, @RequestParam String token, RedirectAttributes redirAttr) {
        try {
            usuarioServicio.cambioPassword(newPassword, confirmPassword, token);
        } catch (ErrorServicio e) {
            if (e.getMessage().equals("claveIncorrecta")) {
                redirAttr
                        .addFlashAttribute("mensaje", "Las claves ingresadas no coinciden.")
                        .addFlashAttribute("clase", "danger");
                return "redirect:/cambiar-password/" + token;
            } else {
                redirAttr
                        .addFlashAttribute("mensaje", e.getMessage())
                        .addFlashAttribute("clase", "danger");
                return "redirect:/login";
            }
        }
        redirAttr
                .addFlashAttribute("mensaje", "Su password ha sido modificado con éxito.")
                .addFlashAttribute("clase", "success");
        return "redirect:/login";
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
            TokenPassword secureToken = secureTokenServicio.crearSecureToken(usuarioServicio.buscarUsuarioPorMail(user.getEmail()));
            modelo.put("titulo", "Verificación de correo");
            modelo.put("url", url + "/login?tk=" + secureToken.getToken());
            modelo.put("titulo", "Verificación de correo");
            modelo.put("tituloBoton", "Verificar cuenta");
            modelo.put("cuerpo", "Usted ha recibido este email debido a que se ha solicitado su registro en la app Wabbi. ");
            //FIN DE VARIABLES
            mailServicio.sendEmail(usuarioServicio.buscarUsuarioPorMail(user.getEmail()), modelo);
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
            return "redirect:/login";
        }
        redirectAttrs
                .addFlashAttribute("mensaje", "Verifique su email para iniciar sesión")
                .addFlashAttribute("clase", "warning");
        return "redirect:/login";
    }

    @RequestMapping(value = "/verificaEmail/{email}", method = RequestMethod.GET)
    @ResponseBody
    public String verificarEmail(@PathVariable String email) {
        return usuarioServicio.verificarEmail(email);
    }

}
