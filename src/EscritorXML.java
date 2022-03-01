import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class EscritorXML
{
    //region Atributos

    /**
     * Esta clase se usará cuando un objeto que se vaya a convertir en nodo tenga un campo
     * que sea un objeto, de esta forma la función objetoANodo() convertirá ese campo en un nodo
     * con sus respetivos nodos hijo, en lugar de crear un nodo con un nodo texto (el toString() del objeto).
     */
    static interface ObjetoXML { }

    Document doc;
    String ficheroDestinoXML;
    
    //endregion

    //region Constructores
    public EscritorXML(String ficheroDestinoXML, Document doc)
    {
        this.ficheroDestinoXML = ficheroDestinoXML;
        this.doc = doc;
    }
    //endregion

    //region Métodos

    /**
     * Función que convierte a un objeto en un nodo XML, dónde el objeto sería el nodo padre
     * y sus atributos sus nodos hijos.
     *
     * @param nodoNombre        Nombre del nodo padre.
     * @param objeto            Objeto a usar para convertir en nodo.
     * @param atributosAIgnorar Lista de atributos que no interesan añadir.
     * @param <T>               Tipo genérico.
     * @return Devuelve un objeto de tipo Element.
     */
    public <T> Element objetoANodo(String nodoNombre, T objeto, List<String> atributosAIgnorar)
    {
        Element nodo = doc.createElement(nodoNombre);
        recorrerCampos(objeto, (campo, valor) ->
        {
            if (!atributosAIgnorar.contains(campo.getName()))
            {
                // Si un campo implementa ObjetoXML entonces se convierte ese campo a nodo con sus respectivos atributos como nodos hijo
                if (ObjetoXML.class.isAssignableFrom(campo.getType()))
                {
                    nodo.appendChild(objetoANodo(campo.getName(), valor));
                }
                else
                {
                    Element e = doc.createElement(campo.getName());
                    e.appendChild(doc.createTextNode(valor.toString()));
                    nodo.appendChild(e);
                }
            }
        });
        return nodo;
    }

    public <T> Element objetoANodo(String nodoNombre, T objeto)
    {
        return objetoANodo(nodoNombre, objeto, new ArrayList<>());
    }

    public <T> Element listaObjetosANodos(Element nodoPadre, String nodosHijosNombre, List<T> listaObjetos, List<String> atributosAIgnorar)
    {
        listaObjetos.forEach(objeto ->

                nodoPadre.appendChild(objetoANodo(
                        nodosHijosNombre,
                        objeto,
                        atributosAIgnorar
                ))
        );
        return nodoPadre;
    }
    public <T> Element listaObjetosANodos(Element nodoPadre, String nodosHijosNombre, List<T> listaObjetos)
    {
        return listaObjetosANodos(nodoPadre, nodosHijosNombre, listaObjetos, new ArrayList<>());
    }

    public void guardarObjetoXMLEnFichero()
    {
        try {
            var transformerFactory = TransformerFactory.newInstance();
            var transformer = transformerFactory.newTransformer();
            var source = new DOMSource(doc);
            var result = new StreamResult(new File(ficheroDestinoXML));
            transformer.transform(source, result);
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
        }
    }

    private static <T> void recorrerCampos(T objeto, BiConsumer<Field, Object> campo_valor)
    {
        // https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
        for (Field campo : objeto.getClass().getDeclaredFields())
        {
            if (campo_valor == null) throw new NullPointerException();
            
            // Se ignoran los campos nulos y estáticos
            if (campo == null || java.lang.reflect.Modifier.isStatic(campo.getModifiers())) continue;
            campo.setAccessible(true);

            Object valor = null;
            try
            {
                valor = campo.get(objeto);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            if (valor == null) continue;

            campo_valor.accept(campo, valor);
        }
    }

    public boolean esDocumentoValido(String ficheroXSD)
    {
        try
        {
            var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            var schema = factory.newSchema(new File(ficheroDestinoXML));
            var validator = schema.newValidator();
            validator.validate(new StreamSource(new File(ficheroDestinoXML)));
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
