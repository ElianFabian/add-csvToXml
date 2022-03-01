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
        // En primer lugar obtenemos las aerolíneas y los países que hay
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

        lectorCSV.leerFilas((fila) ->
        {
            var id = Integer.parseInt(fila[0]);
            var nombre = fila[1];
            var iata = fila[4];
            var pais = fila[6];
            var activo = fila[7];

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
        aerolineas.forEach(aerolinea ->
        {
            aerolineasPorPais.get(aerolinea.pais).add(aerolinea);
        });

        return aerolineasPorPais;
    }

    private static void crearDocumentoXML(Document doc, Element root, EscritorXML escritorXML)
    {
        aerolineasPorPais.forEach((pais, aerolineas) ->
        {
            // Nodo Pais que contendrá sus respectivas aerolíneas
            var nodoPais = doc.createElement("Pais");
            nodoPais.setAttribute("pais", pais);
            
            // Se añaden todas las aerolíneas cómo nodos al nodo País
            nodoPais = escritorXML.listaObjetosANodos(nodoPais, "Aeropuerto", aerolineas);

            // Se añade el nodo Pais al nodo Paises (el elemento raíz)
            root.appendChild(nodoPais);
        });
    }
    //endregion
}
