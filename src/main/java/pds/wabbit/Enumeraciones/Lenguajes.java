package pds.wabbit.Enumeraciones;


public enum Lenguajes {
    Python("Python","Se trata de un lenguaje de programación multiparadigma, ya que soporta parcialmente la orientación a objetos, programación imperativa y, en menor medida, programación funcional"), 
    Java("Java","Es un lenguaje de programación de propósito general. Para correr depende de una Máquina Virtual de Java (JVM)."), 
    JavaScript("JavaScript","es un lenguaje de programación interpretadot. Se define como orientado a objetos, ​ basado en prototipos, imperativo, débilmente tipado y dinámico."), 
    CSHARP("C#","Es un lenguaje de programación multiparadigma desarrollado y estandarizado por la empresa Microsoft como parte de su plataforma .NET"), 
    PHP("PHP","PHP es un lenguaje de 'scripting' de propósito general y de código abierto que está especialmente pensado para el desarrollo web."), 
    CPLUS("C/C++","C ++ es un lenguaje de programación de propósito general de nivel intermedio basado en C."), 
    R("R","Es un entorno y lenguaje de programación con un enfoque al análisis estadístico"), 
    OBJECTIVE("Objective-C","Es un lenguaje de programación orientado a objetos creado como un superconjunto de C para que implementase un modelo de objetos parecido al de Smalltalk"), 
    SWIFT("Swift","Es un lenguaje de programación multiparadigma creado por Apple enfocado en el desarrollo de aplicaciones para iOS y macOS"), 
    TYPESCRIPT("TypeScript","Es un lenguaje de programación libre y de código abierto desarrollado y mantenido por Microsoft."), 
    Kotlin("Kotlin","Es un lenguaje de programación pragmático pensado para funcionar con Máquina Virtual de Java (JVM) y Android. Además, puede ser compilado a código fuente de Javascript."),
    Go("Go","Es un lenguaje de programación concurrente y compilado inspirado en C, que intenta ser dinámico como Python y con el rendimiento C++."),
    HTML("HTML","Hace referencia al lenguaje de marcado para la elaboración de páginas web"), 
    CSS("CSS","Es un lenguaje de diseño gráfico para definir y crear la presentación de un documento estructurado escrito en un lenguaje de marcado");
    
    private final String value;
    private final String descrip;
    
    Lenguajes(String value,String descrip){
        this.value=value;
        this.descrip=descrip;
    }

    public String getValue() {
        return value;
    }
   
    public String getDescrip() {
        return descrip;
    }
}
