package archivos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class LectorCSV
{
    public String ficheroCSV;
    public Character separador = ',';

    private final boolean tieneCabecera;
    private final HashMap<String, Integer> cabeceraHashMap = new HashMap<>();

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

    //region Constructores
    public LectorCSV(String ficheroCSV, boolean tieneCabecera, Character separador)
    {
        this.ficheroCSV = ficheroCSV;
        this.separador = separador;
        this.tieneCabecera = tieneCabecera;

        if(tieneCabecera) obtenerCabeceraDeCSV();
    }
    public LectorCSV(String ficheroCSV, boolean tieneCabecera)
    {
        this.ficheroCSV = ficheroCSV;
        this.tieneCabecera = tieneCabecera;
        
        if (tieneCabecera) obtenerCabeceraDeCSV();
    }
    //endregion

    //region Métodos


    /**
     * Lee cada fila del array.
     */
    public void leerFilas(Consumer<String[]> fila)
    {
        String linea;

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (tieneCabecera) br.readLine(); // Si tiene cabecera se evita leerla

            while (( linea = br.readLine() ) != null)
            {
                String[] columnasCSV = linea.split(separador.toString());

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
     */
    public void leerFilasNombradas(Consumer<Fila> fila)
    {
        String linea;
        Fila filaActual = new Fila();

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (tieneCabecera) br.readLine(); // Si tiene cabecera se evita leerla

            while (( linea = br.readLine() ) != null)
            {
                String[] columnasCSV = linea.split(separador.toString());

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
    public String[] encontrarFila(Predicate<String[]> fila)
    {
        String linea;

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (tieneCabecera) br.readLine(); // Si tiene cabecera se evita leerla
            
            while (( linea = br.readLine() ) != null)
            {
                String[] filaActual = linea.split(separador.toString());
                
                boolean seHaEncontrado = fila.test(filaActual);
                
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
    public List<String[]> encontrarTodasLasFilas(Predicate<String[]> fila)
    {
        String linea;

        List<String[]> filasEncontradas = new ArrayList<>();

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (tieneCabecera) br.readLine(); // Si tiene cabecera se evita leerla
            
            while (( linea = br.readLine() ) != null)
            {
                String[] filaActual = linea.split(separador.toString());

                boolean seHaEncontrado = fila.test(filaActual);

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
        String linea;

        try (FileReader fr = new FileReader(ficheroCSV);
             BufferedReader br = new BufferedReader(fr)
        )
        {
            if (( linea = br.readLine() ) != null)
            {
                String[] cabecera = linea.split(separador.toString());
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
