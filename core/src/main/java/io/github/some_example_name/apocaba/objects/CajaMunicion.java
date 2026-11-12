package io.github.some_example_name.apocaba.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

// Caja de balas que suelta un zombie al morir
public class CajaMunicion {

    // La textura es compartida (la crea y destruye Main), no se libera acá
    private final Texture textura;

    // x, y = esquina inferior izquierda (apoyada en el piso)
    private float x;
    private float y;

    private float ancho = 40;
    private float alto = 30;

    // Cuántas balas da al recogerla
    private int cantidad;

    // Para que flote suavemente y se note que se puede agarrar
    private float tiempo = MathUtils.random(0f, 6f);

    // Si no la recoge nadie, desaparece
    private float tiempoVida = 0;
    private float tiempoVidaMaximo = 15f;

    private boolean activa = true;

    public CajaMunicion(Texture textura, float x, float y, int cantidad) {

        this.textura = textura;
        this.x = x;
        this.y = y;
        this.cantidad = cantidad;
    }

    public void actualizar(float delta) {

        tiempo += delta;
        tiempoVida += delta;

        if (tiempoVida >= tiempoVidaMaximo) {
            activa = false;
        }
    }

    public void dibujar(SpriteBatch batch) {

        if (!activa) {
            return;
        }

        // Los últimos 3 segundos parpadea antes de desaparecer
        if (tiempoVida > tiempoVidaMaximo - 3f && ((int) (tiempoVida * 8)) % 2 == 0) {
            return;
        }

        float flotar = MathUtils.sin(tiempo * 4f) * 4f + 4f;

        batch.draw(textura, x, y + flotar, ancho, alto);
    }

    // Devuelve true si el jugador la recogió. Si ya tiene la munición llena
    // no la agarra, así no se desperdicia.
    public boolean comprobarColision(float jugadorX, float jugadorY,
                                     float jugadorAncho, float jugadorAlto,
                                     boolean municionLlena) {

        if (!activa || municionLlena) {
            return false;
        }

        boolean colision =
                jugadorX < x + ancho &&
                jugadorX + jugadorAncho > x &&
                jugadorY < y + alto &&
                jugadorY + jugadorAlto > y;

        if (colision) {
            activa = false;
            return true;
        }

        return false;
    }

    public int getCantidad() {
        return cantidad;
    }

    public boolean estaActiva() {
        return activa;
    }
}
