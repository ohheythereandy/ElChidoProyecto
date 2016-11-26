package mainMod;
import java.util.*;
import java.awt.Point;

/**
 * This class is in charge of handling all the game logic in the game.
 */
public class GameEngine {

    /**
     * This is an array of {@link Point}s that stores the
     * locations of each of the north facing {@link Tile}s
     * of the {@link Room}s
     */
    private static Point [] enterRoom = {
            new Point(0,1), new Point(0,3), new Point(0,5),
            new Point(3,1), new Point(3,3), new Point(3,5),
            new Point(6,1), new Point(6,3), new Point(6,5)}; // make private and make getter

    /**
     * this field represents the location of the {@link Player}
     */
    private static Point position = new Point(mainMod.Math.toMapX(0), mainMod.Math.toMapY(0));

    /**
     * This field represents the grid of the game. Instantiates a new object of type Grid.
     */
    private Grid board = new Grid();

    /**
     * This field represent a Player object that presents that player in the the game.
     */
    private Player player;

    /**
     * This field represents an array of Enemy objects
     */
    private Enemy[] enemies = new Enemy[6];

    /**
     * This field holds the position of the {@link Enemy}s
     * as an array of {@link Point}s
     */
    private Point[] listOfEnemyLoc = new Point[6];

    private Item[] items = new Item[3];

    private Point[] listOfItemLoc = new Point[3];

    /**
     * This field holds the position of the {@link Room}s
     * as an array of {@link Point}s
     */
    private Point[] rooms = {new Point(1, 1), new Point(1, 4), new Point(1, 7), new Point(4, 1), new Point(4, 4), new Point(4, 7), new Point(7, 1), new Point(7, 4), new Point(7, 7)};

    /**
     * This field represents the Random object used to randomly generate numbers.
     */
    Random rand = new Random();

    /**
     * This field stores if debug mode is on or off. Modified by {@link #changeDebug(boolean)}
     */
    public static boolean debug;

    /**
     * This boolean field tells us whether
     * the {@link Player} is invinsible
     */
    private static boolean isInvincible = false;

    /**
     * This field is the counter that stores the
     * invincibility for the {@link Player}
     */
    private static int invCounter = 0;

    /**
     * This field stores the mmo of the
     * {@link Player}
     */
    private static int playerAmmo = 1;

    private static boolean radar;

    /**
     * This field
     */
    private static boolean gameWon =  false;

    private Point roomplace = new Point();


    /**
     * This enumerated field creates the values
     * UP,DOWN,LEFT,RIGHT.
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, SAVE
    }

    /**
     * This is the constructor for the {@link GameEngine}
     * it instantiates the {@link GameEngine#player},
     * calls the {@link GameEngine#setPlayer()} method,
     * the {@link GameEngine#generateEnemies()} method,
     * and the {@link GameEngine#generateItems()} method.
     * Finally it sets the value of {@link GameEngine#debug}
     * to false;
     *
     */
    public GameEngine(){

        this.player = new Player(new Point(8, 0));
        setPlayer();
        generateEnemies();
        generateItems();
        generateBriefcase();
        debug = false;
    }

    /**
     * This method toggles the value of the boolean
     * field {@link GameEngine#debug} to the value passed
     * as a parameter, (either True or False).
     *
     * @param state
     */
    public void changeDebug(boolean state){
        debug = state;
    }

    /**
     * This abstract method will allow the
     * {@link Entity} to take a turn
     */
    public void taketurn(){
        int invCounter = 0;

        if(player.getNumOfLives() >= 1)
        {
            if(invincibilityOn())
                invCounter++;
            playerTurn();
            checkPos(player.getPos());
            timeDelay(1000);
            allEnemiesTurn();
            board.printGrid(debug);
            System.out.println("LIVES: " + player.getNumOfLives() + " AMMO: " + playerAmmo);
            if(invCounter >= 5)
                System.out.println("Invincibility wore off!");
                isInvincible = false;
        }
        else
            gameOver();
    }

