package io.github.some_example_name.apocaba.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.apocaba.objects.Plataforma;
import io.github.some_example_name.apocaba.weapons.Arma;
import io.github.some_example_name.apocaba.weapons.Pistola;
import io.github.some_example_name.apocaba.weapons.Rifle;
import io.github.some_example_name.apocaba.weapons.Trabuco;

import java.util.ArrayList;


public class Jugador {

    // Estados de animación
    private enum Estado { QUIETO, CAMINANDO, SALTANDO }
	                                     //Agregar panel de control para cambiar la velocidad entre cada nivel
	                                     //Hacer las imagenes y sprites de los personajes
	                                     //Poner las plataformas y el piso en otra clase


    // Hojas de sprites (una por animación)
    private Texture texturaIdle;
    private Texture texturaCaminar;
    private Texture texturaSalto;
    private final ArrayList<Arma> armas = new ArrayList<Arma>();
    private int indiceArmaSeleccionada = 0;

    private Animation<TextureRegion> animIdle;
    private Animation<TextureRegion> animCaminar;
    private TextureRegion[] framesSalto;

    private Estado estado = Estado.QUIETO;
    private float tiempoAnimacion = 0;
    private boolean mirandoDerecha = true;

    private float x;
    private float y;

    // Movimiento
    private float velocidad = 500;

    // Salto y gravedad
    private float velocidadY = 0;
    private float gravedad = -1000;
    private float fuerzaSalto = 500;

    // Piso
    private float suelo = 30;
    private boolean enSuelo = true;

    // Tamaño aproximado del jugador
    private float ancho = 128;
    private float alto = 128;

    // Hitbox ligeramente menor que el sprite para que los márgenes transparentes
    // de la imagen no interfieran con pisos, paredes y enemigos.
    private float anchoCuerpo = 96;
    private float altoCuerpo = 112;

    // Monedas recogidas
    private int monedas = 0;

    // Vida
    private int vidaMaxima = 100;
    private int vida = vidaMaxima;

    // Después de recibir daño es invulnerable un rato (el personaje parpadea)
    private float tiempoInvulnerable = 0;
    private float duracionInvulnerabilidad = 0.6f;

    public Jugador() {

        // idle.png y salto.png: 16 cuadros de 256x256
        // caminar.png: 14 cuadros usados de 128x128 (la hoja tiene 16 lugares)
        texturaIdle = new Texture("Personaje/Oscar-quieto.png");
        texturaSalto = new Texture("Personaje/Oscar-salto.png");
        texturaCaminar = new Texture("Personaje/Oscar-caminar.png");
        armas.add(new Pistola());
        armas.add(new Trabuco());
        armas.add(new Rifle());
        animIdle = new Animation<TextureRegion>(0.08f, cortarHoja(texturaIdle, 256, 256, 16));
        animIdle.setPlayMode(Animation.PlayMode.LOOP);

        animCaminar = new Animation<TextureRegion>(0.045f, cortarHoja(texturaCaminar, 128, 128, 14));
        animCaminar.setPlayMode(Animation.PlayMode.LOOP);

        framesSalto = TextureRegion.split(texturaSalto, 256, 256)[0];

        x = 180;
        y = suelo;
    }

    // Corta una hoja horizontal en 'cantidad' cuadros del tamaño indicado
    private Array<TextureRegion> cortarHoja(Texture hoja, int anchoCuadro, int altoCuadro, int cantidad) {

        TextureRegion[] fila = TextureRegion.split(hoja, anchoCuadro, altoCuadro)[0];

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for (int i = 0; i < cantidad; i++) {
            frames.add(fila[i]);
        }

        return frames;
    }

