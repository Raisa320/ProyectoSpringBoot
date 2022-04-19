package pds.wabbit.Servicios;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import pds.wabbit.Controladores.RecursoControlador;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Entidades.Recurso;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Repositorios.IRecursoRepositorio;
import pds.wabbit.errores.ErrorServicio;

@Service
public class RecursoServicio {
    
    private final Path rootFolder=Paths.get("uploads");
    
    @Autowired
    private IRecursoRepositorio recursoRepositorio;
    
    @Autowired
    private ProyectoServicio proyectoServicio;
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    public void saveFile(MultipartFile  file) throws IOException{
        Files.copy(file.getInputStream(), this.rootFolder.resolve(file.getOriginalFilename()));
    }
    
    public Resource load(String  name) throws MalformedURLException{
        Path file=rootFolder.resolve(name);
        Resource resource=new UrlResource(file.toUri());
        return resource;
    }
    
    public void guadarRecurso(String idP,String idU,String titulo,String urlPag,MultipartFile file) throws IOException, ErrorServicio{
        Recurso recurso=new Recurso();
        Proyecto proyecto=proyectoServicio.buscarProyectoPorId(idP);
        Usuario usuario=usuarioServicio.buscarUsuarioPorId(idU);
        recurso.setTitulo(titulo);
        recurso.setProyecto(proyecto);
        recurso.setUsuario(usuario);
        if (urlPag!=null && !urlPag.isEmpty()) {
            recurso.setUrlWeb(urlPag);
        }
        if (file!=null && !file.isEmpty()) {
           Date hoy = new Date();
           Files.copy(file.getInputStream(), this.rootFolder.resolve(hoy.getTime()+file.getOriginalFilename()));
           recurso.setNameFile(hoy.getTime()+file.getOriginalFilename());
        }
        recursoRepositorio.save(recurso);
    }
    
    public Stream<Path> loadAll() throws IOException{
        return Files.walk(rootFolder, 1).filter(path->!path.equals(rootFolder)).map(rootFolder::relativize);
    }
    
    public List<Recurso> recursosProyecto(String idProyecto){
        return recursoRepositorio.recursosProyecto(idProyecto);
    }
    
}
