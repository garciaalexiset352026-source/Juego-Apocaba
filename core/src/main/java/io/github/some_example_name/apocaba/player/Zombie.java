package io.github.some_example_name.apocaba.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.apocaba.objects.Plataforma;

import java.util.ArrayList;

public class Zombie {

    private enum Estado { QUIETO, CAMINANDO, ATACANDO }

    // =========================
    // RECURSOS COMPARTIDOS
    // Se cargan una sola vez para todos los zombies
    // =========================

    private static final int ANCHO_CUADRO = 192;
    private static final int ALTO_CUADRO = 128;

    private static Texture texturaIdle;
    private static Texture texturaCaminar;
    private static Texture texturaAtaque;

    private static Animation<TextureRegion> animIdle;
    private static Animation<TextureRegion> animCaminar;
    private static Animation<TextureRegion> animAtaque;

    // Ataque: 14 cuadros. En el cuadro 7 el brazo ya está estirado y ahí pega.
    private static final float DURACION_CUADRO_ATAQUE = 0.07f;
    private static final int CUADROS_ATAQUE = 14;
    private static final int CUADRO_GOLPE = 7;

    // Llamar una vez en Main.create()
    public static void cargarRecursos() {

        texturaIdle = new Texture("Personaje/zombie_idle.png");
        texturaCaminar = new Texture("Personaje/zombie_caminar.png");
        texturaAtaque = new Texture("Personaje/zombie_ataque.png");

        animIdle = new Animation<TextureRegion>(0.12f, cortarHoja("zombie_idle.png", texturaIdle, 12));
        animIdle.setPlayMode(Animation.PlayMode.LOOP);

        animCaminar = new Animation<TextureRegion>(0.06f, cortarHoja("zombie_caminar.png", texturaCaminar, 14));
        animCaminar.setPlayMode(Animation.PlayMode.LOOP);

        animAtaque = new Animation<TextureRegion>(DURACION_CUADRO_ATAQUE,
                cortarHoja("zombie_ataque.png", texturaAtaque, CUADROS_ATAQUE));
        animAtaque.setPlayMode(Animation.PlayMode.NORMAL);
    }

    // Llamar una vez en Main.dispose()
    public static void liberarRecursos() {

        texturaIdle.dispose();
        texturaCaminar.dispose();
        texturaAtaque.dispose();
    }

    private static Array<TextureRegion> cortarHoja(String nombre, Texture hoja, int cantidad) {

        // Aviso en la consola si se puso una imagen equivocada en este lugar
        int anchoEsperado = ANCHO_CUADRO * cantidad;

        if (hoja.getWidth() != anchoEsperado || hoja.getHeight() != ALTO_CUADRO) {

            Gdx.app.error("Zombie", "La imagen " + nombre + " mide " + hoja.getWidth() + "x"
                    + hoja.getHeight() + " y tendria que medir " + anchoEsperado + "x" + ALTO_CUADRO
                    + ". Probablemente es otro archivo.");
        }

        // El ancho de cada cuadro se calcula con el tamaño real de la imagen,
        // así no se cae aunque la hoja mida distinto
        int anchoCuadro = hoja.getWidth() / cantidad;

        TextureRegion[] fila = TextureRegion.split(hoja, anchoCuadro, hoja.getHeight())[0];

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for (int i = 0; i < cantidad; i++) {
            frames.add(fila[i]);
        }

        return frames;
    }


    // =========================
    // DATOS DEL ZOMBIE
    // =========================

    // x = centro del cuerpo, y = pies
    private float x;
    private float y;

    // Cuerpo (para recibir balas)
    private float anchoCuerpo = 50;
    private float altoCuerpo = 115;

    // Comportamiento
    private float velocidad = 120;          // el jugador va a 500, el zombie es lento
    private float rangoVision = 350;        // distancia máxima de detección en cualquier dirección
    private float rangoAtaque = 100;        // empieza a atacar a esta distancia
    private float alcanceGolpe = 110;       // hasta dónde llega el brazo
    private float alturaGolpe = 70;         // si el jugador salta más alto que esto, lo esquiva
    private int danio = 10;

    private float tiempoEntreAtaques = 0.5f;
    private float enfriamiento = 0;

    // Vida
    private int vida = 3;

