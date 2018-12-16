/*
 *  Group 6 - Survival Bot
 *  Position Class
 *  Auxiliary Class
 * 
 */

public class Position {

	// instance variables
	private int x,y;
	private int dir;
	
	
	// Constructors
	
	public Position(int x,int y) {
		this.x = x;
		this.y = y;
		this.dir = 0;
	}
	
	public Position(int x,int y, int dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	
	// Set the x axis of position
	public void setX(int x)
	{
		this.x = x;
	}
	
	// Set the y axis of position
	public void setY(int y)
	{
		this.y = y;
	}
	
	// Set direction
	public void setDir(int dir)
	{
		this.dir = dir;
	}
	
	// Get x axis position
	public int getX() {
		return this.x;
	}
	
	// Get y axis position
	public int getY() {
		return this.y;
	}
	
	
	// Get the direct distance between two positions
	int calcutateDistance(Position pos)	//maybe double
	{
		int d;
		
		d = (int) Math.sqrt((int)Math.pow((this.x - pos.x),2) + Math.pow((this.y - pos.y),2));
		
		return d;
	}
	
	// Adding Positions
	public Position add(Position pos)
	{
		Position p = new Position(this.x + pos.x,this.y+pos.y);
		return p;
	}
	
	// Subtraction of Positions
	// Useful to calculate number of steps for a given position
	public Position sub(Position pos)
	{
		Position p = new Position(this.x - pos.x,this.y - pos.y);
		return p;
	}

}
