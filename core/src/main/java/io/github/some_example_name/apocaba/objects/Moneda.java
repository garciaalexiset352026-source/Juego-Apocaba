package io.github.some_example_name.apocaba.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Moneda {

    private Texture textura;

    private float x;
    private float y;

    private float ancho = 40;
    private float alto = 40;

    private float tiempo = MathUtils.random(0f, 6f);

    private boolean recogida = false;

    public Moneda(float x, float y) {

        textura = new Texture("moneda-juego-2.png");

        this.x = x;
        this.y = y;
    }

    public void dibujar(SpriteBatch batch) {

        float flotar = MathUtils.sin(tiempo * 4f) * 4f + 4f;

        if (!recogida) {
            batch.draw(textura, x, y + flotar, ancho, alto);
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
    public void actualizar(float delta) {

        tiempo += delta;

    }

    public boolean estaRecogida() {
        return recogida;
    }

    public void dispose() {
        textura.dispose();
    }
}