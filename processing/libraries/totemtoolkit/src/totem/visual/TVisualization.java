package totem.visual;

/**
 * Interface for sound visualizations. Defines methods concerning beat detection that
 * can be used to visualize sound output. The draw method returns the results. 
 * @author alex
 *
 */
public interface TVisualization{
	/**
	 * Visualize detected beats with lower pitch.
	 */
	public void kick();
	/**
	 * Visualize detected beats with high pitch.
	 */
	public void hat();
	/**
	 * Visualize detected beats with medium pitch.
	 */
	public void snare();
	/**
	 * Draws visualization to the main application.
	 */
	public void draw();
}
