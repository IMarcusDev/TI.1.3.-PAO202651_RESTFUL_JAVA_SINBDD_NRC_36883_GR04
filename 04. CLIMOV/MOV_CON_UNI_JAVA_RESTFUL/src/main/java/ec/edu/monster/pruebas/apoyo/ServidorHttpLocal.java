package ec.edu.monster.pruebas.apoyo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ServidorHttpLocal implements AutoCloseable {

    public static class Respuesta {
        final int codigo;
        final String razon;
        final String cuerpo;
        final long demoraMs;

        public Respuesta(int codigo, String razon, String cuerpo, long demoraMs) {
            this.codigo = codigo;
            this.razon = razon;
            this.cuerpo = cuerpo;
            this.demoraMs = demoraMs;
        }
    }

    private final ServerSocket socket;
    private final Map<String, Respuesta> rutas = new HashMap<>();
    private final Thread hilo;

    public volatile String metodo;
    public volatile String ruta;
    public volatile String cuerpo;
    public final Map<String, String> encabezados = new HashMap<>();

    public ServidorHttpLocal() throws IOException {
        socket = new ServerSocket(0, 10, InetAddress.getByName("127.0.0.1"));
        hilo = new Thread(this::atender, "servidor-http-local");
        hilo.setDaemon(true);
        hilo.start();
    }

    public ServidorHttpLocal ruta(String ruta, Respuesta respuesta) {
        rutas.put(ruta, respuesta);
        return this;
    }

    public String urlBase() {
        return "http://127.0.0.1:" + socket.getLocalPort() + "/api/";
    }

    public synchronized String encabezado(String nombre) {
        return encabezados.get(nombre.toLowerCase(Locale.ROOT));
    }

    private void atender() {
        while (!socket.isClosed()) {
            try (Socket conexion = socket.accept()) {
                manejar(conexion);
            } catch (IOException e) {
            }
        }
    }

    private void manejar(Socket conexion) throws IOException {
        InputStream entrada = conexion.getInputStream();
        String[] inicio = linea(entrada).split(" ");
        int largo = 0;
        synchronized (this) {
            encabezados.clear();
            String encabezado;
            while (!(encabezado = linea(entrada)).isEmpty()) {
                int dosPuntos = encabezado.indexOf(':');
                String nombre = encabezado.substring(0, dosPuntos).trim().toLowerCase(Locale.ROOT);
                String valor = encabezado.substring(dosPuntos + 1).trim();
                encabezados.put(nombre, valor);
                if (nombre.equals("content-length")) {
                    largo = Integer.parseInt(valor);
                }
            }
        }
        byte[] datos = new byte[largo];
        int leidos = 0;
        while (leidos < largo) {
            int n = entrada.read(datos, leidos, largo - leidos);
            if (n < 0) {
                break;
            }
            leidos += n;
        }
        metodo = inicio[0];
        ruta = inicio[1];
        cuerpo = new String(datos, 0, leidos, StandardCharsets.UTF_8);

        Respuesta respuesta = rutas.get(ruta);
        if (respuesta == null) {
            respuesta = new Respuesta(404, "Not Found", "", 0);
        }
        if (respuesta.demoraMs > 0) {
            try {
                Thread.sleep(respuesta.demoraMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        byte[] bytes = respuesta.cuerpo.getBytes(StandardCharsets.UTF_8);
        String cabecera = "HTTP/1.1 " + respuesta.codigo + " " + respuesta.razon + "\r\n"
                + "Content-Type: application/json\r\n"
                + "Content-Length: " + bytes.length + "\r\n"
                + "Connection: close\r\n\r\n";
        OutputStream salida = conexion.getOutputStream();
        salida.write(cabecera.getBytes(StandardCharsets.ISO_8859_1));
        salida.write(bytes);
        salida.flush();
    }

    private static String linea(InputStream entrada) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int c;
        while ((c = entrada.read()) != -1 && c != '\n') {
            if (c != '\r') {
                buffer.write(c);
            }
        }
        return buffer.toString(StandardCharsets.ISO_8859_1.name());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