    /**
     * This method shoots the {@link Enemy}
     * in the {@link GameEngine.Direction}
     * that the {@link Player} chooses
     *
     * @param dir
     */
    public void shoot(Direction dir) {

        Point p = player.getPos();

        for (int i = 0; i < board.map.length; i++) {

            if (dir == Direction.UP) {
                if (!board.isOOB(p.x - i, p.y)) {
                	if(board.map[p.x - i][p.y].isRoom()) {
                		System.out.println("you hit the room");
                		break;
                	}
                	else if(board.map[p.x - i][p.y].hasEnemy()) {
                        board.map[p.x - i][p.y].killEnemy();
                        break;
                	}
                }
            }
            else if (dir == Direction.DOWN) {
                if (!board.isOOB(p.x + i, p.y)) {
                	if(board.map[p.x + i][p.y].isRoom()) {
                		System.out.println("you hit the room");
                		break;
                	}
                	else if(board.map[p.x + i][p.y].hasEnemy()) {
                        board.map[p.x + i][p.y].killEnemy();
                        break;
                	}
                }
            }
            else if (dir == Direction.RIGHT) {
                if (!board.isOOB(p.x, p.y + i)) {
                	if(board.map[p.x][p.y + i].isRoom()) {
                		System.out.println("you hit the room");
                		break;
                	}
                	else if(board.map[p.x][p.y + i].hasEnemy()) {
                        board.map[p.x][p.y + i].killEnemy();
                        break;
                	}
                }
            }
            else if (dir == Direction.LEFT) {
                if (!board.isOOB(p.x, p.y - i)) {
                	if(board.map[p.x][p.y - i].isRoom()) {
                		System.out.println("you hit the room");
                		break;
                	}
                	else if(board.map[p.x][p.y - i].hasEnemy()) {
                        board.map[p.x][p.y - i].killEnemy();
                        break;
                	}
                }
            }
        }
        playerAmmo--;
    }

    /**
     *
     * @return
     */
    boolean gameWon(){
        return gameWon;
    }

    /**
     * This method represents the turn of the main {@link Player} of the game.
     */
    public void playerTurn() {
        Direction direction;
        Point pPos = player.getPos();

        look(UI.lookPrompt());
        int entry = UI.moveOrShootPrompt();

        if(entry == 1)
        {
            while(board.validMove(pPos, direction=UI.movePrompt())){
                timeDelay(1000);
                switch (direction) {
                    case UP:
                        player.moveUp();
                        moveUp(pPos);
                        checkPos(pPos);
                        break;
                    case DOWN:
                        if (board.getTile(player.getPos()).isRoom()) gameWon = true;
                        else {
                            player.moveDown();
                            moveDown(pPos);
                            checkPos(pPos);
                        }
                        break;
                    case LEFT:
                        player.moveLeft();
                        moveLeft(pPos);
                        checkPos(pPos);
                        break;
                    case RIGHT:
                        player.moveRight();
                        moveRight(pPos);
                        checkPos(pPos);
                        break;
                }
                break;
            }
        }
        else if(entry == 2){
            direction=UI.shootPrompt();
            shoot(direction);
        }
    }

    /**
     * This method takes the {@link Enemy}'s
     * turn.
     *
     * @param ePos
     */
    public void enemyTurn(Point ePos){
        enemyAttack(ePos);
        enemyMove(ePos);
    }

