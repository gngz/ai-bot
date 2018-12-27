
public class Mapping {

	public enum Cell {NONVISITED,VISITED};
	
	private Cell map[][] = new Cell[6][6];
	
	
	public Mapping() {
		resetMap();
	}
	
	public void resetMap()
	{
		for(int x = 0;x<6;x++)
			for(int y = 0;y<6;y++)
				this.map[x][y]=Cell.NONVISITED;
	}
	
	void setVisited(int x,int y)
	{
		map[x-1][y-1] = Cell.VISITED;
	}
	
	boolean checkVisited(int x, int y)
	{
		return(map[x-1][y-1] == Cell.VISITED);
	}

}
