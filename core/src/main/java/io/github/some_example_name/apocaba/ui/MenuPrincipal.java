package io.github.some_example_name.apocaba.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class MenuPrincipal {

    public enum Accion {
        NADA, JUGAR, SALIR, REINTENTAR, MENU_PRINCIPAL, REANUDAR, SIGUIENTE_NIVEL,
        NIVEL_1, NIVEL_2, NIVEL_3, NIVEL_4, NIVEL_5, NIVEL_6
    }

    private enum Modo { PRINCIPAL, GAME_OVER, PAUSA, NIVEL_COMPLETADO }

    // Imágenes opcionales. Si no existen, el menú se dibuja igual con colores y texto.
    //   assets/Menu/fondo_menu.png  -> fondo de pantalla completa (sin botones ni título)
    //   assets/Menu/titulo.png      -> logo "APOCABA" (con fondo transparente)
    private static final String RUTA_FONDO = "Fondos/menu.png";
    private static final String RUTA_TITULO = "Fondos/letras-menu.png";

    private static final String[] OPCIONES = { "ELEGIR NIVEL", "SALIR" };
    private static final String[] OPCIONES_GAME_OVER = { "REINTENTAR", "MENU PRINCIPAL" };
    private static final String[] OPCIONES_PAUSA = { "REANUDAR", "MENU PRINCIPAL" };
    private static final String[] OPCIONES_COMPLETADO = { "SIGUIENTE NIVEL", "MENU PRINCIPAL" };
    private static final String[] OPCIONES_NIVELES = {
            "NIVEL 1", "NIVEL 2", "NIVEL 3", "NIVEL 4", "NIVEL 5", "NIVEL 6"
    };

    private static final Color COLOR_TEXTO_TITULO = new Color(0.92f, 0.9f, 0.84f, 1f);
    private static final Color ROJO = new Color(0.78f, 0.14f, 0.1f, 1f);
    private static final Color ROJO_OSCURO = new Color(0.45f, 0.06f, 0.05f, 1f);
    private static final Color GRIS_CLARO = new Color(0.82f, 0.8f, 0.76f, 1f);
    private static final Color GRIS_OSCURO = new Color(0.15f, 0.15f, 0.16f, 1f);
    private static final Color SOMBRA = new Color(0f, 0f, 0f, 0.6f);

    private int seleccionada = 0;
    private Modo modo = Modo.PRINCIPAL;
    private boolean seleccionandoNivel = false;

    private Texture pixel;
    private Texture fondo;
    private Texture titulo;

    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteSubtitulo;
    private BitmapFont fuenteBoton;
    private BitmapFont fuentePequena;

    private GlyphLayout layout = new GlyphLayout();
    private Vector3 mouse = new Vector3();
    private int ultimoMouseX = -1;
    private int ultimoMouseY = -1;

    // Botones (pantalla de 1280 x 720, origen abajo a la izquierda)
    private float botonX = 70;
    private float botonAncho = 340;
    private float botonAlto = 60;
    private float primerBotonY = 400;
    private float separacion = 80;


    public MenuPrincipal() {

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        pixel = new Texture(pixmap);

        pixmap.dispose();

        if (Gdx.files.internal(RUTA_FONDO).exists()) {
            fondo = new Texture(RUTA_FONDO);
        }

        if (Gdx.files.internal(RUTA_TITULO).exists()) {
            titulo = new Texture(RUTA_TITULO);
        }

        fuenteTitulo = crearFuente(6.5f);
        fuenteSubtitulo = crearFuente(2.4f);
        fuenteBoton = crearFuente(2.2f);
        fuentePequena = crearFuente(1.2f);
    }

    private BitmapFont crearFuente(float escala) {

        BitmapFont fuente = new BitmapFont();
        fuente.getData().setScale(escala);

        // Para que no se vea tan pixelada al agrandarla
        fuente.getRegion().getTexture().setFilter(
                Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return fuente;
    }


    // =========================
    // ACTUALIZAR
    // Devuelve qué se eligió (o NADA si todavía no eligió)
    // =========================

    public Accion actualizar(OrthographicCamera camaraHud) {

        // ---- Mouse ----
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camaraHud.unproject(mouse);

        // La selección sigue al mouse solo cuando éste se mueve,
        // así no pelea con el teclado
        boolean mouseSeMovio = Gdx.input.getX() != ultimoMouseX ||
                               Gdx.input.getY() != ultimoMouseY;

        ultimoMouseX = Gdx.input.getX();
        ultimoMouseY = Gdx.input.getY();

        if (mouseSeMovio) {

            for (int i = 0; i < cantidadOpciones(); i++) {

                if (mouseSobreBoton(i)) {
                    seleccionada = i;
                }
            }
        }

        // ---- Teclado ----
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.isKeyJustPressed(Input.Keys.W)) {

            seleccionada = (seleccionada - 1 + cantidadOpciones()) % cantidadOpciones();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) ||
            Gdx.input.isKeyJustPressed(Input.Keys.S)) {

            seleccionada = (seleccionada + 1) % cantidadOpciones();
        }

        if (seleccionandoNivel && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            seleccionandoNivel = false;
            seleccionada = 0;
            return Accion.NADA;
        }

        boolean confirmoConTeclado =
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isKeyJustPressed(Input.Keys.E);

        boolean confirmoConMouse =
                Gdx.input.justTouched() && mouseSobreBoton(seleccionada);

        if (confirmoConTeclado || confirmoConMouse) {

            if (modo == Modo.PAUSA) {
                return seleccionada == 0 ? Accion.REANUDAR : Accion.MENU_PRINCIPAL;
            }

            if (modo == Modo.NIVEL_COMPLETADO) {
                return seleccionada == 0 ? Accion.SIGUIENTE_NIVEL : Accion.MENU_PRINCIPAL;
            }

            if (modo == Modo.GAME_OVER) {
                return seleccionada == 0 ? Accion.REINTENTAR : Accion.MENU_PRINCIPAL;
            }

            if (seleccionandoNivel) {
                seleccionandoNivel = false;
                switch (seleccionada) {
                    case 0: return Accion.NIVEL_1;
                    case 1: return Accion.NIVEL_2;
                    case 2: return Accion.NIVEL_3;
                    case 3: return Accion.NIVEL_4;
                    case 4: return Accion.NIVEL_5;
                    default: return Accion.NIVEL_6;
                }
            }

            if (seleccionada == 0) {
                seleccionandoNivel = true;
                seleccionada = 0;
                ultimoMouseX = -1;
                ultimoMouseY = -1;
                return Accion.NADA;
            }

            return Accion.SALIR;
        }

        return Accion.NADA;
    }

    public void setGameOver(boolean gameOver) {
        setModo(gameOver ? Modo.GAME_OVER : Modo.PRINCIPAL);
    }

    public void setPausado(boolean pausado) {
        setModo(pausado ? Modo.PAUSA : Modo.PRINCIPAL);
    }

    public void setNivelCompletado(boolean completado) {
        setModo(completado ? Modo.NIVEL_COMPLETADO : Modo.PRINCIPAL);
    }

    private void setModo(Modo nuevoModo) {
        if (modo != nuevoModo) {
            modo = nuevoModo;
            seleccionada = 0;
            ultimoMouseX = -1;
            ultimoMouseY = -1;
        }
    }

    private int cantidadOpciones() {
        if (modo == Modo.GAME_OVER || modo == Modo.PAUSA || modo == Modo.NIVEL_COMPLETADO) {
            return OPCIONES_GAME_OVER.length;
        }
        return seleccionandoNivel ? OPCIONES_NIVELES.length : OPCIONES.length;
    }

    private String opcion(int indice) {
        if (modo == Modo.PAUSA) {
            return OPCIONES_PAUSA[indice];
        }
        if (modo == Modo.NIVEL_COMPLETADO) {
            return OPCIONES_COMPLETADO[indice];
        }
        if (modo == Modo.GAME_OVER) {
            return OPCIONES_GAME_OVER[indice];
        }
        return seleccionandoNivel ? OPCIONES_NIVELES[indice] : OPCIONES[indice];
    }

    private float botonY(int indice) {
        if (modo == Modo.PAUSA || modo == Modo.NIVEL_COMPLETADO) {
            return 380 - indice * separacion;
        }
        return primerBotonY - indice * separacion;
    }

    private float obtenerBotonX() {
        return modo == Modo.PAUSA || modo == Modo.NIVEL_COMPLETADO ? 470 : botonX;
    }

    private boolean mouseSobreBoton(int indice) {

        float y = botonY(indice);

        float x = obtenerBotonX();
        return mouse.x >= x && mouse.x <= x + botonAncho &&
               mouse.y >= y && mouse.y <= y + botonAlto;
    }


    // =========================
    // DIBUJAR
    // El batch ya tiene que estar en begin() con la cámara del HUD
    // =========================

    public void dibujar(SpriteBatch batch) {

        // ---- Fondo ----
        if (modo == Modo.PAUSA || modo == Modo.NIVEL_COMPLETADO) {
            rect(batch, 0, 0, 1280, 720, new Color(0f, 0f, 0f, 0.72f));
        } else if (fondo != null) {

            batch.setColor(Color.WHITE);
            batch.draw(fondo, 0, 0, 1280, 720);

        } else {

            rect(batch, 0, 0, 1280, 720, new Color(0.07f, 0.07f, 0.09f, 1f));
        }

        // Capa oscura para que se lea el texto
        rect(batch, 0, 0, 1280, 720, new Color(0f, 0f, 0f, 0.45f));

        // ---- Título ----
        if (modo == Modo.PAUSA) {

            texto(batch, fuenteTitulo, "PAUSA", 510, 610, COLOR_TEXTO_TITULO);

        } else if (modo == Modo.NIVEL_COMPLETADO) {

            texto(batch, fuenteTitulo, "NIVEL COMPLETADO", 285, 610, COLOR_TEXTO_TITULO);

        } else if (modo == Modo.GAME_OVER) {

            texto(batch, fuenteTitulo, "GAME OVER", 70, 670, COLOR_TEXTO_TITULO);

        } else if (seleccionandoNivel) {

            texto(batch, fuenteTitulo, "ELEGIR NIVEL", 70, 670, COLOR_TEXTO_TITULO);

        } else if (titulo != null) {

            float ancho = 460;
            float alto = ancho * titulo.getHeight() / titulo.getWidth();

            batch.setColor(Color.WHITE);
            batch.draw(titulo, 60, 690 - alto, ancho, alto);

        } else {

            // (sin tildes porque la fuente por defecto de libGDX puede no tenerlas)
            texto(batch, fuenteTitulo, "APOCABA", 70, 670, COLOR_TEXTO_TITULO);
            texto(batch, fuenteSubtitulo, "LA ULTIMA CANCHA", 78, 555, ROJO);
        }

        // ---- Botones ----
        for (int i = 0; i < cantidadOpciones(); i++) {

            boolean activo = (i == seleccionada);
            float y = botonY(i);
            float x = obtenerBotonX();

            // Sombra
            rect(batch, x + 5, y - 5, botonAncho, botonAlto, SOMBRA);

            // Botón: rojo si está seleccionado, gris claro si no
            rect(batch, x, y, botonAncho, botonAlto, activo ? ROJO : GRIS_CLARO);

            // Franja oscura abajo, para darle relieve
            rect(batch, x, y, botonAncho, 6, activo ? ROJO_OSCURO : new Color(0.55f, 0.53f, 0.5f, 1f));

            // Texto
            texto(batch, fuenteBoton, opcion(i), x + 40, y + botonAlto / 2 + 2,
                    activo ? Color.WHITE : GRIS_OSCURO);

            // Marcador al costado del botón seleccionado
            if (activo) {
                texto(batch, fuenteBoton, ">", x - 38, y + botonAlto / 2 + 2, ROJO);
            }
        }

        // ---- Textos chicos ----
        texto(batch, fuentePequena, "v1.0", 30, 30, new Color(0.7f, 0.7f, 0.7f, 1f));

        texto(batch, fuentePequena, "[E] / ENTER  SELECCIONAR", 1030, 30,
                new Color(0.85f, 0.85f, 0.85f, 1f));

        batch.setColor(Color.WHITE);
    }

    private void rect(SpriteBatch batch, float x, float y, float ancho, float alto, Color color) {

        batch.setColor(color);
        batch.draw(pixel, x, y, ancho, alto);
    }

    // Texto con la esquina izquierda en x y la línea del medio en y
    private void texto(SpriteBatch batch, BitmapFont fuente, String texto,
                       float x, float y, Color color) {

        fuente.setColor(color);
        layout.setText(fuente, texto);
        fuente.draw(batch, layout, x, y + layout.height / 2);
    }

    public void dispose() {

        pixel.dispose();
        fuenteTitulo.dispose();
        fuenteSubtitulo.dispose();
        fuenteBoton.dispose();
        fuentePequena.dispose();

        if (fondo != null) {
            fondo.dispose();
        }

        if (titulo != null) {
            titulo.dispose();
        }
    }
}