    /**
     * This method takes an {@link Point}
     * for the {@link Enemy} position and
     * checks to see if there is a {@link Player}
     * adjacent to attack. If so,
     * it kills the {@link Player}
     *
     * @param ePos
     */
    public void enemyAttack(Point ePos) {
        //Attack
        if(!board.isOOB(ePos.x - 1, ePos.y)) {
            if(board.getTile(ePos.x - 1, ePos.y).hasPlayer()) {
                player.decLives();
                System.out.println("You ded");
                respawnPlayer();
                return;
            }
        }
        if(!board.isOOB(ePos.x + 1, ePos.y)) {
            if (board.getTile(ePos.x + 1, ePos.y).hasPlayer()) {
                player.decLives();
                System.out.println("You ded");
                respawnPlayer();
                return;
            }
        }
        if(!board.isOOB(ePos.x, ePos.y + 1)) {
            if(board.getTile(ePos.x, ePos.y + 1).hasPlayer()) {
                player.decLives();
                System.out.println("You ded");
                respawnPlayer();
                return;
            }
        }
        if(!board.isOOB(ePos.x, ePos.y - 1)) {
            if(board.getTile(ePos.x, ePos.y - 1).hasPlayer()) {
                player.decLives();
                System.out.println("You ded");
                respawnPlayer();
                return;
            }
        }

    }

    /**
     * This method makes the {@link Enemy}
     * move in a random {@link Direction}
     *
     * @param ePos
     */
    public void enemyMove(Point ePos) {
        Direction movement;

        //Move
        while(board.validMove(ePos,movement = rollMove())) {
            switch (movement) {
                case UP:
                    board.getTile(ePos.x, ePos.y).getEnemy().moveUp();
                    moveUp(ePos);
                    break;
                case DOWN:
                    board.getTile(ePos.x, ePos.y).getEnemy().moveDown();
                    moveDown(ePos);
                    break;
                case LEFT:
                    board.getTile(ePos.x, ePos.y).getEnemy().moveLeft();
                    moveLeft(ePos);
                    break;
                case RIGHT:
                    board.getTile(ePos.x, ePos.y).getEnemy().moveRight();
                    moveRight(ePos);
                    break;
            }
            break;
        }

    }

    /**
     * Respawns player back to starting location. [8, 0]
     */
    public void respawnPlayer() {
    	board.swapTile(board.getTile(player.getPos()), board.getTile(new Point(8,0)));
        player.setPos(new Point(8,0),0,0);

    }

    /**
     * This method causes the enemies at each {@link Point} in the array {@link #listOfEnemyLoc}
     * to take an {@link #enemyTurn(Point)}
     */
    public void allEnemiesTurn(){
//        for(int i = 0; i>listOfEnemyLoc.length;i++){
//            enemyTurn(listOfEnemyLoc[i]);
//        }
        for(int i= 0; i<board.getRowLen();i++){
            for(int j=0;j<board.getColLen();j++){
                if(board.getTile(i,j).hasEnemy()){
                    enemyTurn(new Point(i,j));
                }
            }
        }

    }

    /**
     * This method swaps the tile at {@link Entity}'s pt with the tile going up
     * provided it is valid.
     * @param pt
     */
    public void moveUp(Point pt){
        if(!board.isOOB(pt.x-1,pt.y)) {
            if(board.checkTile(board.getTile(pt.x-1,pt.y)))
            {
               useItem(board.getTile(pt.x-1,pt.y).getItem());
                board.getTile(pt.x-1,pt.y).setItem();
            }
            board.swapTile(board.getTile(pt.x, pt.y), board.getTile(pt.x - 1, pt.y));
            pt.translate(-1,0);
        }
    }

    /**
     * This method swaps the tile at {@link Entity}'s pt with the tile going down
     * provided it is valid.
     * @param pt
     */
    public void moveDown(Point pt){
        if(!board.isOOB(pt.x+1,pt.y)) {
            if(board.checkTile(board.getTile(pt.x+1,pt.y)))
            {
                useItem(board.getTile(pt.x+1,pt.y).getItem());
                board.getTile(pt.x+1,pt.y).setItem();
            }
            board.swapTile(board.getTile(pt.x, pt.y), board.getTile(pt.x + 1, pt.y));
            pt.translate(1,0);
        }
    }

    /**
     * This method swaps the tile at {@link Entity}'s pt with the tile going left
     * provided it is valid.
     * @param pt
     */
    public void moveLeft(Point pt){
        if(!board.isOOB(pt.x,pt.y-1)) {
            if(board.checkTile(board.getTile(pt.x,pt.y - 1)))
            {
                useItem(board.getTile(pt.x,pt.y - 1).getItem());
                board.getTile(pt.x,pt.y-1).setItem();
            }
            board.swapTile(board.getTile(pt.x, pt.y), board.getTile(pt.x, pt.y - 1));
            pt.translate(0,-1);
        }
    }

