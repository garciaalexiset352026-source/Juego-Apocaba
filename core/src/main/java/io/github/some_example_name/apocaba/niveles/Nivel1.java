package io.github.some_example_name.apocaba.niveles;

import com.badlogic.gdx.graphics.Texture;
import io.github.some_example_name.apocaba.objects.Moneda;
import io.github.some_example_name.apocaba.objects.Plataforma;
import io.github.some_example_name.apocaba.player.Zombie;

import java.util.ArrayList;

public class Nivel1 extends Nivel {
    private static final float ANCHO_MUNDO = 1697;
    private static final float ALTO_MUNDO = 927;
    private static final float ANCHO_REFERENCIA = 825;
    private static final float ALTO_REFERENCIA = 450;
    private static final float PISO_PRINCIPAL = alturaMundoDesdeImagen(908);
    private static final float PISO_INTERMEDIO = alturaMundoDesdeImagen(668);
    private static final float PISO_SUPERIOR = alturaMundoDesdeImagen(463);
    private static final float PISO_AZOTEA = alturaMundoDesdeImagen(250);

    public Nivel1(Texture texturaColision) {
        fondo = new Texture("Fondos/Nivel 1 Apocaba.png");
        ancho = ANCHO_MUNDO;
        alto = ALTO_MUNDO;
        zoomCamara = 1f;
        limiteIzquierdo = 0;
        limiteDerecho = ANCHO_MUNDO;
        limiteZombieIzquierdo = 0;
        limiteZombieDerecho = ANCHO_MUNDO;
        inicioJugadorX = 1450;
        inicioJugadorY = PISO_AZOTEA;
        alturaSuelo = PISO_PRINCIPAL;
        crearColisiones(texturaColision);
    }

    private void crearColisiones(Texture textura) {
        agregarBloqueMarcado(textura, 254, 20, 924, 14);
        agregarPlataformaMarcada(textura, 259, 250, 190, 10);
        agregarPlataformaMarcada(textura, 627, 250, 1050, 10);

        agregarPlataformaMarcada(textura, 259, 463, 173, 10);
        agregarPlataformaMarcada(textura, 454, 463, 209, 10);
        agregarPlataformaMarcada(textura, 732, 463, 293, 10);
        agregarPlataformaMarcada(textura, 1167, 463, 163, 10);
        agregarPlataformaMarcada(textura, 1408, 463, 272, 10);

        agregarPlataformaMarcada(textura, 258, 668, 105, 10);
        agregarPlataformaMarcada(textura, 544, 668, 191, 10);
        agregarPlataformaMarcada(textura, 907, 668, 91, 10);
        agregarPlataformaMarcada(textura, 1089, 668, 244, 10);
        agregarPlataformaMarcada(textura, 1471, 668, 209, 10);
        agregarPlataformaMarcada(textura, 14, 908, 1668, 19);

        agregarBloqueMarcado(textura, 254, 20, 34, 723);
        agregarBloqueMarcado(textura, 627, 20, 36, 85);
        agregarBloqueMarcado(textura, 1145, 20, 33, 85);
        agregarBloqueMarcado(textura, 1670, 182, 12, 718);
        agregarBloqueMarcado(textura, 729, 463, 296, 10);
        agregarBloqueMarcado(textura, 1167, 463, 163, 10);
        agregarBloqueMarcado(textura, 544, 668, 191, 10);
        agregarBloqueMarcado(textura, 907, 668, 91, 10);
        agregarBloqueMarcado(textura, 1089, 668, 244, 10);
    }

    private void agregarPlataformaMarcada(Texture textura, float x, float y,
                                          float ancho, float alto) {
        agregarRectanguloMarcado(textura, x, y, ancho, alto, false);
    }

    private void agregarBloqueMarcado(Texture textura, float x, float y,
                                      float ancho, float alto) {
        agregarRectanguloMarcado(textura, x, y, ancho, alto, true);
    }

    private void agregarRectanguloMarcado(Texture textura, float xImagen, float yImagen,
                                          float anchoImagen, float altoImagen, boolean solida) {
        float escalaX = ANCHO_MUNDO / ANCHO_REFERENCIA;
        float escalaY = ALTO_MUNDO / ALTO_REFERENCIA;
        float xReferencia = xImagen * ANCHO_REFERENCIA / ANCHO_MUNDO;
        float yReferencia = yImagen * ALTO_REFERENCIA / ALTO_MUNDO;
        float anchoReferencia = anchoImagen * ANCHO_REFERENCIA / ANCHO_MUNDO;
        float altoReferencia = altoImagen * ALTO_REFERENCIA / ALTO_MUNDO;
        float xMundo = xReferencia * escalaX;
        float yMundo = ALTO_MUNDO - (yReferencia + altoReferencia) * escalaY;
        plataformas.add(new Plataforma(textura, xMundo, yMundo,
                anchoReferencia * escalaX, altoReferencia * escalaY, false, solida));
    }

    private static float alturaMundoDesdeImagen(float yImagen) {
        float yReferencia = yImagen * ALTO_REFERENCIA / ALTO_MUNDO;
        return ALTO_MUNDO - yReferencia * (ALTO_MUNDO / ALTO_REFERENCIA);
    }

    @Override
    public boolean tieneSalida() {
        return true;
    }

    @Override
    public float getSalidaX() {
        return 320;
    }

    @Override
    public float getSalidaY() {
        return PISO_PRINCIPAL;
    }

    @Override
    public ArrayList<Moneda> crearMonedas() {
        ArrayList<Moneda> monedas = new ArrayList<Moneda>();
        monedas.add(new Moneda(500, PISO_PRINCIPAL));
        monedas.add(new Moneda(260, PISO_INTERMEDIO));
        monedas.add(new Moneda(1000, PISO_PRINCIPAL));
        monedas.add(new Moneda(1400, PISO_PRINCIPAL));
        return monedas;
    }

    @Override
    public ArrayList<Zombie> crearZombies() {
        ArrayList<Zombie> zombies = new ArrayList<Zombie>();
        zombies.add(new Zombie(350, PISO_PRINCIPAL));
        zombies.add(new Zombie(1350, PISO_PRINCIPAL));
        zombies.add(new Zombie(350, PISO_INTERMEDIO));
        zombies.add(new Zombie(950, PISO_INTERMEDIO));
        zombies.add(new Zombie(1500, PISO_INTERMEDIO));
        zombies.add(new Zombie(950, PISO_SUPERIOR));
        zombies.add(new Zombie(1450, PISO_SUPERIOR));
        zombies.add(new Zombie(900, PISO_AZOTEA));
        zombies.add(new Zombie(1450, PISO_AZOTEA));
        return zombies;
    }
}
