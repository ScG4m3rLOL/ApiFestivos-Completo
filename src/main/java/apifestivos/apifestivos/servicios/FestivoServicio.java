package apifestivos.apifestivos.servicios;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import apifestivos.apifestivos.entidades.Festivo;
import apifestivos.apifestivos.interfaces.IntFestivoServicio;
import apifestivos.apifestivos.repositorios.FestivoRepositorio;

@Service
public class FestivoServicio implements IntFestivoServicio {

    @Autowired
    private FestivoRepositorio repositorio;

    private static final int[] M_VALUES = {22, 23, 23, 24, 24, 25};
    private static final int[] N_VALUES = {2, 3, 4, 5, 6, 0};

    private Date obtenerDomingoPascua(int año) {
        int sigloIndex = Math.min(Math.max((año - 1583) / 100, 0), M_VALUES.length - 1);
        int M = M_VALUES[sigloIndex];
        int N = N_VALUES[sigloIndex];

        int A = año % 19;
        int B = año % 4;
        int C = año % 7;
        int D = (19 * A + M) % 30;
        int E = (2 * B + 4 * C + 6 * D + N) % 7;

        int dia = (D + E < 10) ? D + E + 22 : D + E - 9;
        int mes = (D + E < 10) ? 3 : 4;

        if ((dia == 26 && mes == 4) || (dia == 25 && mes == 4 && D == 28 && E == 6 && A > 10)) {
            dia -= 7;
        }

        return new Date(año - 1900, mes - 1, dia);
    }

    private Date agregarDias(Date fecha, int dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DATE, dias);
        return cal.getTime();
    }

    private Date siguienteLunes(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysToAdd = (dayOfWeek == Calendar.SUNDAY) ? 1 : (Calendar.SATURDAY - dayOfWeek + 2) % 7;
        return agregarDias(fecha, daysToAdd);
    }

    private List<Festivo> calcularFestivos(List<Festivo> festivos, int año) {
        if (festivos == null) return null;

        Date pascua = obtenerDomingoPascua(año);

        for (Festivo festivo : festivos) {
            Date fecha;
            switch (festivo.getTipo().getId()) {
                case 1:
                    fecha = new Date(año - 1900, festivo.getMes() - 1, festivo.getDia());
                    break;
                case 2:
                    fecha = siguienteLunes(new Date(año - 1900, festivo.getMes() - 1, festivo.getDia()));
                    break;
                case 3:
                    fecha = agregarDias(pascua, festivo.getDiasPascua());
                    break;
                case 4:
                    fecha = siguienteLunes(agregarDias(pascua, festivo.getDiasPascua()));
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de festivo no soportado");
            }
            festivo.setFecha(fecha);
        }
        return festivos;
    }

    @Override
    public List<Festivo> obtenerFestivos(Integer año) {
        List<Festivo> festivos = repositorio.findAll();
        festivos = calcularFestivos(festivos, año);
        List<Festivo> fechas = new ArrayList<>();
        for (Festivo festivo : festivos) {
            fechas.add(new Festivo(festivo.getFecha(), festivo.getNombre(), festivo.getTipo()));
        }
        return fechas;
    }

    private boolean fechasIguales(Date fecha1, Date fecha2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(fecha1);
        cal2.setTime(fecha2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean esFestivo(List<Festivo> festivos, Date fecha) {
        if (festivos == null) return false;

        festivos = calcularFestivos(festivos, fecha.getYear() + 1900);

        for (Festivo festivo : festivos) {
            if (fechasIguales(festivo.getFecha(), fecha)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean esFestivo(Date fecha) {
        List<Festivo> festivos = repositorio.findAll();
        return esFestivo(festivos, fecha);
    }
}