    // Estado y animación
    private Estado estado = Estado.QUIETO;
    private float tiempoAnimacion = 0;
    private boolean mirandoDerecha = true;
    private boolean golpeAplicado = false;
    private float tiempoDanio = 0;          // para mostrarlo rojo al recibir un balazo


    public Zombie(float x, float y) {

        this.x = x;
        this.y = y;
    }


    // =========================
    // ACTUALIZAR
    // =========================

    public void actualizar(float delta, Jugador jugador, ArrayList<Plataforma> plataformas,
                           float xMinimo, float xMaximo) {

        if (!estaVivo()) {
            return;
        }

        if (enfriamiento > 0) {
            enfriamiento -= delta;
        }

        if (tiempoDanio > 0) {
            tiempoDanio -= delta;
        }

        float dx = jugador.getCentroX() - x;
        float distancia = Math.abs(dx);
        float dy = jugador.getCentroY() - (y + altoCuerpo / 2);

        boolean mismaAltura = Math.abs(jugador.getY() - y) < alturaGolpe;
        boolean enRangoAtaque = distancia <= rangoAtaque && mismaAltura;
        boolean cerca = distancia <= rangoVision && mismaAltura && jugador.estaVivo();

        Estado nuevoEstado;

        if (estado == Estado.ATACANDO) {

            // Un ataque empezado se termina siempre (no se interrumpe)
            nuevoEstado = Estado.ATACANDO;

        } else if (!cerca) {

            nuevoEstado = Estado.QUIETO;

        } else if (enRangoAtaque) {

            // Si ya pegó, espera un momento antes del próximo golpe
            nuevoEstado = (enfriamiento <= 0) ? Estado.ATACANDO : Estado.QUIETO;

        } else {

            nuevoEstado = Estado.CAMINANDO;
        }

        // Mientras ataca no se da vuelta; en los demás casos mira al jugador
        if (nuevoEstado != Estado.ATACANDO && cerca) {
            mirandoDerecha = dx >= 0;
        }

        if (nuevoEstado != estado) {
            estado = nuevoEstado;
            tiempoAnimacion = 0;
            golpeAplicado = false;
        }

        tiempoAnimacion += delta;

        switch (estado) {

            case CAMINANDO:
                Plataforma soporte = buscarSoporte(plataformas);
                if (soporte == null) {
                    break;
                }

                float limiteIzquierdo = Math.max(xMinimo,
                        soporte.getX() + anchoCuerpo / 2);
                float limiteDerecho = Math.min(xMaximo,
                        soporte.getX() + soporte.getAncho() - anchoCuerpo / 2);
                if (limiteIzquierdo > limiteDerecho) {
                    break;
                }

                float nuevaX = MathUtils.clamp(
                        x + (mirandoDerecha ? 1 : -1) * velocidad * delta,
                        limiteIzquierdo, limiteDerecho);

                // Se frena si el jugador o un auto está en el camino
                if (!chocaConJugador(jugador, nuevaX) &&
                    !chocaConSolida(plataformas, nuevaX)) {
                    x = nuevaX;
                }
                break;

            case ATACANDO:
                actualizarAtaque(jugador);
                break;

            default:
                break;
        }
    }

    private Plataforma buscarSoporte(ArrayList<Plataforma> plataformas) {
        Plataforma soporte = null;
        float menorDiferencia = Float.MAX_VALUE;

        for (Plataforma plataforma : plataformas) {
            if (plataforma.isSolida()) {
                continue;
            }

            float diferencia = Math.abs(plataforma.getTecho() - y);
            boolean horizontalmenteCerca =
                    x + anchoCuerpo / 2 > plataforma.getX() &&
                    x - anchoCuerpo / 2 < plataforma.getX() + plataforma.getAncho();

            if (horizontalmenteCerca && diferencia <= 12 && diferencia < menorDiferencia) {
                soporte = plataforma;
                menorDiferencia = diferencia;
            }
        }

        return soporte;
    }

