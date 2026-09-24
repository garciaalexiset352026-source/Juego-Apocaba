package io.github.some_example_name.apocaba.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.apocaba.weapon.Bala;

public class Jugador {
	


	private float centroX;
	private float centroY;

    private Texture textura;

    private float x;
    private float y;

    // Movimiento
    private float velocidad = 250;

    // Salto y gravedad
    private float velocidadY = 0;
    private float gravedad = -1000;
    private float fuerzaSalto = 500;

    // Piso
    private float suelo = 120;
    private boolean enSuelo = true;

    // Tamaño aproximado del jugador
    private float ancho = 128;
    private float alto = 128;

    // Plataforma
    private float plataformaX = 200;
    private float plataformaY = 150;
    private float plataformaAncho = 200;
    private float plataformaAlto = 20;

    
    public Vector2 obtenerDireccionMouse() {

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        float centroJugadorX = x + ancho / 2;
        float centroJugadorY = y + alto / 2;

        float direccionX = mouseX - centroJugadorX;
        float direccionY = mouseY - centroJugadorY;

        Vector2 direccion = new Vector2(direccionX, direccionY);

        direccion.nor();

        return direccion;
    }
    public Jugador() {
    	
        textura = new Texture("Personaje/PJ prueba.png");

        x = 100;
        y = suelo;
    }

    public void actualizar(float delta) {

        // =========================
        // MOVIMIENTO
        // =========================

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= velocidad * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += velocidad * delta;
        }


        // =========================
        // SALTO
        // =========================

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && enSuelo) {

            velocidadY = fuerzaSalto;
            enSuelo = false;
        }


        // Guardamos la posición anterior
        float yAnterior = y;


        // =========================
        // GRAVEDAD
        // =========================

        velocidadY += gravedad * delta;

        y += velocidadY * delta;


        // =========================
        // COLISIÓN CON EL PISO
        // =========================

        if (y <= suelo) {

            y = suelo;
            velocidadY = 0;
            enSuelo = true;
        }


        // =========================
        // COLISIÓN CON PLATAFORMA
        // =========================

        float parteSuperiorPlataforma = plataformaY + plataformaAlto;

        boolean estaCayendo = velocidadY <= 0;

        boolean estaSobreLaPlataforma =
                x + ancho > plataformaX &&
                x < plataformaX + plataformaAncho;

        boolean vieneDesdeArriba =
                yAnterior >= parteSuperiorPlataforma;

        boolean llegoALaPlataforma =
                y <= parteSuperiorPlataforma;


        if (estaCayendo &&
            estaSobreLaPlataforma &&
            vieneDesdeArriba &&
            llegoALaPlataforma) {

            y = parteSuperiorPlataforma;

            velocidadY = 0;

            enSuelo = true;
        }
    }
    public Bala disparar() {

        Vector2 direccion = obtenerDireccionMouse();

        float salidaX = x + ancho / 2;
        float salidaY = y + alto / 2;

        return new Bala(
            salidaX,
            salidaY,
            direccion.x,
            direccion.y
        );
    }

    // =========================
    // DIBUJAR PERSONAJE
    // =========================

    public void dibujar(SpriteBatch batch) {

        batch.draw(textura, x, y, ancho, alto);
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

    public void dispose() {

        textura.dispose();
    }
}