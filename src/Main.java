import archivos.EscritorXML;
import archivos.LectorCSV;
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
    private static HashMap<String, List<Aerolinea>> aerolineasPorPais = new HashMap<>();
    //endregion

    public static void main(String[] args) throws ParserConfigurationException
    {
        // En primer lugar obtenemos las aerolíneas y los países que hay
        leerFilasDelCSV(aerolineas, aerolineasPorPais);

        obtenerAerolineasPorPais(aerolineasPorPais);

        //region Se crea el documento dónde se guardarán las aerolíneas agrupadas por país
        var docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        var doc = docBuilder.newDocument();

        var rootElement = doc.createElement("Paises");
        doc.appendChild(rootElement);
        //endregion

        var escritorXML = new EscritorXML(ficheroXMLDestino, doc);

        crearDocumentoXML(doc, rootElement, escritorXML, aerolineasPorPais);

        // Se guarda el objeto en el Fichero
        escritorXML.guardarObjetoXMLEnFichero();
        System.out.println("Fichero Guardado!");
    }

    //region Métodos
    private static void leerFilasDelCSV(List<Aerolinea> aerolineas, HashMap<String, List<Aerolinea>> aerolineasPorPais)
    {
        var lectorCSV = new LectorCSV(ficheroCSV, false, ',');

        lectorCSV.leerFilas(fila ->
        {
            var id = Integer.parseInt(fila[0]);
            var nombre = fila[1];
            var iata = fila[4];
            var pais = fila[6];
            var activo = fila[7];

            var aerolinea = new Aerolinea(id, nombre, iata, pais, activo);

            aerolineas.add(aerolinea);
            aerolineasPorPais.put(pais, new ArrayList<>());
        });
    }

    private static void obtenerAerolineasPorPais(HashMap<String, List<Aerolinea>> aerolineasPorPais)
    {
        // Se rellenan ahora las listas de aerolíneas 
        aerolineas.forEach(aerolinea ->
        {
            aerolineasPorPais.get(aerolinea.pais).add(aerolinea);
        });
    }

    private static void crearDocumentoXML(Document doc, Element root, EscritorXML escritorXML, HashMap<String, List<Aerolinea>> aerolineasPorPais)
    {
        // Dado que las aerolíneas están agrupadas por país no interesa que cada aerolínea tenga un nodo con su país
        var atributosAIgnorar = Set.of("pais");

        aerolineasPorPais.forEach((pais, aerolineas) ->
        {
            // Nodo Pais que contendrá sus respectivas aerolíneas
            var nodoPais = doc.createElement("Pais");
            nodoPais.setAttribute("nombre", pais);

            // Se añaden todas las aerolíneas cómo nodos al nodo País
            nodoPais = escritorXML.listaObjetosANodos(nodoPais, "Aeropuerto", aerolineas, atributosAIgnorar);

            // Se añade el nodo Pais al nodo Paises (el elemento raíz)
            root.appendChild(nodoPais);
        });
    }
    //endregion
}
