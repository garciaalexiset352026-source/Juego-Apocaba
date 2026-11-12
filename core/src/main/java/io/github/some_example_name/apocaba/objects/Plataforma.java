package io.github.some_example_name.apocaba.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Plataforma sobre la que se puede saltar y caminar.
// Por defecto solo se puede pisar desde arriba: desde abajo el jugador la atraviesa.
// Si es sólida (por ejemplo un auto), tampoco se puede atravesar por los costados ni por abajo.
public class Plataforma {

    // La textura es compartida (la crea y destruye Main), no se libera acá
    private final TextureRegion imagen;

    // x, y = esquina inferior izquierda
    private float x;
    private float y;
    private float ancho;
    private float alto;

    // Si es true, el jugador y los zombies chocan con ella por los costados
    private boolean solida = false;

    // Textura estirada para cubrir toda la plataforma
    public Plataforma(Texture textura, float x, float y, float ancho, float alto) {
        this(textura, x, y, ancho, alto, false);
    }

    // Con repetir = true la imagen no se estira: se repite como baldosas
    // (en su tamaño original) hasta cubrir la plataforma.
    public Plataforma(Texture textura, float x, float y, float ancho, float alto, boolean repetir) {

        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;

        imagen = new TextureRegion(textura);

        if (repetir) {
            textura.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            imagen.setRegion(0, 0, (int) ancho, (int) alto);
        }
    }

    // Igual que el anterior pero indicando si es sólida (autos)
    public Plataforma(Texture textura, float x, float y, float ancho, float alto,
                      boolean repetir, boolean solida) {

        this(textura, x, y, ancho, alto, repetir);
        this.solida = solida;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(imagen, x, y, ancho, alto);
    }

    // Altura de la superficie donde se para el jugador
    public float getTecho() {
        return y + alto;
    }

    public boolean isSolida() {
        return solida;
    }

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
}
