package io.github.some_example_name.apocaba.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jugador {

    private Texture textura;
    
    private float ancho = 80;
    private float alto = 100;
    
    private float velocidadSalto = 500;
    private float velocidadVertical = 0;

    private float gravedad = -1200;

    private boolean enSuelo = true;
    private float x;
    private float y;

    private float velocidad = 250;

    public Jugador() {

        textura = new Texture("Personaje/little-man-1.png");

        x = 100;
        y = 100;
    }

    public void actualizar(float delta) {

        // Movimiento hacia la izquierda
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= velocidad * delta;
        }

        // Movimiento hacia la derecha
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += velocidad * delta;
        }

        // Salto
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && enSuelo) {
            velocidadVertical = velocidadSalto;
            enSuelo = false;
        }

        // Gravedad
        velocidadVertical += gravedad * delta;

        // Movimiento vertical
        y += velocidadVertical * delta;

        // Suelo
        if (y <= 100) {
            y = 100;
            velocidadVertical = 0;
            enSuelo = true;
        }
    }

    public void dibujar(SpriteBatch batch) {

        batch.draw(textura, x, y, ancho, alto);
    }

    public void dispose() {

        textura.dispose();
    }
}