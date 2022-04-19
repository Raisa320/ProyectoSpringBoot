package pds.wabbit.Controladores;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import pds.wabbit.Entidades.Recurso;
import pds.wabbit.Entidades.ResponseJson;
import pds.wabbit.Servicios.RecursoServicio;
import pds.wabbit.errores.ErrorServicio;

@RestController
@RequestMapping("/recurso")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class RecursoControlador {

    @Autowired
    private RecursoServicio recursoServicio;

    @PostMapping("/upload")
    public ResponseEntity<ResponseJson> uploadFiles(@RequestParam("file") MultipartFile file) throws IOException {
        recursoServicio.saveFile(file);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseJson("Todo correcto"));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws MalformedURLException {
        Resource resource = recursoServicio.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/archivos")
    public ResponseEntity<List<Recurso>> getAllFiles() throws IOException {
        List<Recurso> recursos = recursoServicio.loadAll().map(path -> {
            String fileName = path.getFileName().toString();
            String url = MvcUriComponentsBuilder.fromMethodName(RecursoControlador.class, "getFile", path.getFileName().toString()).build().toString();
            Recurso recurso = new Recurso();
            //recurso.setUrlFile(url);
            recurso.setNameFile(fileName);
            return recurso;
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(recursos);
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseJson> crearRecurso(@RequestParam("idP") String idP,@RequestParam("idU") String idU,@RequestParam("titulo") String titulo,@RequestParam(name="urlPag",required = false) String urlPag,@RequestParam(name="file",required = false) MultipartFile file) throws IOException, ErrorServicio {
        recursoServicio.guadarRecurso(idP,idU,titulo, urlPag, file);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseJson("Todo correcto"));
    }
}
