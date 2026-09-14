package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.apocaba.player.Jugador;

public class Main extends ApplicationAdapter {
	private Texture fondo;
    private SpriteBatch batch;
    private Jugador jugador;

    @Override
    public void create() {

        batch = new SpriteBatch();

        fondo = new Texture("Fondos/fondo.jpg");

        jugador = new Jugador();
    }
    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);

        jugador.actualizar(delta);

        batch.begin();

        batch.draw(fondo, 0, 0, 1280, 720);

        jugador.dibujar(batch);

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
        fondo.dispose();
        jugador.dispose();
    }
}
