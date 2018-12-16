
public class Navigation {
	
	public enum Direction {NORTH,EAST,WEST,SOUTH};
	public enum Side {RIGHT, LEFT};
	
	
	private Direction dir;
	private Position pos; // Position of robot.
	private int game_size;
	

	public Navigation(Position pos, Direction dir, int size) {
		this.pos = pos;
		this.dir = dir;
		this.game_size = size;
	}
	
	
	private Direction right(Direction dir)
	{
		switch(dir)
		{
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
	
	
	private Direction left(Direction dir)
	{
		switch(dir)
		{
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
	
	public boolean verify(int steps)
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
			return true;
		}
			
		return false;
		
	}
	
	public boolean verify(int steps,Side s)
	{
		int new_x = pos.getX(), new_y = pos.getY();
		
		Direction new_dir;
		
		if(s == Side.RIGHT)
		{
			new_dir=right(this.dir);
		}
		else
		{
			new_dir=left(this.dir);
		}
		
		
		switch(new_dir)
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
			return true;
		}
			
		return false;
		
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
