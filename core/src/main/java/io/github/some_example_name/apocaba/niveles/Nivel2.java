package io.github.some_example_name.apocaba.niveles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.some_example_name.apocaba.objects.Moneda;
import io.github.some_example_name.apocaba.objects.Plataforma;
import io.github.some_example_name.apocaba.player.Zombie;

import java.util.ArrayList;

public class Nivel2 extends Nivel {
    private static final float ANCHO_MUNDO = 2138;
    private static final float ALTO_MUNDO = 735;
    private static final float ALTURA_SUELO = 180;
    private static final float Y_PLATAFORMA_1 = 203;
    private static final float Y_PLATAFORMA_2 = 205;
    private static final float Y_PLATAFORMA_3 = 192;
    private static final float ALTURA_PLATAFORMA_1 = Y_PLATAFORMA_1 + 72;
    private static final float ALTURA_PLATAFORMA_2 = Y_PLATAFORMA_2 + 95;
    private static final float ALTURA_PLATAFORMA_3 = Y_PLATAFORMA_3 + 68;

    private final Texture atlas;
    private final TextureRegion plataformaAtlas;
    private final TextureRegion plataformaCortaAtlas;
    private final TextureRegion barreraAtlas;
    private final TextureRegion cajaAtlas;
    private final TextureRegion barrilAtlas;

    public Nivel2(Texture texturaColision) {
        fondo = new Texture("Fondos/Nivel 2 Apocaba.png");
        ancho = ANCHO_MUNDO;
        alto = ALTO_MUNDO;
        zoomCamara = 1f;
        limiteIzquierdo = 20;
        limiteDerecho = ANCHO_MUNDO - 150;
        limiteZombieIzquierdo = 25;
        limiteZombieDerecho = ANCHO_MUNDO - 30;
        inicioJugadorX = 120;
        inicioJugadorY = ALTURA_SUELO;
        alturaSuelo = ALTURA_SUELO;

        atlas = new Texture("Plataformas/objetos-nivel2.png");
        plataformaAtlas = regionDesdeArriba(1120, 260, 760, 145);
        plataformaCortaAtlas = regionDesdeArriba(780, 35, 310, 175);
        barreraAtlas = regionDesdeArriba(330, 600, 190, 110);
        cajaAtlas = regionDesdeArriba(20, 600, 100, 110);
        barrilAtlas = regionDesdeArriba(160, 590, 75, 120);

        crearColisiones(texturaColision);
    }

    private TextureRegion regionDesdeArriba(int x, int y, int ancho, int alto) {
        return new TextureRegion(atlas, x, atlas.getHeight() - y - alto, ancho, alto);
    }

    private void crearColisiones(Texture textura) {
        agregarPlataforma(textura, 0, ALTURA_SUELO - 10, ANCHO_MUNDO, 10);
        agregarSolido(textura, 0, ALTURA_SUELO - 10, 12, 550);
        agregarSolido(textura, ANCHO_MUNDO - 12, ALTURA_SUELO - 10, 12, 550);

        agregarPlataforma(textura, 410, ALTURA_PLATAFORMA_1 - 8, 380, 8);
        agregarPlataforma(textura, 1000, ALTURA_PLATAFORMA_2 - 8, 400, 8);
        agregarPlataforma(textura, 1630, ALTURA_PLATAFORMA_3 - 8, 350, 8);

        agregarSolido(textura, 625, ALTURA_SUELO, 70, 65);
        agregarSolido(textura, 1205, ALTURA_SUELO, 65, 75);
        agregarSolido(textura, 1780, ALTURA_SUELO, 130, 75);
    }

    private void agregarPlataforma(Texture textura, float x, float y, float ancho, float alto) {
        plataformas.add(new Plataforma(textura, x, y, ancho, alto, false, false));
    }

    private void agregarSolido(Texture textura, float x, float y, float ancho, float alto) {
        plataformas.add(new Plataforma(textura, x, y, ancho, alto, false, true));
    }

    @Override
    public ArrayList<Moneda> crearMonedas() {
        ArrayList<Moneda> monedas = new ArrayList<Moneda>();
        monedas.add(new Moneda(520, alturaSuelo));
        monedas.add(new Moneda(1120, alturaSuelo));
        monedas.add(new Moneda(1760, alturaSuelo));
        return monedas;
    }

    @Override
    public ArrayList<Zombie> crearZombies() {
        ArrayList<Zombie> zombies = new ArrayList<Zombie>();
        zombies.add(new Zombie(390, alturaSuelo));
        zombies.add(new Zombie(890, alturaSuelo));
        zombies.add(new Zombie(1510, alturaSuelo));
        zombies.add(new Zombie(1970, alturaSuelo));
        zombies.add(new Zombie(540, ALTURA_PLATAFORMA_1));
        zombies.add(new Zombie(1220, ALTURA_PLATAFORMA_2));
        zombies.add(new Zombie(1790, ALTURA_PLATAFORMA_3));
        return zombies;
    }

    @Override
    public void dibujarObjetos(SpriteBatch batch) {
        batch.draw(plataformaAtlas, 410, Y_PLATAFORMA_1, 380, 72);
        batch.draw(plataformaCortaAtlas, 1000, Y_PLATAFORMA_2, 400, 95);
        batch.draw(plataformaAtlas, 1630, Y_PLATAFORMA_3, 350, 68);
        batch.draw(cajaAtlas, 625, ALTURA_SUELO, 70, 65);
        batch.draw(barrilAtlas, 1205, ALTURA_SUELO, 65, 75);
        batch.draw(barreraAtlas, 1780, ALTURA_SUELO, 130, 75);
    }

    @Override
    public void dispose() {
        super.dispose();
        atlas.dispose();
    }
}
