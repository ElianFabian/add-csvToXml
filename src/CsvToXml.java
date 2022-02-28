import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvToXml
{
    public static void main(String[] args) throws IOException
    {
        final String csvFichero = "airlines.csv";
        final String xmlFichero = "Aeropuertos.xml";
        final String csvSeparador = ",";
        String linea = "";

        var br = new BufferedReader(new FileReader(csvFichero)); // Se utiliza para leer cada línea del CSV

        List<Aerolinea> aerolineas = new ArrayList<>();
        HashMap<String, Integer> paises = new HashMap<>();
        HashMap<String, List<Aerolinea>> aerolineasPorPais = new HashMap<>();

        // Se lee línea por línea el CSV cogiendo los atributos que nos interesan
        // para añadirlos a un objeto Aerolinea y este a un array de aerolíneas
        var lectorCSV = new LectorCSV(csvFichero, ",");

        lectorCSV.leerFilas((c) ->
        {
            var aerolinea = new Aerolinea(Integer.parseInt(c[0]), c[1], c[4], c[6], c[7]);
            aerolineas.add((aerolinea));
            paises.put(aerolinea.pais, 1);
        });

        // Se rellena el hasmap pero sin introducir los países aún (es más rápido en teoría)
        paises.forEach((pais, valor) ->
        {
            aerolineasPorPais.put(pais, new ArrayList<>());
        });


        // Se rellena ahora con los países
        for (Aerolinea a : aerolineas)
        {
            aerolineasPorPais.get(a.pais).add(a);
        }

        // Se escribe el hashmap de los paises en el xml
        try
        {
            var docFactory = DocumentBuilderFactory.newInstance();
            var docBuilder = docFactory.newDocumentBuilder();
            var doc = docBuilder.newDocument();

            var gdb = new GeneradorXML(xmlFichero, doc);

            var rootElement = doc.createElement("Paises");
            doc.appendChild(rootElement);

            // Recorremos el HashMap de países para ir añadiendo los aeropuertos a cada país
            // y cada país al documento XML
            paises.forEach((p, valor) ->
            {
                // Creamos el nodo Pais que es el que contendrá sus respectivas aerolíneas
                var pais = doc.createElement("Pais");
                pais.setAttribute("pais", p);

                // Cogemos las aerolíneas del país que se esté usando en el bucle
                List<Aerolinea> listaAerolineasPorPaisActual = aerolineasPorPais.get(p);

                // Se recorren las aerolíneas introduciendo cada una como objeto XML
                for (Aerolinea aerolinea : listaAerolineasPorPaisActual)
                {
                    // Se añade el nodo Aeropuerto al nodo Pais que le corresponde
                    pais.appendChild(gdb.objetoANodo("Aeropuerto", aerolinea));
                }
                // Se añade el nodo Pais al nodo Paises (el elemento raíz)
                rootElement.appendChild(pais);
            });

            // Se guarda el objeto en el Fichero
            gdb.guardarObjetoXMLEnFichero();
            System.out.println("Fichero Guardado!");

            // Se muestra un mensaje acerca de la validación del XML
            if (gdb.esDocumentoValido("Aeropuertos.xsd"))
            {
                System.out.println("Validado!");
            }
            else
            {
                System.out.println("Hay algún problema. Fichero no válido!");
            }
        }
        catch (ParserConfigurationException | TransformerException e)
        {
            e.printStackTrace();
        }
    }
}
