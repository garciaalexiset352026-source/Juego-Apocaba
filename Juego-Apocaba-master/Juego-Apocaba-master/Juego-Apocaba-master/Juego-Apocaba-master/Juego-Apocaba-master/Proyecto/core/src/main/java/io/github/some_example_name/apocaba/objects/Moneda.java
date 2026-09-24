package io.github.some_example_name.apocaba.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Moneda {

    private Texture textura;

    private float x;
    private float y;

    private float ancho = 60;
    private float alto = 60;

    private boolean recogida = false;

    public Moneda(float x, float y) {

        textura = new Texture("moneda-juego-2.png");

        this.x = x;
        this.y = y;
    }

    public void dibujar(SpriteBatch batch) {

        if (!recogida) {
            batch.draw(textura, x, y, ancho, alto);
        }
    }

    public boolean comprobarColision(float jugadorX, float jugadorY,
                                     float jugadorAncho, float jugadorAlto) {

        if (recogida) {
            return false;
        }

        boolean colision =
                jugadorX < x + ancho &&
                jugadorX + jugadorAncho > x &&
                jugadorY < y + alto &&
                jugadorY + jugadorAlto > y;

        if (colision) {
            recogida = true;
            return true;
        }

        return false;
    }

    public boolean estaRecogida() {
        return recogida;
    }

    public void dispose() {
        textura.dispose();
    }
}