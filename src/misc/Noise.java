package misc;

public class Noise {
	
	/** Stores the gradients. */
	private static final Vector2D[] GRADS = new Vector2D[10000];
	
	static {
		// Initializes the gradients
		for (int i = 0; i < GRADS.length; i++)
			GRADS[i] = new Vector2D(2*Math.random()-1, 2*Math.random()-1);
	}
	
	/**
	 * @param x The x coordinate of the gradient.
	 * @param y The y coordinate of the gradient.
	 * @param maxX The maximum number of gradients along the x direction.
	 * 
	 * @return The gradient at the given position.
	 */
	private static Vector2D getGrad(int x, int y, int maxX) {
		return GRADS[(y*maxX + x)%GRADS.length];
	}
	
	/**
	 * Computes the noise at the given 2D point.
	 * 
	 * @param pos The point at which the interpolation will happen.
	 * @param maxX The maximum number of gradients along the x direction.
	 * 
	 * @return The noise at the given point.
	 */
	public static double noise(Vector2D pos, int maxX) {
		
		// Calculates the integer coordinates
		final int xInt = (int)pos.x;
		final int yInt = (int)pos.y;
		
		// Calculates the corner gradients
		/*  
		 * g1 | g2
		 * -------
		 * g3 | g4  
		 */
		final Vector2D g1 = getGrad(xInt, yInt, maxX);
		final Vector2D g2 = getGrad(xInt+1, yInt, maxX);
		final Vector2D g3 = getGrad(xInt, yInt+1, maxX);
		final Vector2D g4 = getGrad(xInt+1, yInt+1, maxX);
		
		// Stores the other relevant vectors
		final Vector2D g1ToPos = new Vector2D(pos.x-(xInt), pos.y-(yInt));
		final Vector2D g2ToPos = new Vector2D(pos.x-(xInt+1), pos.y-(yInt));
		final Vector2D g3ToPos = new Vector2D(pos.x-(xInt), pos.y-(yInt+1));
		final Vector2D g4ToPos = new Vector2D(pos.x-(xInt+1), pos.y-(yInt+1));
		
		// Interpolates
		final double lerp1 = lerp(g1.dot(g1ToPos), g2.dot(g2ToPos), g1ToPos.x);
		final double lerp2 = lerp(g3.dot(g3ToPos), g4.dot(g4ToPos), g1ToPos.x);
		return lerp(lerp1, lerp2, g1ToPos.y);
	}
	
	/**
	 * Computes the noise at the given 1D point.
	 * 
	 * @param x The x coordinate at which noise will be computed.
	 * 
	 * @return The noise at the given point.
	 */
	public static double noise(double x) {
		
		// Calculates the boundary gradients
		final int xInt = (int)x;
		final Vector2D g1 = getGrad(xInt, 0, 0);
		final Vector2D g2 = getGrad(xInt+1, 0, 0);
		x -= xInt;
		
		return lerp(g1.x*x, g2.x*(1-x), x);
	}
	
	/**
	 * Interpolates the points.
	 * 
	 * @param val1 The first value to interpolate.
	 * @param val2 The second value to interpolate.
	 * @param t The value at which to interpolate in [0,1].
	 * @return The interpolated value.
	 */
	private static double lerp(double val1, double val2, double t) {
		
		// Calculates the fade function value
		final double fade = t*t*t*(10 + t*(-15 + 6*t));
		
		return (1-fade)*val1 + fade*val2;
	}
}
