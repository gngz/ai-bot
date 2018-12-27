import lejos.utility.Delay;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Robot {
	
	// Global Vars
	static Navigation.Direction robot_dir = Navigation.Direction.NORTH;
	static Position robot_pos = new Position(1,1);
	static Navigation robot_nav = new Navigation(robot_pos,robot_dir,6);
	static final Position moto_pos = new Position(6,6);
	
	static final int ZOMBIE = 1;			// Green
	static final int ZOMBIE_PART = 7;		// BLACK
	static final int SMELL1 = 0;			// Red
	static final int SMELL2 = 2;			// Blue
	static final int PARTS = 6;			// White
	static final int BULLET = 3;			// Yellow
	static final int BULLET2 = 13;		// Brown
	
	
	public static void main(String[] args) {
		// Printing the main message
		Motion.initialize();
		System.out.printf("   SUViVOR BOT   \n\n");
		System.out.printf("Click any button to start!");
		Button.waitForAnyPress();
		run();	// "Run" the game

		
	}
	
	public static void run()
	{
		AlarmThread thread = new AlarmThread();
		thread.start();
		thread.suspend();
		
		int smell	= -1;
		ArrayList<TurnStruct> verified_objects;
		
		boolean has_object = false;
		boolean has_bullet = false;
		int num_delivered_objects = 0;
		
		Position search_position = new Position(2,2);
		
		
		while(num_delivered_objects < 2) {
			
			smell = Motion.getColor(Motion.ColorSensor.BOTTOM);
			verified_objects = verifyAction();
			ArrayList<TurnStruct> possible_turns = new ArrayList<TurnStruct>(verified_objects);
						
			removeByObjType(possible_turns,TurnStruct.Objects.ZOMBIE);	// Remove Zombie from possible turn
			removeByObjType(possible_turns,TurnStruct.Objects.ZOMBIE_PART);	// Remove Zombie with Part from possible turn
			removeBySmell(possible_turns,1);
			
			
			// Corner special cases
			if(isInCorner() && smell == 2)
			{
				if(robot_nav.getPosition().equals(new Position(1,1)))
				{
					
				}
				else if(robot_nav.getPosition().equals(new Position(6,6)))
				{
					
				}
				else if(robot_nav.getPosition().equals(new Position(1,6)))
				{
					
				}
				else if(robot_nav.getPosition().equals(new Position(6,1)))
				{
					
				}
				
			}
			
			
			if(has_object)
			{
				// Should deliver object into destination (moto)
				// if in position 6,6 drop the part
				
				//removeByObjType(possible_turns,TurnStruct.Objects.PART);	// Remove Part from possible turn
				
				if(robot_nav.getPosition().equals(new Position(6,6)))
				{
					has_object = false;
					Motion.openGrab();
					num_delivered_objects++;
				}
				
				// set Heuristic to point (6,6) moto_pos
				calculateHeuristicByPosition(moto_pos,possible_turns);
				
				TurnStruct temp_bullet = isObjectInList(verified_objects,TurnStruct.Objects.BULLET);
				TurnStruct zm = isObjectInList(verified_objects,TurnStruct.Objects.ZOMBIE);
				TurnStruct zmP = isObjectInList(verified_objects,TurnStruct.Objects.ZOMBIE_PART);
				TurnStruct temp_zombie = null;
				if(zm != null || zmP != null)  temp_zombie = zm==null ? zmP : zm;
				
				if(temp_zombie != null) 
				{
					rotateTo(robot_nav,temp_zombie.dir);
					
					if(has_bullet)
					{
						Motion.shot();
						has_bullet = false;
					}		
					else
						Motion.punch();
						
					
					if(possible_turns.size()>0)
			        {
						
						rotateTo(robot_nav,possible_turns.get(0).dir);
					    robot_nav.move(1);
			            Motion.moveForward(false);
			        }
				}
				else if(temp_bullet != null)
				{
					rotateTo(robot_nav,temp_bullet.dir);
					robot_nav.move(1);
			          Motion.moveForward(false);
				}
				else
				{
					// move
					Collections.sort(possible_turns);	
					if(possible_turns.size()>0)
			        {
						
						rotateTo(robot_nav,possible_turns.get(0).dir);
					    robot_nav.move(1);
			            Motion.moveForward(false);
			        }
					
				}
				
				
				
					
				
			}
			else
			{
				// Should find an object
				
				
				if(robot_nav.getPosition().equals(new Position(2,2)))
				{
					search_position = new Position(5,2);
				}
				else if(robot_nav.getPosition().equals(new Position(5,2)))
				{
					search_position = new Position(5,5);
				}
				else if(robot_nav.getPosition().equals(new Position(5,5)))
				{
					search_position = new Position(2,5);
				}
				else if(robot_nav.getPosition().equals(new Position(2,5)))
				{
					search_position = new Position(2,2);
				}
				
				calculateHeuristicByPosition(search_position,possible_turns);
				Collections.sort(possible_turns);	
				
				TurnStruct temp_bullet = isObjectInList(verified_objects,TurnStruct.Objects.BULLET);
				TurnStruct temp_zombie = isObjectInList(verified_objects,TurnStruct.Objects.ZOMBIE);
				TurnStruct temp_zombie_part = isObjectInList(verified_objects,TurnStruct.Objects.ZOMBIE_PART);
				TurnStruct temp_part= isObjectInList(verified_objects,TurnStruct.Objects.PART);
				
				// Attack
				if(temp_zombie_part != null)
				{
					rotateTo(robot_nav,temp_zombie_part.dir);
					if(has_bullet)
					{
						Motion.shot();
						has_bullet = false;
					}
					else
						Motion.punch();
					
					has_object = true;
					receivePart();	// Receive part from zombie
					
				}	
				else if(temp_zombie != null)
				{
					rotateTo(robot_nav,temp_zombie.dir);
					if(has_bullet)
						Motion.shot();
					else
						Motion.punch();
					
				}
				
				// Grab Something or move
				
				if(temp_bullet != null)	// Grab and Move
				{
					rotateTo(robot_nav,temp_bullet.dir);
					robot_nav.move(1);
					Motion.moveForward(true);
					Motion.bulletSound();
					has_bullet = true;
					
					
				}
				else if(!has_object && temp_part != null)
				{
					rotateTo(robot_nav,temp_part.dir);
					robot_nav.move(1);
					Motion.moveForward(true);
				}
				else	// Move
				{
					if(possible_turns.size()>0)
			        {
						
						rotateTo(robot_nav,possible_turns.get(0).dir);
					    robot_nav.move(1);
			            Motion.moveForward(false);
			        }
					
				}
					
				
				
			}
			
			
			// Debugging
			System.out.printf("R: (%d,%d) D:%s \n",robot_nav.getPosition().getX(),robot_nav.getPosition().getY(),robot_nav.getDir().toString());
			for(int i = 0; i < possible_turns.size();i++)
			{
				TurnStruct ts = possible_turns.get(i);
				System.out.printf("%s (%d,%d) %d\n", ts.obj.toString(),ts.p.getX(),ts.p.getY(),ts.heuristic);
			}
			
			
			
			/*
			// moving
			if(has_object)
			{
				Collections.sort(possible_turns);	// Sort possible_turns list by heuristic;
				if(possible_turns.size()>0)
		        {
					
					rotateTo(robot_nav,possible_turns.get(0).dir);
				    robot_nav.move(1);
		            Motion.moveForward(false);
		        }
				
				
			}else {
				Collections.sort(possible_turns);	// Sort possible_turns list by heuristic;
				
				
				if(possible_turns.size()>0)
		        {
					
					rotateTo(robot_nav,possible_turns.get(0).dir);
				    robot_nav.move(1);
		            Motion.moveForward(false);
		        }
				
			}*/
			
					
			waitTurn();	// Wait for next turn
			
			
		}
	
		// Acabou o jogo, You win
	}
	
	static void receivePart()
	{
		Motion.openGrab();
		while(Motion.getDistance() > 1);
		Motion.closeGrab();
	}
	
	static void waitTurn()
	{
		//LCD.clear();
		
		System.out.printf("Waiting my Turn!\n\n");
		
		//System.out.printf("Press ENTER to start my turn.");;
		while(Button.waitForAnyPress() != Button.ID_ENTER);
		
		
	}
	
	
	
	static TurnStruct isObjectInList(ArrayList<TurnStruct> t, TurnStruct.Objects obj)
	{
		for(int i = 0;i<t.size();i++)
		{
			if(t.get(i).obj == obj)
				return  t.get(i);
		}
		
		return null;
		
	}
	
	static void removeByObjType(ArrayList<TurnStruct> t, TurnStruct.Objects obj)
	{
		Iterator iter = t.iterator();
        
        while(iter.hasNext())
        {
           TurnStruct el = (TurnStruct) iter.next();
           if(el.obj == obj)
           {
               iter.remove();
           }
           
        }
		
	}
	
	static void removeBySmell(ArrayList<TurnStruct> t,int smell)
	{
		Iterator iter = t.iterator();
        
        while(iter.hasNext())
        {
           TurnStruct el = (TurnStruct) iter.next();
           if(el.smell == smell)
           {
               iter.remove();
           }
           
        }
		
	}
	
	static void calculateHeuristicByPosition(Position p,ArrayList<TurnStruct> t)
	{
		Iterator<TurnStruct> iter = t.iterator();
        
        while(iter.hasNext())
        {
           TurnStruct el = (TurnStruct) iter.next();
           el.heuristic = el.p.calcutateDirectDistance(p.getX(), p.getY());
           
        }
		
	}
	
	// Rotate robot to a direction
	static void rotateTo(Navigation nav, Navigation.Direction dir)
	{
		int dir_mov = rotateSide(robot_nav.getDir(),dir);
		while(nav.getDir() != dir)
        {
        	if(dir_mov >= 0)
        	{
        		nav.rotateRight();
        		Motion.turn(Motion.side.RIGHT);
        	}
        	else
        	{
        		nav.rotateLeft();
        		Motion.turn(Motion.side.LEFT);
        	}
            
            
           
        }
	}
	
	
	// Auxiliary Function to get best side to rotate
	private static int rotateSide(Navigation.Direction from, Navigation.Direction to)
	{
		int from_value = 0, to_value = 0;
		
		switch(from)
		{
		case NORTH:
			from_value = 0;
			break;
		case EAST:
			from_value = 1;
			break;
		case SOUTH:
			from_value = 2;
			break;
		case WEST:
			from_value = -1;
			break;
		}
		
		switch(to)
		{
		case NORTH:
			to_value = 0;
			break;
		case EAST:
			to_value = 1;
			break;
		case SOUTH:
			to_value = 2;
			break;
		case WEST:
			to_value = -1;
			break;
		}
		
		
		
		return (to_value - from_value);
	}
	
	public static ArrayList<TurnStruct> verifyAction()
	{
		ArrayList<TurnStruct> detected_objects = new ArrayList<TurnStruct>();
		int dis;
	
		
		for(int i = 0; i <4; i++)
		{
			
			if(robot_nav.verify(1))
			{
				 dis = Motion.getDistance();
				 if(dis >= 20 && dis <= 30)
				 {
					 	TurnStruct ts;
					 	robot_nav.move(1);
						Motion.moveUntilObject(false);
						Delay.msDelay(500);
						int color = Motion.getColor(Motion.ColorSensor.TOP);
						Motion.moveUntilDistance(6, false);
						Delay.msDelay(500);
						int smell = Motion.getColor(Motion.ColorSensor.BOTTOM);
						Position p = robot_nav.getPosition();
						Navigation.Direction d = robot_nav.getDir();
						
						switch(color)
						{
						case ZOMBIE:
							ts = new TurnStruct(p,d,TurnStruct.Objects.ZOMBIE,p.calcutateDirectDistance(6, 6),smell);
							detected_objects.add(ts);
							break;
						case ZOMBIE_PART:
							ts = new TurnStruct(p,d,TurnStruct.Objects.ZOMBIE_PART,p.calcutateDirectDistance(6, 6),smell);
							detected_objects.add(ts);
							break;
						case BULLET:
							ts = new TurnStruct(p,d,TurnStruct.Objects.BULLET,p.calcutateDirectDistance(6, 6),smell);
							detected_objects.add(ts);
							break;
						case PARTS:
							ts = new TurnStruct(p,d,TurnStruct.Objects.PART,p.calcutateDirectDistance(6, 6),smell);
							detected_objects.add(ts);
							break;
						}
						
						Motion.moveUntilDistance(dis, true);
						robot_nav.move(-1);
						
						
				 }
				 else
				 {
					robot_nav.move(1);
					Motion.moveForward(false);
					int smell = Motion.getColor(Motion.ColorSensor.BOTTOM);
					Position p = robot_nav.getPosition();
					Navigation.Direction d = robot_nav.getDir();
					TurnStruct ts = new TurnStruct(p,d,TurnStruct.Objects.VOID,p.calcutateDirectDistance(6, 6),smell);
					detected_objects.add(ts);
					Motion.moveForward(false,-1);
					robot_nav.move(-1);
				 }

			}
			Motion.turn(Motion.side.RIGHT);
			robot_nav.rotateRight();
			
			
		}
	
		
		
		return detected_objects;
	}
	
	static boolean isInCorner()
	{
		Position pos = robot_nav.getPosition();
		
		if(pos.equals(new Position(6,6)) || pos.equals(new Position(1,1)) || pos.equals(new Position(6,1)) || pos.equals(new Position(1,6)))
			return true;
		return false;
		
	}
	


}


// Alarm Thread
 class AlarmThread extends Thread {

    public void run(){
       while(true)
       {
    	   Sound.twoBeeps();
       }
    }
  }
