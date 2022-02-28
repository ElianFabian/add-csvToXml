import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

/*
Generar Aerolíneas.xml
        * Se trata de generar un xml que tenga las aeorolíneas agrupadas por paises.
        * De un nodo <Pais nombre="Spain"> deben colgar los nodos con los datos de las aerolíneas.
        * Para cada aerolínea del pais (ejemplo):
        * <Aeropuerto>
                <id>512</id>
                <Nombre>Air Uganda International Ltd.</Nombre>
                <IATA>AUX</IATA>
                <activo>AUX<activo>
        * </Aeropuerto>
*/

public class Main
{
    //region Variables
    private static final String ficheroCSV = "airlines.csv";
    private static final String ficheroXMLDestino = "Aeropuertos.xml";
    private static List<Aerolinea> aerolineas = new ArrayList<>();
    private static Set<String> paises = new HashSet<>();
    private static HashMap<String, List<Aerolinea>> aerolineasPorPais;
    //endregion

    public static void main(String[] args) throws ParserConfigurationException
    {
        leerFilasDelCSV(aerolineas, paises);

        aerolineasPorPais = obtenerAerolineasPorPais();


        // Se crea el documento dónde se guardarán las aerolíneas agrupadas por país
        var docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        var doc = docBuilder.newDocument();

        var rootElement = doc.createElement("Paises");
        doc.appendChild(rootElement);


        var escritorXML = new EscritorXML(ficheroXMLDestino, doc);

        crearDocumentoXML(doc, rootElement, escritorXML);

        // Se guarda el objeto en el Fichero
        escritorXML.guardarObjetoXMLEnFichero();
        System.out.println("Fichero Guardado!");
    }

    //region Métodos
    private static void leerFilasDelCSV(List<Aerolinea> aerolineas, Set<String> paises)
    {
        var lectorCSV = new LectorCSV(ficheroCSV, ",");

        lectorCSV.leerFilas((columnas) ->
        {
            var id = Integer.parseInt(columnas[0]);
            var nombre = columnas[1];
            var iata = columnas[4];
            var pais = columnas[6];
            var activo = columnas[7];

            var aerolinea = new Aerolinea(id, nombre, iata, pais, activo);
            aerolineas.add(( aerolinea ));

            paises.add(aerolinea.pais);
        });
    }

    private static HashMap<String, List<Aerolinea>> obtenerAerolineasPorPais()
    {
        HashMap<String, List<Aerolinea>> aerolineasPorPais = new HashMap<>();

        // Se rellena el HashMap con listas de aerolíneas vacías
        paises.forEach(pais ->
        {
            aerolineasPorPais.put(pais, new ArrayList<>());
        });

        // Se rellenan ahora las listas de aerolíneas 
        for (var aerolinea : aerolineas)
        {
            aerolineasPorPais.get(aerolinea.pais).add(aerolinea);
        }

        return aerolineasPorPais;
    }

    private static void crearDocumentoXML(Document doc, Element root, EscritorXML escritor)
    {
        paises.forEach(pais ->
        {
            // Se crea el nodo Pais que es el que contendrá sus respectivas aerolíneas
            var nodoPais = doc.createElement("Pais");
            nodoPais.setAttribute("pais", pais);

            // Se cogen las aerolíneas del país que se esté usando en el bucle
            var aerolineasPorPaisActual = aerolineasPorPais.get(pais);

            // Se recorren las aerolíneas introduciendo cada una como objeto XML
            for (var aerolinea : aerolineasPorPaisActual)
            {
                // Se añade el nodo Aeropuerto al nodo Pais que le corresponde
                nodoPais.appendChild(escritor.objetoANodo("Aeropuerto", aerolinea));
            }

            // Se añade el nodo Pais al nodo Paises (el elemento raíz)
            root.appendChild(nodoPais);
        });
    }
    //endregion
}
