package pds.wabbit.Controladores;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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

/**
 *
 * @author Asus
 */
@Controller
@RequestMapping("/usuario")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ProyectoServicio proyectoServicio;

    @Autowired
    private EmailServicio mailServicio;

    @Autowired
    private TokenPasswordServicio secureTokenServicio;

    @Value("${my.property.baseUrl}")
    private String url;

    @GetMapping(value = {"/perfil", "/perfil/{idUser}"})
    public String perfil(Model modelo, HttpSession session, @PathVariable(required = false) String idUser) throws ErrorServicio {
        Usuario perfil;
        if (idUser != null) {
            perfil = usuarioServicio.buscarUsuarioPorId(idUser);
        } else {
            perfil = (Usuario) session.getAttribute("usuariosession");
        }
        modelo.addAttribute("temasEnum", Temas.class);
        modelo.addAttribute("lenguajesEnum", Lenguajes.class);
        modelo.addAttribute("proyectos", proyectoServicio.cantidadProyectosPorUsuario(perfil.getId()));
        // Integer foros = foroServicio.cantidadPreguntasPorUsuario(perfil.getId()) + respuestaServicio.cantidadRespuestasPorUsuario(perfil.getId());
        // modelo.addAttribute("foros", foros);
        modelo.addAttribute("foros", 0);
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("titulo", "Perfil");
        modelo.addAttribute("activarItem", "perfil");
        return "usuario/profile.html";
    }

    @GetMapping(value = {"/editar"})
    public String editarUsuario(Model modelo, HttpSession session) throws ErrorServicio {
        Usuario perfil = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("titulo", "Editar");
        modelo.addAttribute("temas", Temas.values());
        modelo.addAttribute("sexos", Sexo.values());
        modelo.addAttribute("niveles", Nivel.values());
        modelo.addAttribute("roles", Rol.values());
        modelo.addAttribute("lenguajes", Lenguajes.values());
        modelo.addAttribute("activarItem", "perfil");
        return "usuario/editar.html";
    }

    @PostMapping("/edit")
    public String altaUsuarioPost(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result, ModelMap modelo, RedirectAttributes redirectAttrs, @RequestParam List<String> lenguajes, HttpSession session) throws ErrorServicio, MessagingException, IOException, TemplateException {
        try {
            Usuario user = usuarioServicio.updateUsuario(usuario, lenguajes);
            session.setAttribute("usuariosession", user);

        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            if (e.getMessage().equals("email")) {
                TokenPassword secureToken = secureTokenServicio.crearSecureToken(usuarioServicio.buscarUsuarioPorMail(usuario.getEmail()));
                modelo.put("titulo", "Verificación de correo");
                modelo.put("url", url + "/login?tk=" + secureToken.getToken());
                modelo.put("titulo", "Verificación de correo");
                modelo.put("tituloBoton", "Verificar cuenta");
                modelo.put("cuerpo", "Usted ha recibido este email debido a que se ha solicitado su registro en la app Wabbi. ");
                //FIN DE VARIABLES
                mailServicio.sendEmail(usuarioServicio.buscarUsuarioPorMail(usuario.getEmail()), modelo);
                return "redirect:/login?logout";
            }
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
            return "redirect:/usuario/editar";
        }
        redirectAttrs
                .addFlashAttribute("mensaje", "Datos de Usuario Actualizados")
                .addFlashAttribute("clase", "success");
        return "redirect:/usuario/perfil";
    }

    @GetMapping("/lenguajes")
    public String lenguajesPorcentaje(Model modelo, HttpSession session) {
        modelo.addAttribute("titulo", "Editar");
        Usuario perfil = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", perfil);
        modelo.addAttribute("lenguajesEnum", Lenguajes.class);
        modelo.addAttribute("activarItem", "perfil");
        return "usuario/lenguajes.html";
    }

    @PostMapping("/leng")
    public String cambiarLenguaje(RedirectAttributes redirectAttrs, @RequestParam Map<String, Object> params, HttpSession session) {
        try {
            Usuario user = usuarioServicio.cambiarPorcentaje(params);
            session.setAttribute("usuariosession", user);
        } catch (Exception e) {
            System.out.println("ERROR:" + e);
            redirectAttrs
                    .addFlashAttribute("mensaje", e.getMessage())
                    .addFlashAttribute("clase", "danger");
            return "redirect:/usuario/lenguajes";
        }
        redirectAttrs
                .addFlashAttribute("mensaje", "Porcentajes actualizados correctamente")
                .addFlashAttribute("clase", "success");
        return "redirect:/usuario/perfil";
    }

    public Model baseModeloListar(Model modelo) {
        modelo.addAttribute("titulo", "Usuarios");
        modelo.addAttribute("activarItem", "grupos");
        modelo.addAttribute("lenguajesEnum", Lenguajes.class);
        modelo.addAttribute("rolEnum", Rol.class);
        modelo.addAttribute("roles", Rol.values());
        modelo.addAttribute("lenguajes", Lenguajes.values());
        return modelo;
    }

    @GetMapping("/all")
    public String usuariosAll(@RequestParam Map<String, Object> params, Model modelo) {
        baseModeloListar(modelo);
        int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        PageRequest pageRequest = PageRequest.of(page, 12);
        Page<Usuario> pageUsuario = usuarioServicio.usuariosActivos(pageRequest);
        modelo.addAttribute("total", pageUsuario.getTotalElements());
        int totalPage = pageUsuario.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            modelo.addAttribute("pages", pages);
        }
        modelo.addAttribute("list", pageUsuario.getContent());
        modelo.addAttribute("mostrando", pageUsuario.getContent().size());
        modelo.addAttribute("current", page + 1);
        modelo.addAttribute("next", page + 2);
        modelo.addAttribute("prev", page);
        modelo.addAttribute("last", totalPage);
        return "usuario/usuarios.html";
    }

    @PostMapping("/buscar")
    public String buscarUsuario(@RequestParam Map<String, Object> params, Model modelo) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        if (params.get("user") == "" && params.get("lenguajes") == "" && params.get("roles") == "") {
            return "redirect:/usuario/all";
        }

        return "redirect:/usuario/all";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @RequestMapping(value = "/edit-psw", method = RequestMethod.POST)
    @ResponseBody
    public String agregarObsevacionAjax(@RequestParam Map<String, Object> params) throws JSONException, ErrorServicio {

        return usuarioServicio.modificarPassword(params);
    }

}
