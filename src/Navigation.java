/*
 *  Group 6 - Survival Bot
 *  Navigation Class
 *  Class that helps navigation
 * 
 */

public class Navigation {

	// Util enums

	public enum Direction {
		NORTH, EAST, WEST, SOUTH
	};

	public enum Side {
		RIGHT, LEFT, FRONT, BACK
	};

	// Instance Variables
	private Direction dir;
	private Position pos; // Position of robot.
	private int game_size;

	// Constructor
	public Navigation(Position pos, Direction dir, int size) {
		this.pos = pos;
		this.dir = dir;
		this.game_size = size;
	}

	// Auxiliary function to get the right direction of another direction.

	private Direction right(Direction dir) {
		switch (dir) {
		case NORTH:
			return Direction.EAST;
		case EAST:
			return Direction.SOUTH;
		case WEST:
			return Direction.NORTH;
		case SOUTH:
			return Direction.WEST;
		default:
			return dir;

		}
	}

	// Auxiliary function to get the left direction of another direction.
	private Direction left(Direction dir) {
		switch (dir) {
		case NORTH:
			return Direction.WEST;
		case EAST:
			return Direction.NORTH;
		case WEST:
			return Direction.SOUTH;
		case SOUTH:
			return Direction.EAST;
		default:
			return dir;

		}
	}

	// Saves the new direction (Left of current direction).
	public void rotateLeft() {
		switch (this.dir) {
		case NORTH:
			this.dir = Direction.WEST;
			break;
		case EAST:
			this.dir = Direction.NORTH;
			break;
		case WEST:
			this.dir = Direction.SOUTH;
			break;
		case SOUTH:
			this.dir = Direction.EAST;
			break;

		}
	}

	// Verify if movement is possible (forward or backward)
	public boolean verify(int steps) {
		int new_x = pos.getX(), new_y = pos.getY();
		switch (this.dir) {
		case NORTH:
			new_y += steps;
			break;
		case EAST:
			new_x += steps;
			break;
		case WEST:
			new_x -= steps;
			break;
		case SOUTH:
			new_y -= steps;
			break;

		}
		if (new_x >= 1 && new_x <= game_size && new_y >= 1 && new_y <= game_size) {
			return true;
		}

		return false;

	}

	// Verify if movement its possible, x steps to a side.
	public boolean verify(int steps, Side s) {
		int new_x = pos.getX(), new_y = pos.getY();

		Direction new_dir;

		if (s == Side.RIGHT) {
			new_dir = right(this.dir);
		} else {
			new_dir = left(this.dir);
		}

		switch (new_dir) {
		case NORTH:
			new_y += steps;
			break;
		case EAST:
			new_x += steps;
			break;
		case WEST:
			new_x -= steps;
			break;
		case SOUTH:
			new_y -= steps;
			break;

		}

		if (new_x >= 1 && new_x <= game_size && new_y >= 1 && new_y <= game_size) {
			return true;
		}

		return false;

	}
	// return true if movement is possible and updates position, if not movement is
	// not possible return false.

	// Verify if movement its possible (forward and backwards), if possible saves
	// the position.
	public boolean move(int steps) {
		int new_x = pos.getX(), new_y = pos.getY();
		switch (this.dir) {
		case NORTH:
			new_y += steps;
			break;
		case EAST:
			new_x += steps;
			break;
		case WEST:
			new_x -= steps;
			break;
		case SOUTH:
			new_y -= steps;
			break;

		}

		if (new_x >= 1 && new_x <= game_size && new_y >= 1 && new_y <= game_size) {
			pos.setX(new_x);
			pos.setY(new_y);
			return true;
		}

		return false;

	}

	// Saves the new direction (Right of current direction).
	public void rotateRight() {
		switch (this.dir) {
		case NORTH:
			this.dir = Direction.EAST;
			break;
		case EAST:
			this.dir = Direction.SOUTH;
			break;
		case WEST:
			this.dir = Direction.NORTH;
			break;
		case SOUTH:
			this.dir = Direction.WEST;
			break;

		}
	}

	// Get current direction of robot
	public Direction getDir() {
		return this.dir;
	}

	public Position getPosition() {
		return new Position(pos.getX(), pos.getY());
	}

}
