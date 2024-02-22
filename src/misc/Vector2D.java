package misc;

import javafx.geometry.Point2D;

public class Vector2D {
	
	/** The zero vector. */
	public static final Vector2D ZERO = new Vector2D(0, 0);
	
	/** The vector pointing right. */
	public static final Vector2D RIGHT = new Vector2D(1, 0);
	
	/** The vector pointing left. */
	public static final Vector2D LEFT = new Vector2D(-1, 0);
	
	/** The vector pointing DOWN. */
	public static final Vector2D DOWN = new Vector2D(0, 1);
	
	/** The vector pointing up. */
	public static final Vector2D UP = new Vector2D(0, -1);

	/** The x coordinate. */
	public final double x;

	/** The y coordinate. */
	public final double y;
	
	/**
	 * Creates an immutable 2D vector with the specified coordinates.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return Whether this vector is the zero vector.
	 */
	public boolean isZero() {
		return x == 0 && y == 0;
	}
	
	/**
	 * Scales this vector by the given amount.
	 * 
	 * @param scalar The amount by which to scale.
	 * @return The scaled vector.
	 */
	public Vector2D scale(double scalar) {
		return new Vector2D(scalar*x, scalar*y);
	}
	
	/**
	 * @return The normalized version of the vector.
	 */
	public Vector2D normalize() {
		return scale(1/magnitude());
	}
	
	/**
	 * Adds this vector to the given vector.
	 * 
	 * @param vec The vector to sum.
	 * @return The vector sum.
	 */
	public Vector2D add(Vector2D vec) {
		return new Vector2D(x+vec.x, y+vec.y);
	}
	
	/**
	 * Subtracts the given vector from this vector.
	 * 
	 * @param vec The vector to subtract.
	 * @return The difference of the vectors.
	 */
	public Vector2D subtract(Vector2D vec) {
		return add(vec.scale(-1));
	}
	
	/**
	 * @return This vector rotated 90 degrees clockwise.
	 */
	public Vector2D rotate90ClockWise() {
		return new Vector2D(y, -x);
	}
	
	/**
	 * @return This vector rotated 90 degrees counter-clockwise.
	 */
	public Vector2D rotate90CounterClockWise() {
		return new Vector2D(-y, x);
	}
	
	/**
	 * @return The Point2D version of this vector.
	 */
	public Point2D toPoint() {
		return new Point2D(x, y);
	}
	
	/**
	 * Computes the dot product with the given vector.
	 * 
	 * @param vec The vector with which the product will be computed.
	 * @return The dot product with the given vector.
	 */
	public double dot(Vector2D vec) {
		return x*vec.x + y*vec.y;
	}
	
	/**
	 * @return The magnitude of the vector.
	 */
	public double magnitude() {
		return Math.sqrt(dot(this));
	}
	
	/**
	 * @return A unit vector pointing in a random direction.
	 */
	public static Vector2D randomUnit() {
		final double angle = 2*Math.PI*Math.random();
		return new Vector2D(Math.cos(angle), Math.sin(angle));
	}
	
	/**
	 * Creates a vector from the given point.
	 * 
	 * @param point The point from which to create a vector.
	 * @return The created vector.
	 */
	public static Vector2D fromPoint(Point2D point) {
		return new Vector2D(point.getX(), point.getY());
	}
	
	public String toString() {
		return "Vector2D(x: " + x + ", y: " + y + ")";
	}
	
	public boolean equals(Object obj) {
		try {
			final Vector2D vec = (Vector2D) obj;
			if (x == vec.x && y == vec.y)
				return true;
		} catch (ClassCastException e) {}
		return false;
	}
}
