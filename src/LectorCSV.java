import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

    void leerFilas(CSVColumnas columnas) throws IOException
    {
        String linea = "";
        var br = new BufferedReader(new FileReader(ficheroCSV)); // Se utiliza para leer cada línea del CSV
        
        while (( linea = br.readLine() ) != null)
        {
            String[] columnasCSV = linea.split(separador);
            
            columnas.set(columnasCSV);
        }
    }
    interface CSVColumnas
    {
        void set(String[] columnas);
    }
}
