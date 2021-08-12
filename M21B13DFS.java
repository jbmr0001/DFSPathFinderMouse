package mouserun.mouse;


import static java.lang.Math.random;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;
import java.util.Queue;
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
public class M21B13DFS extends Mouse {
  
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
     * Tabla hash para almacenar las celdas visitadas por el DFS:
     * Clave:Coordenadas Valor: La celda
     */
    private final HashMap<Pair<Integer, Integer>, Grid> celdasVisitadasDFS;
   
    /**
     * Pila para almacenar el camino recorrido.
     */
    private final Stack<Integer> pilaMovimientos;
    
    /**
     * Lista para almacenar los movimientos del camino encontrado con el DFS en su procesamiento.
     */
    private final LinkedList<Integer> movimientosBusqueda;
    
    /**
     * Lista para almacenar los movimientos del camino encontrado con el DFS para su uso en el move.
     */
    private final LinkedList<Integer> movimientosBusquedaAux;
    
    /**
     * Boolean que indica si ha respawneado un nuevo queso.
     */
    boolean quesoCambiado=false;
    
    /**
     * Boolean que indica si se ha encontrado el queso con el dfs.
     */
    boolean encontrado=false;
    
    
    /**
     * @brief Constructor del ratón
     */
    public M21B13DFS() {
        super("DFS");
        celdasVisitadas = new HashMap<>();
        celdasVisitadasDFS = new HashMap<>();
        celdasTemporales = new HashMap<>();
        pilaMovimientos = new Stack<>();
        movimientosBusqueda = new LinkedList<>();
        movimientosBusquedaAux = new LinkedList<>();
    }

    /**
     * @brief Método que devuelve el siguiente movimiento del ratón
     * @param currentGrid Casilla en la que nos encontramos
     * @param cheese Queso actual
     * @return Un entero con el siguiente movimiento
     */
    @Override
    public int move(Grid currentGrid, Cheese cheese){
                     
        //incluir celdas si no las conozco
        Pair clave = new Pair(currentGrid.getX(), currentGrid.getY());
        if (!celdasTemporales.containsKey(clave)) {
            celdasTemporales.put(clave, currentGrid);
        }
        if (!celdasVisitadas.containsKey(clave)) {//Añadimos la casilla a visitadas si nola hemos visitado
            celdasVisitadas.put(clave, currentGrid);
            incExploredGrids();
        }
        
        Pair queso = new Pair(cheese.getX(), cheese.getY());
        if (celdasVisitadas.containsKey(queso)) {//posición del queso conocida
            if(quesoCambiado){//Si hay un queso nuevo
                quesoCambiado=false;
                //limpiamos lista y mapa
                celdasVisitadasDFS.clear();
                movimientosBusqueda.clear();
                
                dfs(currentGrid,cheese,0);//realizamos búsqueda
                //devolvemos el último movimiento de la lista
                return movimientosBusquedaAux.removeLast();
             
            }else{//Si no hay un queso nuevo
                    //si la lista no está vacía devolvemos el último movimiento
                    if(!movimientosBusquedaAux.isEmpty()){
                        return movimientosBusquedaAux.removeLast();
                    }
                    //en caso de estar vacía la lista volvemos a explorar
                    return explora(currentGrid);//explorará si el dfs no ha llegado al queso
            }
        
        }else{//posición del queso no conocida
            
            return explora(currentGrid);//seguimos explorando
        }
                       
    }

    
    /**
     * @brief Función que devuelve el movimiento contrario
     * @param mov Entero con el movimiento
     * @return Un entero con el movimiento opuesto
     */
   public int movimientoContrario(int mov){
       if(mov==Mouse.UP){
           mov=DOWN;
       }else if(mov==Mouse.DOWN){
               mov=UP;
       }else if(mov==Mouse.LEFT){
               mov=RIGHT;
       }else if(mov==Mouse.RIGHT) {
               mov=LEFT;
       }
           return mov;
   }
    
