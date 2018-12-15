
public class Navigation {
	
	public enum Direction {NORTH,EAST,WEST,SOUTH};
	
	
	
	private Direction dir;
	private Position pos; // Position of robot.
	private int game_size;
	

	public Navigation(Position pos, Direction dir, int size) {
		this.pos = pos;
		this.dir = dir;
		this.game_size = size;
	}
	
	public void rotateLeft() 
	{
		switch(this.dir)
		{
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
	
	// return true if movement is possible and updates position, if not movement is not possible return false.
	public boolean move(int steps)
	{
		int new_x = pos.getX(), new_y = pos.getY();
		switch(this.dir)
		{
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
		
		if(new_x >=1 && new_x <= game_size && new_y >= 1 && new_y <= game_size)
		{
			pos.setX(new_x);
			pos.setY(new_y);
			return true;
		}
			
		return false;
		
	}
	
	public void rotateRight()
	{
		switch(this.dir)
		{
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
	
	public Direction getDir()
	{
		return this.dir;
	}

}
