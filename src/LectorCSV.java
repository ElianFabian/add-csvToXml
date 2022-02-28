import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

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
     * @param columnas Es un array con los valores de las columnas de la fila que se está leyendo.
     */
    public void leerFilas(Consumer<String[]> columnas)
    {
        try
        {
            String linea = "";
            var br = new BufferedReader(new FileReader(ficheroCSV));

            while (( linea = br.readLine() ) != null)
            {
                String[] columnasCSV = linea.split(separador);

                columnas.accept(columnasCSV);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
