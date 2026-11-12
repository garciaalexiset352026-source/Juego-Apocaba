package io.github.some_example_name.apocaba.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public abstract class Arma {

    private static final float DESPLAZAMIENTO_MANO_X = 20;

    private final String nombre;
    private final Texture textura;
    private final TextureRegion region;
    private final int capacidadCargador;
    private final int municionTotalMaxima;
    private final int danio;
    private final float cadencia;
    private final float alcance;
    private final float velocidadBala;
    private final float ancho;
    private final float distanciaBoca;

    private int balasCargador;
    private int municionReserva;
    private float angulo;
    private float manoX;
    private float manoY;
    private boolean mirandoDerecha = true;

    protected Arma(String nombre, String rutaTextura, int capacidadCargador,
                   int municionTotalMaxima, int danio, float cadencia,
                   float alcance, float velocidadBala, float ancho,
                   float distanciaBoca, boolean invertirTextura) {
        this.nombre = nombre;
        this.capacidadCargador = capacidadCargador;
        this.municionTotalMaxima = municionTotalMaxima;
        this.danio = danio;
        this.cadencia = cadencia;
        this.alcance = alcance;
        this.velocidadBala = velocidadBala;
        this.ancho = ancho;
        this.distanciaBoca = distanciaBoca;

        textura = new Texture(rutaTextura);
        region = new TextureRegion(textura);
        if (invertirTextura) {
            region.flip(true, false);
        }

        balasCargador = capacidadCargador;
        municionReserva = municionTotalMaxima - capacidadCargador;
    }

    public void apuntar(float objetivoX, float objetivoY, float centroJugadorX,
                        float jugadorY, float altoJugador, boolean mirandoDerecha) {
        this.mirandoDerecha = mirandoDerecha;
        manoX = centroJugadorX + (mirandoDerecha
                ? DESPLAZAMIENTO_MANO_X : -DESPLAZAMIENTO_MANO_X);
        manoY = jugadorY + altoJugador * 0.58f;
        angulo = MathUtils.atan2(objetivoY - manoY, objetivoX - manoX)
                * MathUtils.radiansToDegrees;
    }

    public void dibujar(SpriteBatch batch) {
        float alto = ancho * textura.getHeight() / textura.getWidth();
        float origenX = ancho * 0.18f;
        float origenY = alto * 0.16f;
        float rotacion = mirandoDerecha ? angulo : angulo - 180;

        batch.draw(region,
                manoX - origenX, manoY - origenY,
                origenX, origenY,
                ancho, alto,
                mirandoDerecha ? 1 : -1, 1,
                rotacion);
    }

    public void dibujarIcono(SpriteBatch batch, float x, float y, float ancho, float alto) {
        batch.draw(region, x, y, ancho, alto);
    }

    public float getPuntoDisparoX() {
        return manoX + MathUtils.cosDeg(angulo) * distanciaBoca;
    }

    public float getPuntoDisparoY() {
        return manoY + MathUtils.sinDeg(angulo) * distanciaBoca;
    }

    public boolean tieneMunicion() {
        return balasCargador > 0;
    }

    public void gastarMunicion() {
        if (balasCargador > 0) {
            balasCargador--;
        }
    }

    public void recargar() {
        int transferidas = Math.min(capacidadCargador - balasCargador, municionReserva);
        balasCargador += transferidas;
        municionReserva -= transferidas;
    }

    public void agregarMunicion(int cantidad) {
        int espacioDisponible = municionTotalMaxima - getMunicionTotal();
        municionReserva += Math.min(Math.max(0, cantidad), espacioDisponible);
    }

    public String getNombre() {
        return nombre;
    }

    public int getDanio() {
        return danio;
    }

    public float getCadencia() {
        return cadencia;
    }

    public float getAlcance() {
        return alcance;
    }

    public float getVelocidadBala() {
        return velocidadBala;
    }

    public int getBalasCargador() {
        return balasCargador;
    }

    public int getMunicionReserva() {
        return municionReserva;
    }

    public int getMunicionTotal() {
        return balasCargador + municionReserva;
    }

    public int getMunicionTotalMaxima() {
        return municionTotalMaxima;
    }

    public void dispose() {
        textura.dispose();
    }
}
