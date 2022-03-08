package pds.wabbit.Servicios;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Repositorios.IUsuarioRepositorio;
import pds.wabbit.errores.ErrorServicio;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private IUsuarioRepositorio usuarioRepositorio;
    private final static Logger LOGGER = Logger.getLogger("com.soporte.PIT.Servicios.UsuarioServicio");
    
    @Transactional
    public Usuario agregarUsuario(Usuario usuario, List<String> lenguajes) {
        //Encripto clave
        String encriptada = new BCryptPasswordEncoder().encode(usuario.getClave());
        usuario.setClave(encriptada);
        Map<String, String> lenguajesMap = new HashMap<>();

        lenguajes.forEach(leng -> {
            lenguajesMap.put(leng, "100");
        });
        usuario.setLenguajes(lenguajesMap);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario updateUsuario(Usuario usuario, List<String> lenguajes) throws ErrorServicio {
        System.out.println("USUARIO UPDATE:" + usuario);
        Usuario user = buscarUsuarioPorId(usuario.getId());
        Map<String, String> lenguajesMap = user.getLenguajes();
        Map<String, String> lenguajesMap2 = new HashMap<>();

        lenguajes.forEach(leng -> {
            System.out.println("LENGUAJES:" + leng + " ");
            if (lenguajesMap.containsKey(leng)) {
                lenguajesMap2.put(leng, lenguajesMap.get(leng));
            } else {
                lenguajesMap2.put(leng, "100");
            }
        });
        usuario.setLenguajes(lenguajesMap2);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public void darDeBajaUsuario(String usuari) throws ErrorServicio {
        Usuario usuario = buscarUsuarioPorId(usuari);
        usuario.setEliminado(true);
        usuarioRepositorio.save(usuario);
    }

    public Usuario buscarUsuarioPorId(String id) throws ErrorServicio {
        Usuario usuario = usuarioRepositorio.findById(id).get();
        if (usuario != null) {
            return usuario;
        } else {
            throw new ErrorServicio("No hay usuario activos con este nombre de usuario");
        }
    }

    public String verificarEmail(String mail) {
        Usuario usuario = usuarioRepositorio.buscarUsuarioPorEmail(mail);
        if (usuario != null) {
            return "no";
        } else {
            return "si";
        }
    }


    //ES PARA EL LOGIN IDENTIFICARSE NECESITA UN IMPLEMENTS ARRIBA
    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarUsuarioPorEmail(mail);
        System.out.println("USUARIO:" + usuario);
         LOGGER.log(Level.INFO, usuario.toString());
        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
            permisos.add(p1);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);
             LOGGER.log(Level.INFO, "Proceso exitoso");
            User user = new User(usuario.getEmail(), usuario.getClave(), permisos);
            return user;
        } else {
            //Paso un usuario sin permisos para que lo fuerce a loguearse
            List<GrantedAuthority> permisos = new ArrayList<>();
            User user = new User("00000000", "111", permisos);
            return user;
        }
    }
}
