package apifestivos.apifestivos.interfaces;

import java.util.Date;
import java.util.List;
import apifestivos.apifestivos.entidades.Festivo;

public interface IntFestivoServicio {
    public List<Festivo> obtenerFestivos(Integer año);
    public boolean esFestivo(Date Fecha);
}
