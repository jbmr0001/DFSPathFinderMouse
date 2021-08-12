package mouserun.mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;
import javafx.util.Pair;
import mouserun.game.Mouse;
import mouserun.game.Grid;
import mouserun.game.Cheese;

/**
 * Clase que contiene el esqueleto del raton base para las prácticas de
 * Inteligencia Artificial del curso 2020-21.
 *
 * 
 */
public class M21B13a extends Mouse {

    /**
     * Tabla hash para almacenar las celdas visitadas por el raton:
     * Clave:Coordenadas Valor: La celda
     */
    private final HashMap<Pair<Integer, Integer>, Grid> celdasVisitadas;
    /**
     * Tabla hash para almacenar las celdas visitadas temporalmente por el ratón:
     * Clave:Coordenadas Valor: La celda.
     * Se reinicia en caso de que el ratón muera, la pila pilaMovimientos esté vacía o no haya ningún movimiento posible.
     */
    private final HashMap<Pair<Integer, Integer>, Grid> celdasTemporales;
    /**
     * Pila para almacenar el camino recorrido.
     */
    private final Stack<Integer> pilaMovimientos;

    /**
     * @brief Constructor del ratón
     */
    public M21B13a() {
        super("Exploracion123");
        celdasVisitadas = new HashMap<>();
        pilaMovimientos = new Stack<>();
        celdasTemporales = new HashMap<>();
    }

    /**
     * @brief Método que devuelve el siguiente movimiento del ratón
     * @param currentGrid Casilla en la que nos encontramos
     * @param cheese Queso actual
     * @return Un entero con el siguiente movimiento
     */
    @Override
    public int move(Grid currentGrid, Cheese cheese) {
        Random random = new Random();//Random creado para las bombas y elegir un possibleMove
        //Tiramos bomba con el random
        if (random.nextInt(50) == 5) {
            return Mouse.BOMB;
        }
        //Añadimos a los mapas la casilla actual en caso de no haberla visitado.
        Pair clave = new Pair(currentGrid.getX(), currentGrid.getY());
        if (!celdasTemporales.containsKey(clave)) {
            celdasTemporales.put(clave, currentGrid);
        }
        if (!celdasVisitadas.containsKey(clave)) {
            //Si se añade al mapa original se incrementan las celdas exploradas
            celdasVisitadas.put(clave, currentGrid);
            incExploredGrids();
        }
         //Metemos los posibles movimientos en la lista
        ArrayList<Integer> possibleMoves = new ArrayList<Integer>();

        if (currentGrid.canGoUp() && !(visitada(currentGrid, UP))) {
            possibleMoves.add(Mouse.UP);
        }
        if (currentGrid.canGoDown() && !(visitada(currentGrid, DOWN))) {
            possibleMoves.add(Mouse.DOWN);
        }
        if (currentGrid.canGoLeft() && !(visitada(currentGrid, LEFT))) {
            possibleMoves.add(Mouse.LEFT);
        }
        if (currentGrid.canGoRight() && !(visitada(currentGrid, RIGHT))) {
            possibleMoves.add(Mouse.RIGHT);
        }
        
        //En caso de que no haya ningun movimiento disponible buscaremos con el hashmap secundario
        if (possibleMoves.isEmpty()) {

            if (currentGrid.canGoUp() && !(visitada2(currentGrid, UP))) {
                possibleMoves.add(Mouse.UP);
            }
            if (currentGrid.canGoDown() && !(visitada2(currentGrid, DOWN))) {
                possibleMoves.add(Mouse.DOWN);
            }
            if (currentGrid.canGoLeft() && !(visitada2(currentGrid, LEFT))) {
                possibleMoves.add(Mouse.LEFT);
            }
            if (currentGrid.canGoRight() && !(visitada2(currentGrid, RIGHT))) {
                possibleMoves.add(Mouse.RIGHT);
            } 
            if (!possibleMoves.isEmpty()) { //usamos un movimiento aleatorio de la lista de posibles movimientos
                int mov = random.nextInt(possibleMoves.size());
                pilaMovimientos.add(possibleMoves.get(mov));
                return possibleMoves.get(mov);
            } //Si ni siquiera con el hashmap secundario hay algún movimiento disponible pasaremos a usar la pila de movimientos ya realizados para salir de ahí
            if (!pilaMovimientos.empty()) {
                return movimientoContrario(pilaMovimientos.pop());
            } else { //En caso de que ni siquiera la pila de movimientos esté llena reiniciamos el mapa para no quedarnos atascados y volvemos a añadir movimientos a la lista
                celdasTemporales.clear();
                if (currentGrid.canGoUp() && !(visitada2(currentGrid, UP))) {
                    possibleMoves.add(Mouse.UP);
                }
                if (currentGrid.canGoDown() && !(visitada2(currentGrid, DOWN))) {
                    possibleMoves.add(Mouse.DOWN);
                }
                if (currentGrid.canGoLeft() && !(visitada2(currentGrid, LEFT))) {
                    possibleMoves.add(Mouse.LEFT);
                }
                if (currentGrid.canGoRight() && !(visitada2(currentGrid, RIGHT))) {
                    possibleMoves.add(Mouse.RIGHT);
                }
                int mov = random.nextInt(possibleMoves.size());
                pilaMovimientos.add(possibleMoves.get(mov));
                return possibleMoves.get(mov);
            }
        } else {
            //En caso de que haya movimientos disponibles en la lista usamos uno aleatorio
            int mov = random.nextInt(possibleMoves.size());
            pilaMovimientos.add(possibleMoves.get(mov));
            return possibleMoves.get(mov);
        }

    }

