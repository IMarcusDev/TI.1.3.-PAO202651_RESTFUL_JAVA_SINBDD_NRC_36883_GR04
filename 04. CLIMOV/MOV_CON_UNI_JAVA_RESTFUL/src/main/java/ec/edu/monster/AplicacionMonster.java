package ec.edu.monster;

import android.app.Application;
import android.content.Context;
import ec.edu.monster.config.RestConfig;
import ec.edu.monster.controlador.CatalogoControlador;
import ec.edu.monster.controlador.ConversionControlador;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelos.interfaces.CatalogoRepositorio;
import ec.edu.monster.modelos.interfaces.ConversionRepositorio;
import ec.edu.monster.modelos.interfaces.LoginRepositorio;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.modelos.repositorios.CatalogoRestRepositorio;
import ec.edu.monster.modelos.repositorios.ConversionRestRepositorio;
import ec.edu.monster.modelos.repositorios.LoginRestRepositorio;
import ec.edu.monster.servicios.ClienteRest;
import ec.edu.monster.servicios.EjecutorTareas;
import ec.edu.monster.servicios.RedAndroid;
import ec.edu.monster.servicios.SesionServicio;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AplicacionMonster extends Application {

    private SesionServicio sesion;
    private LoginRepositorio loginRepositorio;
    private CatalogoRepositorio catalogoRepositorio;
    private ConversionRepositorio conversionRepositorio;
    private ExecutorService hilos;
    private EjecutorTareas tareas;

    public static AplicacionMonster de(Context contexto) {
        return (AplicacionMonster) contexto.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sesion = new SesionServicio();

        ClienteRest cliente = new ClienteRest(RestConfig.URL_BASE);
        VerificadorRed red = new RedAndroid(this);
        loginRepositorio = new LoginRestRepositorio(cliente, red);
        catalogoRepositorio = new CatalogoRestRepositorio(cliente, red);
        conversionRepositorio = new ConversionRestRepositorio(cliente, red);

        hilos = Executors.newCachedThreadPool();
        tareas = new EjecutorTareas(hilos);
    }

    public EjecutorTareas getTareas() {
        return tareas;
    }

    public LoginControlador crearLoginControlador() {
        return new LoginControlador(loginRepositorio, sesion);
    }

    public ConversionControlador crearConversionControlador() {
        return new ConversionControlador(conversionRepositorio, catalogoRepositorio, sesion);
    }

    public CatalogoControlador crearCatalogoControlador() {
        return new CatalogoControlador(catalogoRepositorio, sesion, hilos);
    }
}
