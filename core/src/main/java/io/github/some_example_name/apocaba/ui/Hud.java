package io.github.some_example_name.apocaba.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.apocaba.player.Jugador;
import io.github.some_example_name.apocaba.weapons.Arma;

public class Hud {

    private static final Color BORDE = new Color(0.05f, 0.05f, 0.05f, 1f);
    private static final Color FONDO = new Color(0.18f, 0.18f, 0.2f, 0.9f);
    private static final Color VERDE = new Color(0.2f, 0.8f, 0.25f, 1f);

    // Textura de 1x1 pixel blanco: se estira y se tiñe para dibujar rectángulos
    private Texture pixel;
    private BitmapFont fuente;
    private GlyphLayout layout = new GlyphLayout();

    // Posición y tamaño de los elementos (pantalla de 1280 x 720)
    private float barraX = 20;
    private float barraY = 670;
    private float barraAncho = 300;
    private float barraAlto = 30;

    private float cajaY = 612;
    private float cajaAlto = 40;
    private float cajaBalasX = 20;
    private float cajaBalasAncho = 230;
    private float cajaMonedasX = 265;
    private float cajaMonedasAncho = 170;

    public Hud() {

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        pixel = new Texture(pixmap);

        pixmap.dispose();

        fuente = new BitmapFont();
        fuente.getData().setScale(1.5f);
    }

    // El batch ya tiene que estar en begin() y usando la cámara fija del HUD
    public void dibujar(SpriteBatch batch, Jugador jugador) {

        // ---- Barra de vida ----
        caja(batch, barraX, barraY, barraAncho, barraAlto);

        float porcentaje = MathUtils.clamp(
                (float) jugador.getVida() / jugador.getVidaMaxima(), 0f, 1f);

        if (porcentaje > 0) {
            rect(batch, barraX, barraY, barraAncho * porcentaje, barraAlto, VERDE);
        }

        texto(batch, jugador.getVida() + " / " + jugador.getVidaMaxima(),
                barraX + barraAncho / 2, barraY + barraAlto / 2, Color.WHITE);

        // ---- Caja de balas (en rojo cuando no quedan) ----
        caja(batch, cajaBalasX, cajaY, cajaBalasAncho, cajaAlto);
        Arma arma = jugador.getArmaSeleccionada();
        arma.dibujarIcono(batch, cajaBalasX + 8, cajaY + 9, 34, 22);

        Color colorBalas = arma.tieneMunicion() ? Color.WHITE : Color.RED;

        texto(batch, arma.getNombre() + "  "
                        + arma.getBalasCargador() + " / " + arma.getMunicionReserva(),
                cajaBalasX + cajaBalasAncho / 2, cajaY + cajaAlto / 2, colorBalas);

        // ---- Caja de monedas ----
        caja(batch, cajaMonedasX, cajaY, cajaMonedasAncho, cajaAlto);

        texto(batch, "Monedas: " + jugador.getMonedas(),
                cajaMonedasX + cajaMonedasAncho / 2, cajaY + cajaAlto / 2, Color.GOLD);

        // ---- Game over ----
        if (!jugador.estaVivo()) {

            fuente.getData().setScale(3f);
            texto(batch, "GAME OVER", 640, 360, Color.RED);
            fuente.getData().setScale(1.5f);
        }

        batch.setColor(Color.WHITE);
    }

    // Rectángulo de color
    private void rect(SpriteBatch batch, float x, float y, float ancho, float alto, Color color) {

        batch.setColor(color);
        batch.draw(pixel, x, y, ancho, alto);
    }

    // Caja con borde oscuro y fondo
    private void caja(SpriteBatch batch, float x, float y, float ancho, float alto) {

        rect(batch, x - 3, y - 3, ancho + 6, alto + 6, BORDE);
        rect(batch, x, y, ancho, alto, FONDO);
    }

    // Texto centrado en (centroX, centroY)
    private void texto(SpriteBatch batch, String texto, float centroX, float centroY, Color color) {

        fuente.setColor(color);
        layout.setText(fuente, texto);

        fuente.draw(batch, layout,
                centroX - layout.width / 2,
                centroY + layout.height / 2);
    }

    public void dispose() {

        pixel.dispose();
        fuente.dispose();
    }
}