    public void actualizar(float delta, ArrayList<Plataforma> plataformas,
                           float xMinimo, float xMaximo) {

        // =========================
        // MOVIMIENTO
        // =========================

        // Si el personaje murió, ya no responde a los controles
        boolean vivo = estaVivo();

        if (tiempoInvulnerable > 0) {
            tiempoInvulnerable -= delta;
        }

        boolean izquierda = vivo && Gdx.input.isKeyPressed(Input.Keys.A);
        boolean derecha = vivo && Gdx.input.isKeyPressed(Input.Keys.D);

        boolean caminando = false;

        if (izquierda) {
            x -= velocidad * delta;
            mirandoDerecha = false;
            caminando = true;
        }

        if (derecha) {
            x += velocidad * delta;
            mirandoDerecha = true;
            caminando = true;
        }

        // Si se apretan A y D a la vez, no cuenta como caminar
        if (izquierda && derecha) {
            caminando = false;
        }

        // Choque lateral con paredes y otras plataformas sólidas
        chocarLateralConSolidas(plataformas);
        x = MathUtils.clamp(x, xMinimo, xMaximo - ancho);


        // =========================
        // SALTO
        // =========================

        if (vivo && Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && enSuelo) {

            velocidadY = fuerzaSalto;
            enSuelo = false;
        }


        // Guardamos la posición anterior
        float yAnterior = y;

        // Se vuelve a poner en true si toca el piso o una plataforma.
        // Así, si el jugador se cae de una plataforma, cuenta como "en el aire".
        enSuelo = false;


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
        // COLISIÓN CON PLATAFORMAS
        // =========================

        float mitadCuerpo = anchoCuerpo / 2;

        for (Plataforma plataforma : plataformas) {

            float parteSuperiorPlataforma = plataforma.getTecho();

            boolean estaSobreLaPlataforma;

            estaSobreLaPlataforma =
                    getCentroX() + mitadCuerpo > plataforma.getX() &&
                    getCentroX() - mitadCuerpo < plataforma.getX() + plataforma.getAncho();

            boolean estaCayendo = velocidadY <= 0;

            boolean vieneDesdeArriba =
                    yAnterior >= parteSuperiorPlataforma;

            boolean llegoALaPlataforma =
                    y <= parteSuperiorPlataforma;

            // Aterrizar desde arriba
            if (estaCayendo &&
                estaSobreLaPlataforma &&
                vieneDesdeArriba &&
                llegoALaPlataforma) {

                y = parteSuperiorPlataforma;

                velocidadY = 0;

                enSuelo = true;
            }

            // Golpearse la cabeza contra la parte de abajo de una sólida
            if (plataforma.isSolida() &&
                velocidadY > 0 &&
                estaSobreLaPlataforma &&
                yAnterior + altoCuerpo <= plataforma.getY() &&
                y + altoCuerpo > plataforma.getY()) {

                y = plataforma.getY() - altoCuerpo;
                velocidadY = 0;
            }
        }


        // =========================
        // ANIMACIÓN
        // =========================

        Estado nuevoEstado;

        if (!enSuelo) {
            nuevoEstado = Estado.SALTANDO;
        } else if (caminando) {
            nuevoEstado = Estado.CAMINANDO;
        } else {
            nuevoEstado = Estado.QUIETO;
        }

        // Al cambiar de animación, empieza desde el primer cuadro
        if (nuevoEstado != estado) {
            estado = nuevoEstado;
            tiempoAnimacion = 0;
        }

        tiempoAnimacion += delta;
    }

    // Si el cuerpo se metió dentro de una plataforma sólida, lo empuja hacia afuera
    // del lado por el que venía.
    private void chocarLateralConSolidas(ArrayList<Plataforma> plataformas) {

        float mitadCuerpo = anchoCuerpo / 2;

        for (Plataforma p : plataformas) {

            if (!p.isSolida()) {
                continue;
            }

            // El -1 evita que se considere "dentro" cuando está parado arriba
            boolean solapaEnY = y < p.getTecho() - 1 && y + altoCuerpo > p.getY();

            boolean solapaEnX =
                    getCentroX() + mitadCuerpo > p.getX() &&
                    getCentroX() - mitadCuerpo < p.getX() + p.getAncho();

            if (!solapaEnY || !solapaEnX) {
                continue;
            }

            float centroPlataforma = p.getX() + p.getAncho() / 2;

            if (getCentroX() < centroPlataforma) {
                x = p.getX() - mitadCuerpo - ancho / 2;                    // se queda a la izquierda
            } else {
                x = p.getX() + p.getAncho() + mitadCuerpo - ancho / 2;     // se queda a la derecha
            }
        }
    }

    // Orienta al personaje y el arma hacia el mouse en coordenadas del mundo.
    public void mirarHacia(float mundoX, float mundoY) {
        mirandoDerecha = mundoX >= getCentroX();
        getArmaSeleccionada().apuntar(mundoX, mundoY, getCentroX(), y, alto, mirandoDerecha);
    }


    // =========================
    // DIBUJAR PERSONAJE
    // =========================

