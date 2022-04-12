package pds.wabbit.Repositorios;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pds.wabbit.Entidades.Proyecto;
import pds.wabbit.Enumeraciones.Temas;

public interface IProyectoRepositorio extends JpaRepository<Proyecto, String> {
    @Query("SELECT COUNT(s) FROM Proyecto s WHERE s.usuarioCreador.id=:idUsuario")
    public Integer cantidadProyectosPorUsuario(@Param("idUsuario") String idUsuario);
    
    @Query("SELECT COUNT(s) FROM Proyecto s WHERE s.tema=:tem AND s.usuarioCreador.id!=:idUsuario")
    public Integer cantidadProyectosPorTema(@Param("tem") Temas tem,@Param("idUsuario") String idUsuario);
    
    @Query("SELECT s FROM Proyecto s WHERE s.usuarioCreador.id=:idUsuario")
    public List<Proyecto> proyectosPorUsuario(@Param("idUsuario") String idUsuario);
    //cuidado
    @Query("SELECT s FROM Proyecto s WHERE s.tema=:temaP")
    public List<Proyecto> proyectosPorTema(@Param("temaP") Temas temaP);
    
    @Query("SELECT s FROM Proyecto s ")
    public List<Proyecto> allProjects();
    
    @Query("SELECT s FROM Proyecto s WHERE s.estadosProyecto!='Finalizado' ORDER BY s.fechaCreacion DESC")
    public List<Proyecto> allRecents();
    
    @Query(nativeQuery = true,value="SELECT proyecto_id FROM proyecto_lenguajes WHERE lenguajes=:leng")
    public List<String> idProyectosPorLenguaje(@Param("leng") String leng);
    
    
}
