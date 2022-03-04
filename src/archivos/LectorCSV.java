package archivos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class LectorCSV
{
    public String ficheroCSV;
    public String separador = ",";
    private String[] cabecera;
    private HashMap<String, Integer> cabeceraHashMap = new HashMap<>();

    public class Fila
    {
        String[] filaActual;

        public String getString(String columna)
        {
            return filaActual[cabeceraHashMap.get(columna)];
        }

        public int getInt(String columna)
        {
            return Integer.parseInt(getString(columna));
        }

        public float getFloat(String columna)
        {
            return Float.parseFloat(getString(columna));
        }
    }

    //region Constrcutores
    public LectorCSV(String ficheroCSV, String separador)
    {
        this.ficheroCSV = ficheroCSV;
        this.separador = separador;

        obtenerCabeceraDeCSV();
    }
    public LectorCSV(String ficheroCSV)
    {
        this.ficheroCSV = ficheroCSV;
    }
    //endregion

    //region Métodos


    /**
     * Lee cada fila del array.
     * 
     * @param tieneCabecera
     * @param fila
     */
    public void leerFilas(boolean tieneCabecera, Consumer<String[]> fila)
    {
        String linea = "";

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (tieneCabecera) br.readLine(); // Si tiene cabecera se evita leerla
            
            while (( linea = br.readLine() ) != null)
            {
                String[] columnasCSV = linea.split(separador);

                fila.accept(columnasCSV);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Lee cada fila del array, para obtener los valores se hace mediante el nombre de la columna.
     * 
     * @param tieneCabecera
     * @param fila
     */
    public void leerFilasNombradas(boolean tieneCabecera, Consumer<Fila> fila)
    {
        String linea = "";
        Fila filaActual = new Fila();

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (tieneCabecera) br.readLine(); // Si tiene cabecera se evita leerla
            
            while (( linea = br.readLine() ) != null)
            {
                String[] columnasCSV = linea.split(separador);

                filaActual.filaActual = columnasCSV;

                fila.accept(filaActual);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve la primera ocurrencia que cumple la condición indicada.
     */
    public String[] encontrarFila(Function<String[], Boolean> fila)
    {
        String linea = "";

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            while (( linea = br.readLine() ) != null)
            {
                String[] filaActual = linea.split(separador);
                boolean seHaEncontrado = fila.apply(filaActual);

                if (seHaEncontrado) return filaActual;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new String[]{ };
    }

    /**
     * Devuelve todas las ocurrencias que cumplen la condición indicada.
     */
    public List<String[]> encontrarTodasLasFilas(Function<String[], Boolean> fila)
    {
        String linea = "";

        List<String[]> filasEncontradas = new ArrayList<>();

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            while (( linea = br.readLine() ) != null)
            {
                String[] filaActual = linea.split(separador);

                boolean seHaEncontrado = fila.apply(filaActual);

                if (seHaEncontrado) filasEncontradas.add(filaActual);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return filasEncontradas;
    }

    private void obtenerCabeceraDeCSV()
    {
        String linea = "";

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (( linea = br.readLine() ) != null)
            {
                cabecera = linea.split(separador);
                for (int i = 0; i < cabecera.length; i++)
                {
                    cabeceraHashMap.put(cabecera[i], i);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //endregion
}
