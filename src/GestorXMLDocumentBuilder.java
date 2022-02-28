import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class GestorXMLDocumentBuilder
{
    Document doc;
    String ficheroXML;

    public GestorXMLDocumentBuilder(String ficheroXML, Document doc) throws ParserConfigurationException
    {
        this.ficheroXML = ficheroXML;
        this.doc = doc;
    }

    //region Métodos

    public Element objectoANodo(String nodoNombre, Aerolinea aerolinea)
    {
        Element nodo = doc.createElement(nodoNombre);

        // https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
        for (Field campo : aerolinea.getClass().getDeclaredFields())
        {
            // La primera condición no afecta al resultado, pero debería ser más rápido
            // La segunda condición es porque no nos interesa almacenar el país dentro de la aerolínea
            if (campo == null || campo.getName().equals("pais")) continue;

            campo.setAccessible(true);
            Object valor = null;
            try
            {
                valor = campo.get(aerolinea);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

            Element e = doc.createElement(campo.getName());
            e.appendChild(doc.createTextNode(valor.toString()));
            nodo.appendChild(e);
        }
        return nodo;
    }

    public void guardarObjetoXMLEnFichero() throws TransformerException
    {
        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();
        var source = new DOMSource(doc);
        var result = new StreamResult(new File(ficheroXML));
        transformer.transform(source, result);
    }
       
    public boolean esDocumentoValido(String ficheroXSD)
    {
        try
        {
            var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            var schema = factory.newSchema(new File(ficheroXML));
            var validator = schema.newValidator();
            validator.validate(new StreamSource(new File(ficheroXML)));
        }
        catch (IOException | SAXException e)
        {
            System.out.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    //endregion
}