    /**
     * @brief Función que devuelve el movimiento contrario
     * @param mov Entero con el movimiento
     * @return Un entero con el movimiento opuesto
     */
    public int movimientoContrario(int mov) {
        if (mov == Mouse.UP) {
            mov = DOWN;
        } else if (mov == Mouse.DOWN) {
            mov = UP;
        } else if (mov == Mouse.LEFT) {
            mov = RIGHT;
        } else if (mov == Mouse.RIGHT) {
            mov = LEFT;
        }
        return mov;
    }

    /**
     * @brief Método que se llama cuando aparece un nuevo queso
     */
    @Override
    public void newCheese() {

    }

    /**
     * @brief Método que se llama cuando el raton pisa una bomba
     * reinicia la pila de movimientos y el mapa de las celdas temporales.
     */
    @Override
    public void respawned() {
        pilaMovimientos.clear();
        celdasTemporales.clear();
    }

    /**
     *
     * @brief Método que devuelve si de una casilla dada, está contenida en el
     * mapa de celdasVisitadas
     * @param casilla Casilla que se pasa para saber si ha sido visitada
     * @param direccion Dirección de la casilla visitada
     * @return True Si la casilla vecina que indica la dirección había sido
     * visitada
     */
    public boolean visitada(Grid casilla, int direccion) {
        int x = casilla.getX();
        int y = casilla.getY();

        switch (direccion) {
            case UP:
                y += 1;
                break;

            case DOWN:
                y -= 1;
                break;

            case LEFT:
                x -= 1;
                break;

            case RIGHT:
                x += 1;
                break;
        }
        Pair par = new Pair(x, y);
        return celdasVisitadas.containsKey(par);
    }
    
    /**
     *
     * @brief Método que devuelve si de una casilla dada, está contenida en el
     * mapa de celdasTemporales
     * @param casilla Casilla que se pasa para saber si ha sido visitada
     * @param direccion Dirección de la casilla visitada
     * @return True Si la casilla vecina que indica la dirección había sido
     * visitada
     */
    public boolean visitada2(Grid casilla, int direccion) {
        int x = casilla.getX();
        int y = casilla.getY();

        switch (direccion) {
            case UP:
                y += 1;
                break;

            case DOWN:
                y -= 1;
                break;

            case LEFT:
                x -= 1;
                break;

            case RIGHT:
                x += 1;
                break;
        }
        Pair par = new Pair(x, y);
        return celdasTemporales.containsKey(par);
    }

}