    private void actualizarAtaque(Jugador jugador) {

        int cuadro = (int) (tiempoAnimacion / DURACION_CUADRO_ATAQUE);

        // Golpea una sola vez por ataque, cuando el brazo está estirado
        if (!golpeAplicado && cuadro >= CUADRO_GOLPE) {

            golpeAplicado = true;

            if (golpeaAlJugador(jugador)) {
                jugador.recibirDanio(danio);
            }
        }

        // Terminó la animación: vuelve a decidir qué hacer
        if (cuadro >= CUADROS_ATAQUE) {

            estado = Estado.QUIETO;
            tiempoAnimacion = 0;
            golpeAplicado = false;
            enfriamiento = tiempoEntreAtaques;
        }
    }

    // Zona delante del zombie donde llega el golpe
    private boolean golpeaAlJugador(Jugador jugador) {

        float golpeIzq = mirandoDerecha ? x : x - alcanceGolpe;
        float golpeDer = mirandoDerecha ? x + alcanceGolpe : x;

        // El cuerpo del jugador es más angosto que su imagen de 128 px
        float jugadorIzq = jugador.getCentroX() - 30;
        float jugadorDer = jugador.getCentroX() + 30;

        boolean tocaEnX = jugadorIzq < golpeDer && jugadorDer > golpeIzq;
        boolean tocaEnY = Math.abs(jugador.getY() - y) < alturaGolpe;

        return tocaEnX && tocaEnY;
    }


    // =========================
    // DAÑO
    // =========================

    // ¿El punto (por ejemplo el centro de una bala) está dentro del cuerpo?
    public boolean colisionaConPunto(float px, float py) {

        return px > x - anchoCuerpo / 2 &&
               px < x + anchoCuerpo / 2 &&
               py > y &&
               py < y + altoCuerpo;
    }

    public void recibirDanio(int cantidad) {

        vida -= cantidad;
        tiempoDanio = 0.12f;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAnchoCuerpo() {
        return anchoCuerpo;
    }

    public float getAltoCuerpo() {
        return altoCuerpo;
    }

    // ¿El cuerpo del zombie, si estuviera en 'posX', se superpondría con el del jugador?
    private boolean chocaConJugador(Jugador jugador, float posX) {

        float mitadJugador = jugador.getAnchoCuerpo() / 2;

        boolean solapaEnX =
                posX + anchoCuerpo / 2 > jugador.getCentroX() - mitadJugador &&
                posX - anchoCuerpo / 2 < jugador.getCentroX() + mitadJugador;

        boolean solapaEnY =
                jugador.getY() < y + altoCuerpo &&
                jugador.getY() + jugador.getAlto() > y;

        return solapaEnX && solapaEnY;
    }

    // ¿El cuerpo del zombie, si estuviera en 'posX', se metería dentro de una
    // plataforma sólida (auto)?
    private boolean chocaConSolida(ArrayList<Plataforma> plataformas, float posX) {

        for (Plataforma p : plataformas) {

            if (!p.isSolida()) {
                continue;
            }

            boolean solapaEnX =
                    posX + anchoCuerpo / 2 > p.getX() &&
                    posX - anchoCuerpo / 2 < p.getX() + p.getAncho();

            boolean solapaEnY =
                    y < p.getTecho() &&
                    y + altoCuerpo > p.getY();

            if (solapaEnX && solapaEnY) {
                return true;
            }
        }

        return false;
    }


    // =========================
    // DIBUJAR
    // =========================

    public void dibujar(SpriteBatch batch) {

        TextureRegion frame;

        switch (estado) {

            case CAMINANDO:
                frame = animCaminar.getKeyFrame(tiempoAnimacion);
                break;

            case ATACANDO:
                frame = animAtaque.getKeyFrame(tiempoAnimacion);
                break;

            default:
                frame = animIdle.getKeyFrame(tiempoAnimacion);
                break;
        }

        // Rojo un instante cuando le pega una bala
        if (tiempoDanio > 0) {
            batch.setColor(1f, 0.4f, 0.4f, 1f);
        }

        // Los cuadros están centrados en el cuerpo, así que se dibuja centrado en x.
        // Si mira a la izquierda se espeja con ancho negativo.
        float izquierda = x - ANCHO_CUADRO / 2f;

        if (mirandoDerecha) {
            batch.draw(frame, izquierda, y, ANCHO_CUADRO, ALTO_CUADRO);
        } else {
            batch.draw(frame, izquierda + ANCHO_CUADRO, y, -ANCHO_CUADRO, ALTO_CUADRO);
        }

        batch.setColor(Color.WHITE);
    }
}
