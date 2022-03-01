import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class LectorCSV
{
    String ficheroCSV;
    String separador = ",";

    public LectorCSV(String ficheroCSV, String separador)
    {
        this.ficheroCSV = ficheroCSV;
        this.separador = separador;
    }
    public LectorCSV(String ficheroCSV)
    {
        this.ficheroCSV = ficheroCSV;
    }

    /**
     * Lee cada fila del array.
     *
     * @param fila Es un array con los valores de las columnas de la fila que se está leyendo.
     */
    public void leerFilas(Consumer<String[]> fila)
    {
        BufferedReader br = null;

        try
        {
            String linea = "";
            br = new BufferedReader(new FileReader(ficheroCSV));

            while (( linea = br.readLine() ) != null)
            {
                String[] columnasCSV = linea.split(separador);

                fila.accept(columnasCSV);
            }

            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve la primera ocurrencia que cumple la condición indicada.
     *
     * @param encontrar
     * @return
     */
    public String[] encontrarFila(Function<String[], Boolean> encontrar)
    {
        BufferedReader br = null;
        try
        {
            String linea = "";
            br = new BufferedReader(new FileReader(ficheroCSV));

            while (( linea = br.readLine() ) != null)
            {
                String[] fila = linea.split(separador);

                boolean seHaEncontrado = encontrar.apply(fila);

                if (seHaEncontrado) return fila;
            }

            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        return new String[]{ };
    }

    /**
     * Devuelve todas las ocurrencias que cumplen la condición indicada.
     *
     * @param encontrar
     * @return
     */
    public List<String[]> encontrarTodasLasFilas(Function<String[], Boolean> encontrar)
    {
        BufferedReader br = null;
        List<String[]> filasEncontradas = new ArrayList<>();

        try
        {
            String linea = "";
            br = new BufferedReader(new FileReader(ficheroCSV));

            while (( linea = br.readLine() ) != null)
            {
                String[] fila = linea.split(separador);

                boolean seHaEncontrado = encontrar.apply(fila);

                if (seHaEncontrado) filasEncontradas.add(fila);
            }
            
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return filasEncontradas;
    }
}
