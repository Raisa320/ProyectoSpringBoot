package pds.wabbit.Servicios;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import pds.wabbit.Entidades.TokenPassword;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Enumeraciones.Rol;
import pds.wabbit.Repositorios.IUsuarioRepositorio;
import pds.wabbit.errores.ErrorServicio;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private IUsuarioRepositorio usuarioRepositorio;

    @Autowired
    private TokenPasswordServicio secureTokenServicio;

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
        if (!user.getEmail().equals(usuario.getEmail())) {
            usuario.setCuentaVerificada(Boolean.FALSE);
            usuarioRepositorio.save(usuario);
            throw new ErrorServicio("email");
        } else {
            usuario.setCuentaVerificada(Boolean.TRUE);
            return usuarioRepositorio.save(usuario);
        }
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

    public Usuario buscarUsuarioPorMail(String mail) throws ErrorServicio {
        Usuario usuario = usuarioRepositorio.buscarUsuarioPorEmail(mail);
        if (usuario != null) {
            return usuario;
        } else {
            throw new ErrorServicio("No hay usuario activos con este email");
        }
    }

    public String verificarEmail(String mail) {
        Usuario usuario = usuarioRepositorio.buscarUsuarioPorEmail(mail);
        if (usuario != null) {
            if (usuario.isCuentaVerificada()) {
                return "no";
            } else {
                usuarioRepositorio.delete(usuario);
                return "si";
            }
        } else {
            return "si";
        }
    }

    public void cambioPassword(String newPassword, String confirmPassword, String token) throws ErrorServicio {
        TokenPassword tokenPassword = secureTokenServicio.findByToken(token);
        if (Objects.isNull(tokenPassword) || tokenPassword.estaExpirado()) {
            throw new ErrorServicio("Token no valido");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ErrorServicio("claveIncorrecta");
        }
        Usuario usuario = usuarioRepositorio.getById(tokenPassword.getUsuario().getId());
        if (Objects.isNull(usuario)) {
            throw new ErrorServicio("No se puede encontrar el usuario para el token.");
        }
        secureTokenServicio.removeTokenById(tokenPassword.getId());
        String encriptada = new BCryptPasswordEncoder().encode(newPassword);
        usuario.setClave(encriptada);
        usuarioRepositorio.save(usuario);
    }

    public void verificarCuenta(String token) throws ErrorServicio {
        TokenPassword tokenPassword = secureTokenServicio.findByToken(token);
        if (Objects.isNull(tokenPassword) || tokenPassword.estaExpirado()) {
            throw new ErrorServicio("Token no valido");
        }
        Usuario usuario = usuarioRepositorio.getById(tokenPassword.getUsuario().getId());
        if (Objects.isNull(usuario)) {
            throw new ErrorServicio("No se puede encontrar el usuario para el token.");
        }
        secureTokenServicio.removeTokenById(tokenPassword.getId());
        usuario.setCuentaVerificada(Boolean.TRUE);
        usuarioRepositorio.save(usuario);
    }

    public Usuario cambiarPorcentaje(Map<String, Object> params) throws ErrorServicio {
        if (params.containsKey("id")) {
            Usuario usuario = buscarUsuarioPorId(params.get("id").toString());
            if (usuario != null) {
                Map<String, String> lenguajesMap = new HashMap<>();
                params.entrySet().forEach(entry -> {
                    if (!entry.getKey().equals("id")) {
                        lenguajesMap.put(entry.getKey(), entry.getValue().toString());
                    }
                });
                usuario.setLenguajes(lenguajesMap);
                return usuarioRepositorio.save(usuario);
            } else {
                throw new ErrorServicio("Id no existe");
            }
        } else {
            throw new ErrorServicio("Se ha eliminado la clave");
        }
    }

    public Page<Usuario> usuariosActivos(PageRequest pageRequest) {
        return usuarioRepositorio.usuariosActivos(pageRequest);
    }

    public Page<Usuario> usuariosFiltrado(PageRequest pageRequest, Map<String, Object> params) {
        List<Usuario> usuarios = usuarioRepositorio.usuariosActivos();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!entry.getKey().equals("page")) {
                String filtro = entry.getValue().toString();
                System.out.println("FILTRO:" + filtro);
                if (entry.getKey().equals("user")) {
                    filtro = cleanString(params.get("user").toString().toLowerCase());
                }
                usuarios = filtro(usuarios, filtro);
            }
        }
        return listConvertToPage1(usuarios, pageRequest);
    }

    //Esta función toma un string (filtro) y filtra todas las observaciones que no tengan ningun string que contenga este filtro
    public List<Usuario> filtro(List<Usuario> listUbicaciones, String filtro) {
        List<Usuario> copy = new ArrayList<Usuario>(listUbicaciones);
        copy.removeIf(o
                -> (!o.getNombre().toLowerCase().contains(filtro))
                && !o.getApellido().toLowerCase().contains(filtro) && !o.getRol().toString().equals(filtro) && !o.getLenguajes().containsKey(filtro)
        );
        return copy;
    }

    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }

    public static <T> Page<T> listConvertToPage1(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    public String modificarPassword(Map<String, Object> params) throws JSONException, ErrorServicio {
        JSONArray results = new JSONArray();
        JSONObject objetoData = new JSONObject();
        if (!params.containsKey("contrsActual") || !params.containsKey("contraNueva") || !params.containsKey("contraRepetida") || !params.containsKey("idUser")) {
            objetoData.put("status", "error");
            objetoData.put("mensaje", "Todos los campos son obligatorios.");
        } else if (params.get("contrsActual").toString().isEmpty() || params.get("contraNueva").toString().isEmpty() || params.get("contraRepetida").toString().isEmpty() || params.get("idUser").toString().isEmpty()) {
            objetoData.put("status", "error");
            objetoData.put("mensaje", "Todos los campos son obligatorios.");
        } else if (!params.get("contraNueva").toString().equals(params.get("contraRepetida").toString())) {
            objetoData.put("status", "error");
            objetoData.put("mensaje", "Las claves no coinciden.");
        } else {
            Usuario usuario = usuarioRepositorio.getById(params.get("idUser").toString());
            if (Objects.isNull(usuario)) {
                objetoData.put("status", "error");
                objetoData.put("mensaje", "El id del usuario es incorrecto.");
            } else {
                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                if (!bcrypt.matches(params.get("contrsActual").toString(), usuario.getClave())) {
                    objetoData.put("status", "error");
                    objetoData.put("mensaje", "La contraseña actual no es correcta.");
                } else {
                    objetoData.put("status", "ok");
                    objetoData.put("mensaje", "La contraseña ha sido modificada correctamente");
                    String encriptada = new BCryptPasswordEncoder().encode(params.get("contraNueva").toString());
                    usuario.setClave(encriptada);
                    usuarioRepositorio.save(usuario);
                }
            }
        }
        results.put(objetoData);
        return results.toString();
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
            boolean activo = !usuario.isCuentaVerificada(); //false por defecto
            UserDetails user = User.withUsername(usuario.getEmail())
                    .password(usuario.getClave())
                    .disabled(activo)
                    .authorities(permisos).build();
//            User user = new User(usuario.getEmail(), usuario.getClave(), permisos);
            return user;
        } else {
            //Paso un usuario sin permisos para que lo fuerce a loguearse
            List<GrantedAuthority> permisos = new ArrayList<>();
            User user = new User("00000000", "111", permisos);
            return user;
        }
    }
}
