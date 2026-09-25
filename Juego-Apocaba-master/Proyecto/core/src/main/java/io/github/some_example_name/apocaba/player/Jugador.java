package io.github.some_example_name.apocaba.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;


public class Jugador {
	                                     //Agregar panel de control para cambiar la velocidad entre cada nivel
	                                     //Hacer las imagenes y sprites de los personajes
	                                     //Poner las plataformas y el piso en otra clase


	private float centroX;
	private float centroY;

    private Texture textura;

    private float x;
    private float y;

    // Movimiento
    private float velocidad = 500;

    // Salto y gravedad
    private float velocidadY = 0;
    private float gravedad = -1000;
    private float fuerzaSalto = 500;

    // Piso
    private float suelo = 120;
    private boolean enSuelo = true;

    // Tama�o aproximado del jugador
    private float ancho = 128;
    private float alto = 128;

    // Plataforma
    private float plataformaX = 200;
    private float plataformaY = 150;
    private float plataformaAncho = 200;
    private float plataformaAlto = 20;

    
   
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


        // Guardamos la posici�n anterior
        float yAnterior = y;


        // =========================
        // GRAVEDAD
        // =========================

        velocidadY += gravedad * delta;

        y += velocidadY * delta;


        // =========================
        // COLISI�N CON EL PISO
        // =========================

        if (y <= suelo) {

            y = suelo;
            velocidadY = 0;
            enSuelo = true;
        }


        // =========================
        // COLISI�N CON PLATAFORMA
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