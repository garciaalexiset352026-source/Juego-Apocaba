package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.apocaba.player.Jugador;
import io.github.some_example_name.apocaba.objects.Moneda;
import java.util.ArrayList;
import com.badlogic.gdx.Input;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Jugador jugador;
    private Texture piso;
    private Texture fondo;
    private OrthographicCamera camera;
    private Moneda moneda;
    
    
    @Override
    public void create() {

        batch = new SpriteBatch();
        
        jugador = new Jugador();
        
        fondo = new Texture("Fondos/segundo-nivel-fuego.png");
        
        moneda = new Moneda(500, 120);

       
        // Crear c�mara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        // Crear textura para piso y plataformas
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        pixmap.setColor(0.3f, 0.3f, 0.3f, 1);
        pixmap.fill();

        piso = new Texture(pixmap);

        pixmap.dispose();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);

       
        // Actualizar jugador
        jugador.actualizar(delta);
        
        if (moneda.comprobarColision(
                jugador.getX(),
                jugador.getY(),
                jugador.getAncho(),
                jugador.getAlto())) {

            System.out.println("�Moneda recogida!");
        }

        // La c�mara sigue al jugador
        camera.position.x = jugador.getX() + 64;

        camera.update();

        // Le decimos al SpriteBatch que use la c�mara
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        
        // Fondo
        batch.draw(fondo, 0, 0, 3000, 1000);
        
        // Piso
        batch.draw(piso, 0, 80, 3000, 40);

        // Plataforma
        batch.draw(piso, 200, 150, 200, 20);
        
        // moneda
        moneda.dibujar(batch);

        

        // Jugador
        jugador.dibujar(batch);

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
        fondo.dispose();
        piso.dispose();
        jugador.dispose();
        moneda.dispose();
    }
}
