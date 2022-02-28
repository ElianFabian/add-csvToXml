public class Aerolinea
{
    Integer id;
    String nombre;
    String IATA;
    String pais;
    String activo;

    public Aerolinea(Integer id, String nombre, String IATA, String pais, String activo)
    {
        this.id = id;
        this.nombre = nombre;
        this.IATA = IATA;
        this.pais = pais;
        this.activo = activo;
    }

    @Override
    public String toString()
    {
        return "Aerolinea{" + "id=" + id + ", nombre='" + nombre + '\'' + ", IATA='" + IATA + '\'' + ", pais='" + pais + '\'' + ", activo='" + activo + '\'' + '}';
    }
}
