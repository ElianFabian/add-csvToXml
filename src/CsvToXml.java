import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class CsvToXml
{
    //region Variables
    private static final String ficheroCSV = "airlines.csv";
    private static final String ficheroXMLDestino = "Aeropuertos.xml";
    private static List<Aerolinea> aerolineas = new ArrayList<>();
    private static HashMap<String, Integer> paises = new HashMap<>();
    private static HashMap<String, List<Aerolinea>> aerolineasPorPais;
    //endregion

    public static void main(String[] args) throws ParserConfigurationException
    {
        // Se obtienen las aerolíneas y los países
        leerFilasDelCSV(aerolineas, paises);

        aerolineasPorPais = obtenerAerolineasPorPais();

        //--------------------------------------------------------------------------------------------------------------

        // Se crea el documento dónde se guardarán las aerolíneas agrupadas por país
        var docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        
        var doc = docBuilder.newDocument();

        var rootElement = doc.createElement("Paises");
        doc.appendChild(rootElement);

        var escritorXML = new EscritorXML(ficheroXMLDestino, doc);

        //--------------------------------------------------------------------------------------------------------------

        crearDocumentoXML(doc, rootElement, escritorXML);

        //--------------------------------------------------------------------------------------------------------------

        // Se guarda el objeto Documento en el Fichero
        escritorXML.guardarObjetoXMLEnFichero();
        System.out.println("Fichero Guardado!");
    }

    //region Métodos

    private static void leerFilasDelCSV(List<Aerolinea> aerolineas, HashMap<String, Integer> paises)
    {
        // Se lee línea por línea el CSV cogiendo los atributos que nos interesan
        // para añadirlos a un objeto Aerolinea y este a un array de aerolíneas
        var lectorCSV = new LectorCSV(ficheroCSV, ",");

        lectorCSV.leerFilas((c) ->
        {
            var id = Integer.parseInt(c[0]);
            var nombre = c[1];
            var iata = c[2];
            var pais = c[3];
            var activo = c[4];

            var aerolinea = new Aerolinea(id, nombre, iata, pais, activo);
            aerolineas.add(( aerolinea ));

            paises.put(aerolinea.pais, 1);
        });
    }

    private static HashMap<String, List<Aerolinea>> obtenerAerolineasPorPais()
    {
        HashMap<String, List<Aerolinea>> aerolineasPorPais = new HashMap<>();

        // Se rellena el hasmap con listas de aerolíneas vacías
        paises.forEach((pais, valor) ->
        {
            aerolineasPorPais.put(pais, new ArrayList<>());
        });

        // Se rellenan ahora las lista de aerolíneas 
        for (var a : aerolineas)
        {
            aerolineasPorPais.get(a.pais).add(a);
        }

        return aerolineasPorPais;
    }

    private static void crearDocumentoXML(Document doc, Element root, EscritorXML escritor)
    {
        // Recorremos el HashMap de países para ir añadiendo los aeropuertos a cada país
        // y cada país al documento XML
        paises.forEach((p, valor) ->
        {
            // Creamos el nodo Pais que es el que contendrá sus respectivas aerolíneas
            var pais = doc.createElement("Pais");
            pais.setAttribute("pais", p);

            // Cogemos las aerolíneas del país que se esté usando en el bucle
            var aerolineasPorPaisActual = aerolineasPorPais.get(p);

            // Se recorren las aerolíneas introduciendo cada una como objeto XML
            for (var aerolinea : aerolineasPorPaisActual)
            {
                // Se añade el nodo Aeropuerto al nodo Pais que le corresponde
                pais.appendChild(escritor.objetoANodo("Aeropuerto", aerolinea));
            }

            // Se añade el nodo Pais al nodo Paises (el elemento raíz)
            root.appendChild(pais);
        });
    }

    //endregion
}
