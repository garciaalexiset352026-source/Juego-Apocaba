package io.github.some_example_name.apocaba.niveles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.apocaba.objects.Moneda;
import io.github.some_example_name.apocaba.objects.Plataforma;
import io.github.some_example_name.apocaba.player.Zombie;

import java.util.ArrayList;

public abstract class Nivel {
    protected final ArrayList<Plataforma> plataformas = new ArrayList<Plataforma>();
    protected Texture fondo;
    protected float ancho;
    protected float alto;
    protected float limiteIzquierdo;
    protected float limiteDerecho;
    protected float limiteZombieIzquierdo;
    protected float limiteZombieDerecho;
    protected float inicioJugadorX;
    protected float inicioJugadorY;
    protected float alturaSuelo;
    protected float zoomCamara = 1.5f;

    public Texture getFondo() {
        return fondo;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    public float getLimiteIzquierdo() {
        return limiteIzquierdo;
    }

    public float getLimiteDerecho() {
        return limiteDerecho;
    }

    public float getLimiteZombieIzquierdo() {
        return limiteZombieIzquierdo;
    }

    public float getLimiteZombieDerecho() {
        return limiteZombieDerecho;
    }

    public float getInicioJugadorX() {
        return inicioJugadorX;
    }

    public float getInicioJugadorY() {
        return inicioJugadorY;
    }

    public float getAlturaSuelo() {
        return alturaSuelo;
    }

    public float getZoomCamara() {
        return zoomCamara;
    }

    public boolean tieneSalida() {
        return false;
    }

    public float getSalidaX() {
        return 0;
    }

    public float getSalidaY() {
        return 0;
    }

    public ArrayList<Plataforma> getPlataformas() {
        return plataformas;
    }

    public abstract ArrayList<Moneda> crearMonedas();

    public abstract ArrayList<Zombie> crearZombies();

    public void dibujarObjetos(SpriteBatch batch) {
    }

    public void dispose() {
        if (fondo != null) {
            fondo.dispose();
        }
    }
}
