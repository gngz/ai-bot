
public class Position {

	private int x,y;
	int dir;
	
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
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public void setDir(int dir)
	{
		this.dir = dir;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	int calcutateDistance(Position pos)	//maybe double
	{
		int d;
		
		d = (int) Math.sqrt((int)Math.pow((this.x - pos.x),2) + Math.pow((this.y - pos.y),2));
		
		return d;
	}
	
	public Position add(Position pos)
	{
		Position p = new Position(this.x + pos.x,this.y+pos.y);
		return p;
	}
	
	// Useful to calculate number of steps for a given position
	public Position sub(Position pos)
	{
		Position p = new Position(this.x - pos.x,this.y - pos.y);
		return p;
	}

}
