package io.github.some_example_name.apocaba.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Balas {

    // La textura es compartida (la crea y destruye Main), no se libera aca
    private final Texture textura;

    // Posicion del CENTRO de la bala
    private float x;
    private float y;

    private float velocidadX;
    private float velocidadY;
    private final float velocidad;

    private float ancho = 24;
    private float alto = 6;

    // Angulo en grados (para dibujar la bala "mirando" hacia donde va)
    private float rotacion;

    private final int danio;
    private final float alcance;
    private float distanciaRecorrida = 0;

    private boolean activa = true;

    public Balas(Texture textura,
                 float origenX, float origenY,
                 float objetivoX, float objetivoY,
                 float velocidad, int danio, float alcance) {

        this.textura = textura;
        this.x = origenX;
        this.y = origenY;
        this.velocidad = velocidad;
        this.danio = danio;
        this.alcance = alcance;

        // Direccion normalizada hacia el mouse
        float dx = objetivoX - origenX;
        float dy = objetivoY - origenY;

        float longitud = (float) Math.sqrt(dx * dx + dy * dy);

        if (longitud == 0) {
            dx = 1;
            dy = 0;
            longitud = 1;
        }

        velocidadX = (dx / longitud) * velocidad;
        velocidadY = (dy / longitud) * velocidad;

        rotacion = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
    }

    public void actualizar(float delta) {

        x += velocidadX * delta;
        y += velocidadY * delta;
        distanciaRecorrida += velocidad * delta;

        if (distanciaRecorrida >= alcance ||
            x < 0 || x > 3000 ||
            y < 0 || y > 1000) {

            activa = false;
        }
    }

    public void dibujar(SpriteBatch batch) {

        batch.draw(textura,
                x - ancho / 2, y - alto / 2,   // esquina inferior izquierda
                ancho / 2, alto / 2,           // punto de rotacion (centro)
                ancho, alto,
                1, 1,
                rotacion,
                0, 0,
                textura.getWidth(), textura.getHeight(),
                false, false);
    }

    public boolean estaActiva() {
        return activa;
    }

    // Por si despues queres desactivarla al golpear un zombie
    public void desactivar() {
        activa = false;
    }

    // Para detectar colisiones con enemigos mas adelante
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    public int getDanio() {
        return danio;
    }
}
