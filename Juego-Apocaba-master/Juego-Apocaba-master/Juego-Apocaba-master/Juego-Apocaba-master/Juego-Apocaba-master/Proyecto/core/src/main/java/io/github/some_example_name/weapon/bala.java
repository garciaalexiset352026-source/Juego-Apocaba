package io.github.some_example_name.apocaba.weapon;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bala {
	
    private Texture textura;
    
    private float x;
    private float y;

    private float velocidad = 700;

    private float direccionX;
    private float direccionY;

    private boolean activa = true;

    public Bala(float x, float y, float direccionX, float direccionY) {

        textura = new Texture("libgdx.png");

        this.x = x;
        this.y = y;

        this.direccionX = direccionX;
        this.direccionY = direccionY;
    }

    public void actualizar(float delta) {

        x += direccionX * velocidad * delta;
        y += direccionY * velocidad * delta;
    }

    public void dibujar(SpriteBatch batch) {

        batch.draw(textura, x, y, 10, 10);
    }

    public boolean estaActiva() {
        return activa;
    }

    public void dispose() {
        textura.dispose();
    }
}