    /**
     * This method swaps the tile at {@link Entity}'s pt with the tile going right
     * provided it is valid.
     * @param pt
     */
    public void moveRight(Point pt){
        if(!board.isOOB(pt.x,pt.y+1)) {
            if(board.checkTile(board.getTile(pt.x,pt.y+1)))
            {
                useItem(board.getTile(pt.x,pt.y+1).getItem());
                board.getTile(pt.x,pt.y+1).setItem();
            }
            board.swapTile(board.getTile(pt.x, pt.y), board.getTile(pt.x, pt.y + 1));
            pt.translate(0,1);
        }
    }

    /**
     * This method rolls a random number from 0 - 3
     * and returns an enum {@link Direction}
     *
     * @return
     */
    public Direction rollMove() {
        int enemyMove;
        Random rand = new Random();
        enemyMove = rand.nextInt(3);

        switch (enemyMove) {
            case 0:
                return Direction.UP;
            case 1:
                return Direction.DOWN;
            case 2:
                return Direction.LEFT;
            case 3:
                return Direction.RIGHT;
        }
        return Direction.UP;
    }

    /**
     * This method takes a {@link Direction}
     * and translates two points in that
     * {@link Direction}
     * @param direction
     */
    public void look(Direction direction){
        Point A  = player.getPos();
        Point B = player.getPos();
        switch(direction){
            case UP: A.translate(-1,0);
                B.translate(-2,0);
                break;
            case DOWN:  A.translate(1, 0);
                B.translate(2, 0);
                break;
            case LEFT:  A.translate(0, -1);
                B.translate(0, -2);
                break;
            case RIGHT:  A.translate(0,1);
                B.translate(0,2);
                break;
            case SAVE: //SaveEngine.writeSave(player); uncomment to save
                break;
        }
        board.printlookGrid(A,B, debug);
    }

    /**
     * This method spawns the player object at the default starting point of the grid (bottom left corner).
     */
    public void setPlayer()
    {
        board.getTile(8, 0).insertPlayer(this.player);
    }

    /**
     * This method is in charge of randomly generating enemies on an empty space of the map denoted by the "/" symbol. If the space
     */

    private boolean checkSpawn(int n, int m) {
       if(n==0 && (m==5 || m ==6 || m==7))
           return false;
       else if(n==1 && (m==6 || m==7 || m==8))
            return false;
       else if(n==2 && (m==7 || m ==8))
           return false;
        else if(n==3 && n==8)
            return false;

        return true;

    }

    public void generateEnemies(){
        int num1, num2;
        Point enemyloc;
        Enemy enemyholder;
        int i = 0;


        board.map[1][1].returnSymbol(debug);

        while(i < 6)
        {
            num1 = rand.nextInt(8);
            num2 = rand.nextInt(8);


            if(board.map[num1][num2].isEmpty() && checkSpawn(num1, num2))
            {
                enemyloc = new Point(num1, num2);
                enemyholder = new Enemy(enemyloc);
                enemies[i] = new Enemy(enemyloc);
                board.getTile(num1, num2).insertEnemy(enemies[i]);
                listOfEnemyLoc[i]=enemyloc;
                i++;
                System.out.println("DUMBSHIT AT" + enemyloc);
            }

            else if(!checkSpawn(num1,num2)){
                //andy is cool
                //sean is bad at chess
            }
        }
    }

