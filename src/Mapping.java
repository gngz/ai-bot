
public class Mapping {

	public enum Actors {NONVISITED,EMPTY,ZOMBIE,PART,BULLET};
	
	private Actors map[][] = new Actors[6][6];
	
	
	public Mapping() {
		resetMap();
	}
	
	public void resetMap()
	{
		for(int x = 0;x<6;x++)
			for(int y = 0;y<6;y++)
				this.map[x][y]=Actors.NONVISITED;
	}

	public void setPosition(int x, int y, Actors type)
	{
		map[x-1][y-1] = type;
	}
	
	public Actors readPosition(int x, int y)
	{
		return map[x-1][y-1];
	}
	

}


