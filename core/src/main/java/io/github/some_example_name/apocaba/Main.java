package io.github.some_example_name.apocaba;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.apocaba.player.Zombie;
import io.github.some_example_name.apocaba.objects.CajaMunicion;
import io.github.some_example_name.apocaba.objects.Moneda;
import io.github.some_example_name.apocaba.objects.Plataforma;
import io.github.some_example_name.apocaba.player.Jugador;
import io.github.some_example_name.apocaba.ui.Hud;
import io.github.some_example_name.apocaba.ui.MenuPrincipal;
import io.github.some_example_name.apocaba.weapons.Arma;
import io.github.some_example_name.apocaba.weapons.Balas;
import io.github.some_example_name.apocaba.niveles.Nivel;
import io.github.some_example_name.apocaba.niveles.Nivel1;
import io.github.some_example_name.apocaba.niveles.Nivel2;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private ShapeRenderer debugRenderer;
    private boolean mostrarColisiones = false;

    private Jugador jugador;
    private Texture piso;
    private Texture texturaBala;
    private Texture texturaMunicion;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private Hud hud;

    // Menú: el juego empieza en el menú principal
    private MenuPrincipal menu;
    private boolean enMenu = true;
    private boolean gameOver = false;
    private boolean pausado = false;
    private boolean nivelCompletado = false;

    // Para que el click en "Jugar" no dispare una bala apenas empieza la partida
    private boolean esperarSoltarMouse = false;

    private ArrayList<Moneda> monedas;
    private ArrayList<Balas> balas;
    private ArrayList<Zombie> zombies;
    private ArrayList<CajaMunicion> cajasMunicion;

    // Plataformas y las texturas que se cargaron para ellas (para liberarlas al final)
    private ArrayList<Plataforma> plataformas;
    private Nivel nivel;
    private int nivelSeleccionado = 1;

    // Disparo
    private float tiempoDesdeDisparo = 0;

    // Drop de munición al morir un zombie
    private float probabilidadMunicion = 0.35f;
    private int balasPorCaja = 10;

    // Para convertir la posición del mouse a coordenadas del mundo
    private Vector3 mouse = new Vector3();


    @Override
    public void create() {

        batch = new SpriteBatch();
        debugRenderer = new ShapeRenderer();

        monedas = new ArrayList<Moneda>();
        balas = new ArrayList<Balas>();
        cajasMunicion = new ArrayList<CajaMunicion>();
        zombies = new ArrayList<Zombie>();
        Zombie.cargarRecursos();

        // Crear textura para piso y plataformas
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        pixmap.setColor(0.3f, 0.3f, 0.3f, 1);
        pixmap.fill();

        piso = new Texture(pixmap);

        pixmap.dispose();

        // Textura de la bala (un rectángulo amarillo)
        Pixmap pixmapBala = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapBala.setColor(1f, 0.9f, 0.2f, 1);
        pixmapBala.fill();

        texturaBala = new Texture(pixmapBala);

        pixmapBala.dispose();

        // Textura de la caja de munición.
        // Si existe assets/municion.png se usa esa imagen; si no, se dibuja una caja simple.
        if (Gdx.files.internal("municion.png").exists()) {

            texturaMunicion = new Texture("municion.png");

        } else {

            Pixmap pixmapMunicion = new Pixmap(20, 15, Pixmap.Format.RGBA8888);

            // borde oscuro
            pixmapMunicion.setColor(0.1f, 0.1f, 0.1f, 1);
            pixmapMunicion.fill();

            // caja verde militar
            pixmapMunicion.setColor(0.35f, 0.45f, 0.2f, 1);
            pixmapMunicion.fillRectangle(1, 1, 18, 13);

            // franja amarilla (como una bala)
            pixmapMunicion.setColor(1f, 0.85f, 0.2f, 1);
            pixmapMunicion.fillRectangle(4, 6, 12, 3);

            texturaMunicion = new Texture(pixmapMunicion);

            pixmapMunicion.dispose();
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, 1280, 720);
        hud = new Hud();
        menu = new MenuPrincipal();
        plataformas = new ArrayList<Plataforma>();
        balas = new ArrayList<Balas>();
        cajasMunicion = new ArrayList<CajaMunicion>();
        cargarNivel(1);
    }

    private void cargarNivel(int nivel) {
        if (this.nivel != null) {
            this.nivel.dispose();
        }

        nivelSeleccionado = nivel;
        this.nivel = nivel == 2 ? new Nivel2(piso) : new Nivel1(piso);
        plataformas = this.nivel.getPlataformas();
        camera.zoom = this.nivel.getZoomCamara();
        reiniciarEntidadesNivel();
        camera.position.set(this.nivel.getInicioJugadorX() + 64,
                this.nivel.getInicioJugadorY() + 64, 0);
        camera.update();
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // =========================
        // MENÚ PRINCIPAL
        // =========================

        if (!enMenu && !gameOver && !jugador.estaVivo()) {
            gameOver = true;
        }

        if (enMenu || gameOver || pausado || nivelCompletado) {

            ScreenUtils.clear(0.05f, 0.05f, 0.07f, 1);

            if (gameOver) {
                menu.setGameOver(true);
            } else if (pausado) {
                menu.setPausado(true);
            } else if (nivelCompletado) {
                menu.setNivelCompletado(true);
            } else {
                menu.setGameOver(false);
            }
            MenuPrincipal.Accion accion = menu.actualizar(hudCamera);

            batch.setProjectionMatrix(hudCamera.combined);
            batch.begin();
            menu.dibujar(batch);
            batch.end();

            if (accion == MenuPrincipal.Accion.REINTENTAR) {
                reiniciarPartida();
                gameOver = false;
                pausado = false;
                nivelCompletado = false;
                enMenu = false;
                esperarSoltarMouse = true;
            } else if (accion == MenuPrincipal.Accion.MENU_PRINCIPAL) {
                reiniciarPartida();
                gameOver = false;
                pausado = false;
                nivelCompletado = false;
                enMenu = true;
            } else if (accion == MenuPrincipal.Accion.REANUDAR) {
                pausado = false;
                esperarSoltarMouse = true;
            } else if (accion == MenuPrincipal.Accion.SIGUIENTE_NIVEL) {
                nivelCompletado = false;
                iniciarNivel(nivelSeleccionado + 1);
            } else if (accion == MenuPrincipal.Accion.NIVEL_1) {
                iniciarNivel(1);
            } else if (accion == MenuPrincipal.Accion.NIVEL_2) {
                iniciarNivel(2);
            } else if (accion == MenuPrincipal.Accion.NIVEL_3) {
                iniciarNivel(3);
            } else if (accion == MenuPrincipal.Accion.NIVEL_4) {
                iniciarNivel(4);
            } else if (accion == MenuPrincipal.Accion.NIVEL_5) {
                iniciarNivel(5);
            } else if (accion == MenuPrincipal.Accion.NIVEL_6) {
                iniciarNivel(6);
            } else if (accion == MenuPrincipal.Accion.JUGAR) {
                iniciarNivel(nivelSeleccionado);
            } else if (accion == MenuPrincipal.Accion.SALIR) {
                Gdx.app.exit();
            }

            return;
        }

        // ESC pausa la partida
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pausado = true;
            return;
        }

        // K muestra u oculta los rectángulos de colisión
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            mostrarColisiones = !mostrarColisiones;
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1);

        // =========================
        // ACTUALIZAR
        // =========================

        jugador.actualizar(delta, plataformas,
                nivel.getLimiteIzquierdo(), nivel.getLimiteDerecho());

        if (nivel.tieneSalida()) {
            float salidaDx = jugador.getCentroX() - nivel.getSalidaX();
            float salidaDy = jugador.getY() + jugador.getAlto() / 2 - nivel.getSalidaY();
            if (salidaDx * salidaDx + salidaDy * salidaDy <= 42 * 42) {
                nivelCompletado = true;
            }
        }

        // Los zombies son sólidos: el jugador no los traspasa
        // (si salta lo bastante alto, pasa por encima)
        for (Zombie zombie : zombies) {

            if (zombie.estaVivo()) {
                jugador.chocarConCuerpo(
                        zombie.getX(),
                        zombie.getY(),
                        zombie.getAnchoCuerpo(),
                        zombie.getAltoCuerpo());
            }
        }

        // La cámara sigue al jugador
        float mitadVistaX = camera.viewportWidth * camera.zoom / 2;
        float mitadVistaY = camera.viewportHeight * camera.zoom / 2;
        camera.position.x = MathUtils.clamp(
                jugador.getCentroX(), mitadVistaX, nivel.getAncho() - mitadVistaX);
        camera.position.y = MathUtils.clamp(
                jugador.getCentroY(), mitadVistaY, nivel.getAlto() - mitadVistaY);
        camera.update();

        // Posición del mouse en pantalla -> posición en el mundo
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            jugador.seleccionarArma(0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            jugador.seleccionarArma(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            jugador.seleccionarArma(2);
        }

        // El personaje mira hacia donde apunta el mouse
        jugador.mirarHacia(mouse.x, mouse.y);

        // Monedas: si el jugador toca una, se suma al contador
        for (Moneda moneda : monedas) {
            moneda.actualizar(delta);

            if (moneda.comprobarColision(
                    jugador.getX(),
                    jugador.getY(),
                    jugador.getAncho(),
                    jugador.getAlto())) {

                jugador.sumarMoneda();
            }
        }

        // Cajas de munición: si el jugador las toca (y no tiene la munición llena)
        // se suman las balas
        Iterator<CajaMunicion> itCajas = cajasMunicion.iterator();

        while (itCajas.hasNext()) {

            CajaMunicion caja = itCajas.next();

            caja.actualizar(delta);

            Arma arma = jugador.getArmaSeleccionada();
            boolean municionLlena = arma.getMunicionTotal() >= arma.getMunicionTotalMaxima();

            if (caja.comprobarColision(
                    jugador.getX(),
                    jugador.getY(),
                    jugador.getAncho(),
                    jugador.getAlto(),
                    municionLlena)) {

                arma.agregarMunicion(caja.getCantidad());
            }

            if (!caja.estaActiva()) {
                itCajas.remove();
            }
        }

        // Disparo apuntando con el mouse (mantener click izquierdo)
        tiempoDesdeDisparo += delta;

        // H = recibir 10 de daño (prueba), R = recargar el cargador
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            jugador.recibirDanio(10);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            jugador.getArmaSeleccionada().recargar();
        }

        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            esperarSoltarMouse = false;
        }

        // Solo dispara si está vivo y le quedan balas
        Arma arma = jugador.getArmaSeleccionada();
        if (!esperarSoltarMouse &&
                jugador.estaVivo() &&
                arma.tieneMunicion() &&
                Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&
                tiempoDesdeDisparo >= arma.getCadencia()) {

            arma.gastarMunicion();

            balas.add(new Balas(
                    texturaBala,
                    arma.getPuntoDisparoX(),
                    arma.getPuntoDisparoY(),
                    mouse.x,
                    mouse.y,
                    arma.getVelocidadBala(),
                    arma.getDanio(),
                    arma.getAlcance()));

            tiempoDesdeDisparo = 0;
        }

        // Actualizar balas y borrar las que ya no sirven
        Iterator<Balas> it = balas.iterator();

        while (it.hasNext()) {

            Balas bala = it.next();

            bala.actualizar(delta);

            // Las balas se frenan contra las plataformas sólidas (autos)
            for (Plataforma p : plataformas) {

                if (bala.estaActiva() &&
                        p.isSolida() &&
                        bala.getX() > p.getX() && bala.getX() < p.getX() + p.getAncho() &&
                        bala.getY() > p.getY() && bala.getY() < p.getTecho()) {

                    bala.desactivar();
                }
            }

            // Si la bala toca a un zombie, le saca vida y desaparece
            for (Zombie zombie : zombies) {

                if (bala.estaActiva() &&
                        zombie.estaVivo() &&
                        zombie.colisionaConPunto(bala.getX(), bala.getY())) {

                    zombie.recibirDanio(bala.getDanio());
                    bala.desactivar();
                }
            }

            if (!bala.estaActiva()) {
                it.remove();
            }
        }

        // Zombies: persiguen / atacan al jugador.
        // Los muertos sueltan una moneda y, con cierta probabilidad, una caja de balas.
        Iterator<Zombie> itZombies = zombies.iterator();

        while (itZombies.hasNext()) {

            Zombie zombie = itZombies.next();

            zombie.actualizar(delta, jugador, plataformas,
                    nivel.getLimiteZombieIzquierdo(), nivel.getLimiteZombieDerecho());

            if (!zombie.estaVivo()) {

                monedas.add(new Moneda(zombie.getX() - 30, zombie.getY()));

                if (MathUtils.random() < probabilidadMunicion) {
                    // Al costado de la moneda (la moneda ocupa de x-30 a x+30)
                    cajasMunicion.add(new CajaMunicion(
                            texturaMunicion, zombie.getX() + 35,
                            zombie.getY(), balasPorCaja));
                }

                itZombies.remove();
            }
        }

        // =========================
        // DIBUJAR MUNDO
        // =========================

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Fondo
        batch.draw(nivel.getFondo(), 0, 0, nivel.getAncho(), nivel.getAlto());
        nivel.dibujarObjetos(batch);

        // Monedas
        for (Moneda moneda : monedas) {
            moneda.dibujar(batch);
        }

        // Cajas de munición
        for (CajaMunicion caja : cajasMunicion) {
            caja.dibujar(batch);
        }

        // Balas
        for (Balas bala : balas) {
            bala.dibujar(batch);
        }

        // Zombies
        for (Zombie zombie : zombies) {
            zombie.dibujar(batch);
        }

        // Jugador
        jugador.dibujar(batch);

        batch.end();

        if (nivel.tieneSalida()) {
            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
            debugRenderer.setColor(Color.GREEN);
            debugRenderer.circle(nivel.getSalidaX(), nivel.getSalidaY() + 8, 24);
            debugRenderer.end();
        }

        // =========================
        // DEBUG DE COLISIONES
        // =========================

        if (mostrarColisiones) {

            debugRenderer.setProjectionMatrix(camera.combined);
            debugRenderer.begin(ShapeRenderer.ShapeType.Line);

            // Verde: rectángulos de las plataformas
            debugRenderer.setColor(Color.GREEN);

            for (Plataforma p : plataformas) {
                debugRenderer.rect(
                        p.getX(),
                        p.getY(),
                        p.getAncho(),
                        p.getAlto()
                );
            }

            // Rojo: rectángulo aproximado del jugador
            debugRenderer.setColor(Color.RED);

            debugRenderer.rect(
                    jugador.getX(),
                    jugador.getY(),
                    jugador.getAncho(),
                    jugador.getAlto()
            );

            debugRenderer.end();
        }

        // =========================
        // DIBUJAR HUD (fijo en pantalla)
        // =========================

        batch.setProjectionMatrix(hudCamera.combined);

        batch.begin();

        hud.dibujar(batch, jugador);

        batch.end();
    }

    private void reiniciarPartida() {
        reiniciarEntidadesNivel();
        camera.position.set(nivel.getInicioJugadorX() + 64,
                nivel.getInicioJugadorY() + 64, 0);
        camera.update();
    }

    private void reiniciarEntidadesNivel() {
        if (jugador != null) {
            jugador.dispose();
        }
        jugador = new Jugador();
        jugador.setAlturaSuelo(nivel.getAlturaSuelo());
        jugador.setPosicion(nivel.getInicioJugadorX(), nivel.getInicioJugadorY());

        if (monedas != null) {
            for (Moneda moneda : monedas) {
                moneda.dispose();
            }
        } else {
            monedas = new ArrayList<Moneda>();
        }
        monedas.clear();
        monedas.addAll(nivel.crearMonedas());

        balas.clear();
        cajasMunicion.clear();
        zombies.clear();
        zombies.addAll(nivel.crearZombies());
        tiempoDesdeDisparo = 0;
    }

    private void iniciarNivel(int nivel) {
        cargarNivel(nivel);
        gameOver = false;
        pausado = false;
        nivelCompletado = false;
        enMenu = false;
        esperarSoltarMouse = true;
    }

    @Override
    public void dispose() {

        batch.dispose();
        debugRenderer.dispose();

        nivel.dispose();
        piso.dispose();
        texturaBala.dispose();
        texturaMunicion.dispose();

        hud.dispose();
        menu.dispose();
        jugador.dispose();
        Zombie.liberarRecursos();

        for (Moneda moneda : monedas) {
            moneda.dispose();
        }
    }
}