    public void dibujar(SpriteBatch batch) {

        TextureRegion frame;

        switch (estado) {

            case CAMINANDO:
                frame = animCaminar.getKeyFrame(tiempoAnimacion);
                break;

            case SALTANDO:
                // Los cuadros 0 a 4 son la agachada previa al salto, así que
                // se usan solo del 5 al 11 (subida, punto más alto y caída)
                // según la velocidad vertical: +fuerzaSalto = subiendo,
                // -fuerzaSalto o menos = cayendo.
                float progreso = 1 - (velocidadY + fuerzaSalto) / (2 * fuerzaSalto);
                progreso = MathUtils.clamp(progreso, 0f, 1f);

                int indice = 5 + (int) (progreso * 6.99f);
                frame = framesSalto[indice];
                break;

            default:
                frame = animIdle.getKeyFrame(tiempoAnimacion);
                break;
        }

        // Parpadeo mientras es invulnerable
        if (tiempoInvulnerable > 0 && ((int) (tiempoInvulnerable * 20)) % 2 == 0) {
            batch.setColor(1, 1, 1, 0.4f);
        }

        // Si mira a la izquierda se dibuja espejado (ancho negativo)
        if (mirandoDerecha) {
            batch.draw(frame, x, y, ancho, alto);
        } else {
            batch.draw(frame, x + ancho, y, -ancho, alto);
        }

        getArmaSeleccionada().dibujar(batch);

        batch.setColor(1, 1, 1, 1);
    }

    // =========================
    // VIDA
    // =========================

    // Llamar a este método cuando un zombie (o lo que sea) lastime al jugador
    public void recibirDanio(int danio) {

        if (!estaVivo() || tiempoInvulnerable > 0) {
            return;
        }

        vida = Math.max(0, vida - danio);
        tiempoInvulnerable = duracionInvulnerabilidad;
    }

    public void curar(int cantidad) {
        vida = Math.min(vidaMaxima, vida + cantidad);
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    // =========================
    // MUNICIÓN
    // =========================

    public Arma getArmaSeleccionada() {
        return armas.get(indiceArmaSeleccionada);
    }

    public void seleccionarArma(int indice) {
        if (indice >= 0 && indice < armas.size()) {
            indiceArmaSeleccionada = indice;
        }
    }

    // =========================
    // MONEDAS
    // =========================

    public void sumarMoneda() {
        monedas++;
    }

    public int getMonedas() {
        return monedas;
    }

    // =========================
    // GETTERS
    // =========================

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
        this.velocidadY = 0;
        this.enSuelo = true;
    }

    public void setAlturaSuelo(float alturaSuelo) {
        this.suelo = alturaSuelo;
    }

    public float getAncho() {
        return ancho;
    }

    public float getAlto() {
        return alto;
    }

    public float getAnchoCuerpo() {
        return anchoCuerpo;
    }

    // =========================
    // CHOQUE CON ZOMBIES
    // =========================

    // Si el jugador se metió dentro de un cuerpo (centroX = centro del cuerpo,
    // pies = y del suelo), lo empuja hacia afuera, del lado donde estaba.
    // Si el jugador está más arriba que el cuerpo (saltándolo) no lo toca.
    public void chocarConCuerpo(float centroCuerpoX, float piesCuerpoY,
                                float anchoCuerpoOtro, float altoCuerpoOtro) {

        float mitadJugador = anchoCuerpo / 2;
        float mitadOtro = anchoCuerpoOtro / 2;

        boolean solapaEnX =
                getCentroX() + mitadJugador > centroCuerpoX - mitadOtro &&
                getCentroX() - mitadJugador < centroCuerpoX + mitadOtro;

        boolean solapaEnY =
                y < piesCuerpoY + altoCuerpoOtro &&
                y + altoCuerpo > piesCuerpoY;

        if (!solapaEnX || !solapaEnY) {
            return;
        }

        float centroNuevo;

        if (getCentroX() < centroCuerpoX) {
            centroNuevo = centroCuerpoX - mitadOtro - mitadJugador;   // lo deja a la izquierda
        } else {
            centroNuevo = centroCuerpoX + mitadOtro + mitadJugador;   // lo deja a la derecha
        }

        x = centroNuevo - ancho / 2;
    }

    // Punto desde donde sale la bala: delante de la pistola.
    public float getCentroX() {
        return x + ancho / 2;
    }

    public float getCentroY() {
        return y + alto / 2;
    }

    public void dispose() {

        texturaIdle.dispose();
        texturaCaminar.dispose();
        texturaSalto.dispose();
        for (Arma arma : armas) {
            arma.dispose();
        }
    }
}