    /**
     * This method places {@link Ammo}, {@link Invincibility}, and {@link Radar} power ups on the map. Places a power up on
     * random locations that are empty on the map denoted by a "/" Symbol and if the space on the map are not empty it will
     * keep generating numbers until a space is empty. Once
     */
    public void generateItems(){
        int num1, num2;
        boolean playerAmmoPlace = false, invPlace = false, radarPlace = false;
        Point itemloc;
        Item itemholder;
        int i = 0;
        while(i != 3)
        {
            num1 = rand.nextInt(8);
            num2 = rand.nextInt(8);

            if(board.map[num1][num2].isEmpty())
            {
                if(playerAmmoPlace == false)
                {
                    itemloc = new Point(num1,num2);
                    itemholder = new Ammo(itemloc);
                    board.getTile(num1,num2).insertItem(itemholder);
                    listOfItemLoc[i] = itemloc;
                    playerAmmoPlace = true;
                    i++;
                }
                else if(invPlace == false)
                {
                    itemloc = new Point(num1,num2);
                    itemholder = new Invincibility(itemloc);
                    board.getTile(num1,num2).insertItem(itemholder);
                    listOfItemLoc[i] = itemloc;
                    invPlace = true;
                    i++;
                }
                else if(radarPlace == false)
                {
                    itemloc = new Point(num1,num2);
                    itemholder = new Radar(itemloc);
                    board.getTile(num1,num2).insertItem(itemholder);
                    listOfItemLoc[i] = itemloc;
                    radarPlace = true;
                    i++;
                }
            }
        }
    }

    private void generateBriefcase() {
        int r = rand.nextInt(9);
        board.getRoom(rooms[r]).setBriefcase(true);
        System.out.print("Placed at " + rooms[r]);

    }

    /**
     * This method prints the {@link GameEngine#board}
     * to the screen (For Debug mode)
     */
    public void printBoard()
    {
        this.board.printGrid(debug);
    }

    /**
     * This method sets the {@link GameEngine#isInvincible}
     */
    public static boolean invincibilityOn() {
        isInvincible = true;
        return isInvincible;
    }

    /**
     * This method increments the {@link GameEngine#playerAmmo}
     * by 1
     */
    public static void addAmmo() {
        playerAmmo = 1;
    }

    /**
     * This function checks to see if the
     * {@link GameEngine#playerAmmo} is empty
     *
     * @return ans
     */
    public static boolean playerAmmoEmpty(){
        boolean ans;
        ans = (playerAmmo <= 0)? true : false;
        return ans;
    }

    /**
     * This method sets the value of
     * {@link GameEngine#radar} to true;
     */
    public static void radarOn() {
        radar = true;
    }

    /**
     * This method gets the {@link Player}'s {@link GameEngine#position}
     *
     * @return position
     */
    public static Point getPos()
    {
        return position;
    }

    /**
     * This method sets the {@link Player}'s {@link GameEngine#position}
     */
    public void setPos(Point p) {
        if(!board.isOOB(p.x, p.y)) {
            position = p;
        }

    }

    /**
     * This method returns the {@link Player}
     * @return
     */
    public Player getPlayer(){
        return player;
    }

    /**
     * This method goes through the list of items
     * checking if the item still exists.
     * If so, checks if the position is the same as
     * the {@link Player}s
     * If the position is the same, it uses the {@link Item}
     */
    public void checkPos(Point playerposition) {
        if(board.getTile(playerposition).hasItem()) {
            System.out.println("It sees the item");
            useItem(board.getTile(playerposition).getItem());
        }
    }

    /**
     * This method uses the {@link Item}
     *
     * @param i
     */
    public void useItem(Item i)
    {
        i.use();
    }

    /**
     * This method sets the {@link GameEngine#gameWon}
     * equal to True;
     */
    public void setGameWon(){
        gameWon=true;
    }

    /**
     * This method returns a boolean value of {@code false} representing the game is over.
     * @return false.
     */
    public boolean gameOver(){
       if(player.getNumOfLives() == 0) {
           return true;
       }
       else if(player.getNumOfLives() != 0){
           return false;
       }
       else if (gameWon()) {
           return true;
       }
       else return false;
    }

    /**
     * This method pauses the program for the amount
     * of milliseconds passed into the function.
     * @param a
     */
    public void timeDelay(int a) {
        try {
            System.out.println(". . .");
            Thread.sleep(a);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