    /**
     * @brief Método que se llama cuando aparece un nuevo queso
     */
    @Override
    public void newCheese() {
            quesoCambiado=true;
            encontrado=false;
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
     * @brief Algoritmo de búsqueda primero en profundidad recursivo
     * @param actual Casilla actual
     * @param cheese El queso a buscar
     * @param mov Entero con el movimiento para llegar a la casilla actual
     */
    private void dfs(Grid actual, Cheese cheese,int mov) {
     
        ArrayList<Grid> lista=new ArrayList();
        ArrayList<Integer> listaMov=new ArrayList();
        
        Pair clave = new Pair(actual.getX(), actual.getY());
        //Añadimos el movimiento que nos llevó a aquí a la lista
        movimientosBusqueda.push(mov);
        //si hemos llegado al queso se añaden los movimientos a la lista que usaremos realmente
        if(actual.getX()==cheese.getX()&&actual.getY()==cheese.getY()){
            encontrado=true; 
            movimientosBusquedaAux.addAll(movimientosBusqueda);
        }
         
        celdasVisitadasDFS.put(clave,actual);//marcamos como visitado
         
        Pair clav;
        //Si no se ha encontrado el queso calculamos las celdas adyacentes a esta celda
        //Solo podrá avanzar a celdas exploradas y no  visitadas en el DFS
        if(!encontrado){
            if(actual.canGoUp()){
                clav = new Pair(actual.getX(), actual.getY()+1);
                if(celdasVisitadas.containsKey(clav)&&!celdasVisitadasDFS.containsKey(clav)){
                    lista.add(celdasVisitadas.get(clav));
                    listaMov.add(UP);
                }
            }
            if(actual.canGoDown()){
                clav = new Pair(actual.getX(), actual.getY()-1);
                if(celdasVisitadas.containsKey(clav)&&!celdasVisitadasDFS.containsKey(clav)){
                    lista.add(celdasVisitadas.get(clav));
                    listaMov.add(DOWN);
                }
            }
            if(actual.canGoRight()){
            clav = new Pair(actual.getX()+1, actual.getY());
                if(celdasVisitadas.containsKey(clav)&&!celdasVisitadasDFS.containsKey(clav)){
                    lista.add(celdasVisitadas.get(clav));
                    listaMov.add(RIGHT);
                }
            }
            if(actual.canGoLeft()){
                clav = new Pair(actual.getX()-1, actual.getY());
                    if(celdasVisitadas.containsKey(clav)&&!celdasVisitadasDFS.containsKey(clav)){
                        lista.add(celdasVisitadas.get(clav));
                        listaMov.add(LEFT);
                    }
            }
            //con la lista de celdas a visitar realizamos un for 
            for(int i=0;i<lista.size();i++){  
                clav = new Pair(lista.get(i).getX(), lista.get(i).getY());
                if(!celdasVisitadasDFS.containsKey(clav)){// y si la celda si no está visitado
                    dfs(lista.get(i),cheese,listaMov.get(i)); //se realiza el dfs desde esa celda con el movimiento que se llega a ella
                }
            } //en caso de que no haya servido para llegar al queso se saca ese movimiento para descartar ese hilo de movimientos
            movimientosBusqueda.pop();
            
        }
            
    }
    
    /**
     * @brief Método que explora el mapeado
     * @param currentGrid Casilla en la que nos encontramos
     * @return Un entero con un siguiente movimiento
     */
    private Integer explora(Grid currentGrid) {
        
        Random random = new Random();//Random creado para las bombas y elegir un possibleMove
        //Tiramos bomba con el random
        if (random.nextInt(50) == 5) {
            return Mouse.BOMB;
        }

        ArrayList<Integer> possibleMoves = new ArrayList<Integer>();
        //Metemos los posibles movimientos en la lista
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
            if (!possibleMoves.isEmpty()) {
                int mov = random.nextInt(possibleMoves.size());
                pilaMovimientos.add(possibleMoves.get(mov));
                return possibleMoves.get(mov);
            }
            //Si ni siquiera con el hashmap secundario hay algún movimiento disponible pasaremos a usar la pila de movimientos ya realizados para salir de ahí
            if (!pilaMovimientos.empty()) {
                return movimientoContrario(pilaMovimientos.pop());
            } else {//En caso de que ni siquiera la pila de movimientos esté llena reiniciamos el mapa para no quedarnos atascados y volvemos a añadir movimientos a la lista
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
