package mainMod;
import java.awt.Point;
import java.io.Serializable;

/**
 * This class is in charge of creating an Invincibility object which can be randomly 
 * spawned on a tile in the grid..
 */
public class Invincibility extends Item implements Serializable {

    /**
     * This field represents the position of {@link Invincibility} on the grid.
     */
    private Point invPosition;

    /**
     * The {@link Invincibility} constructor which allows the {@link Radar}
     * {@link Item} to be randomly spawned in
     * the grid where there is a {@link Tile} object.
     */
    public Invincibility(Point p) {
        super(p);
        invPosition = p;
        this.exists = true;
    }

    /**
     * This method gets the {@link Invincibility}s location
     * @return Point {@link #invPosition}
     */
    public Point getPos()
    {
        return invPosition;
    }

    /**
     * This method returns a char value of {@code I} on the grid to represent 
     * the location of the invincibility item.
     * 
     * @return char {@code I}
     */
    public char returnSymbol()
    {
        return 'I';
    }

    /**
     * Overloaded method of {@link #returnSymbol()} Checks if debug mode is on and
     * returns {@code I} if it is {@code true} and {@code /} if {@code false}
     * @param debug {@link GameEngine#debug}
     * @return char representing object
     */
    public char returnSymbol(boolean debug) {return debug?'I':'/';}

    /**
     * This method uses the invincibility item. Calls the
     * {@link GameEngine#invincibilityOn()} method to set
     * {@link GameEngine#isInvincible} to {@code true}
     */
    public void use() {
        GameEngine.invincibilityOn();
        System.out.println("You are now INVINCIBLE");
        this.exists = false;
    }
}
