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
	static Position robot_pos = new Position(1, 1);
	static Navigation robot_nav = new Navigation(robot_pos, robot_dir, 6);
	static final Position moto_pos = new Position(6, 6);

	static final int ZOMBIE = 1; // Green
	static final int ZOMBIE_PART = 7; // BLACK
	static final int SMELL1 = 0; // Red
	static final int SMELL2 = 2; // Blue
	static final int SMELL3 = 1; // Smell 1 + Smell 2 Green
	static final int PARTS = 6; // White
	static final int BULLET = 3; // Yellow
	static final int BULLET2 = 13; // Brown
	static AlarmThread thread = new AlarmThread();
	public static void main(String[] args) {
		// Printing the main message
		Motion.initialize();	// Initialize Motors
		run(); // "Run" the game
		
		

	}

	public static void run() {
	
		// Initialize Alarm Thread
		
		thread.start();
		thread.suspend();

		// Global Vars
		
		int smell = -1;	// Smell in current position
		ArrayList<TurnStruct> verified_objects;	// List of verified objects
		boolean has_object = false;	// has object in grab flag
		boolean has_bullet = false;	// has bullet flag
		int num_delivered_objects = 0;	// number of delivered objects into moto position

		Position search_position = new Position(2, 2);	// Initial search route corner

		while (num_delivered_objects < 2) {	// Objective Test
			waitTurn(); // Wait for next turn
			smell = Motion.getColor(Motion.ColorSensor.BOTTOM);	// Get the smell in current position
			
			
			verified_objects = verifyAction(has_object);	// get list of verified objects in adjacent positions.
			
		

						
			ArrayList<TurnStruct> possible_turns = new ArrayList<TurnStruct>(verified_objects);	// Make a copy

			
			// Make Walls
			removeByObjType(possible_turns, TurnStruct.Objects.ZOMBIE); // Remove Zombie from possible turn
			removeByObjType(possible_turns, TurnStruct.Objects.ZOMBIE_PART); // Remove Zombie with Part from possible
			removeBySmell(possible_turns, SMELL1);
			removeBySmell(possible_turns, SMELL3);	// Patch

			// Corner special cases TODO: TEST!!!!
			if (isInCorner() && smell == SMELL3) {	// if is in a corner position and the smell in current position is 1 and 2 (same time).
				
				// check for zombie in adjacent positions.
				TurnStruct zm = isObjectInList(verified_objects, TurnStruct.Objects.ZOMBIE);
				TurnStruct zmP = isObjectInList(verified_objects, TurnStruct.Objects.ZOMBIE_PART);
				TurnStruct temp_zombie = null;
				if (zm != null || zmP != null)
					temp_zombie = (zm == null) ? zmP : zm;
				
				
				// Check another smell 3 in adjacent position
				TurnStruct temp_smell3 = getSmellInList(verified_objects,SMELL3);
				
				if(temp_smell3 != null)
					rotateTo(robot_nav,temp_smell3.dir);	// Rotate to Smell 3 position
				
				
				Motion.moveForward(false);	// Move to smell 3 position
				robot_nav.move(1);
				
				
				if(getNumOfSmells(verified_objects,SMELL1) != 1)	// if there is no smell 1 in adjacent positions.
				{
					rotateTo(robot_nav,temp_zombie.dir);	// go to side of zombie
					Motion.moveForward(false);
					robot_nav.move(1);
					
					Navigation.Direction zombie_dir = getDirFromPos(robot_nav,temp_zombie.p);	// get zombie direction
					
					rotateTo(robot_nav,zombie_dir);	// rotate to zombie direction
					
					
				
					// Punch or Shoot
					if (has_bullet) {
						Motion.shot();
						has_bullet = false;
					} else
						Motion.punch();
				}
				else
				{
					Motion.moveForward(false);	
					robot_nav.move(1);
				}
			
				
				
				
				continue;	// Continue to next turn.
				
			}
			else if (isInCorner() && smell == SMELL1 && getNumbOfZombies(verified_objects) == 2)	// If robot is in corner, smell is smell1 and cornered by Zombies 
			{
				boolean zombie_part_punched = false;
				
				TurnStruct temp_zombie = isObjectInList(verified_objects, TurnStruct.Objects.ZOMBIE);
				TurnStruct temp_zombie_part = isObjectInList(verified_objects, TurnStruct.Objects.ZOMBIE_PART);
				
				if(temp_zombie != null || temp_zombie_part != null)
				{
					if(temp_zombie_part != null)
					{
						rotateTo(robot_nav,temp_zombie_part.dir);
						zombie_part_punched = true;
					}
					else
					{
						rotateTo(robot_nav,temp_zombie.dir);
					}
					
					
					// Punch or Shoot
					if (has_bullet) {
						Motion.shot();
						has_bullet = false;
					} else
						Motion.punch();
					
					Motion.moveForward(false);
					robot_nav.move(1);
					
					Motion.moveForward(false);
					robot_nav.move(1);
					
					if(!has_object && zombie_part_punched)
					{
						receivePart();
					}
					
					continue;	// Continue to next turn.
					
					
				}
			}
			

			if (has_object) {
				// Should deliver object into destination (moto)
				// if in position 6,6 drop the part

				// removeByObjType(possible_turns,TurnStruct.Objects.PART); // Remove Part from
				// possible turn

				

				// set Heuristic to point (6,6) moto_pos and sort
				calculateHeuristicByPosition(moto_pos, possible_turns);
				calculateHeuristicByPosition(moto_pos, verified_objects);
				Collections.sort(possible_turns);
				Collections.sort(verified_objects);
			
				
				TurnStruct temp_bullet = isObjectInList(verified_objects, TurnStruct.Objects.BULLET);
				TurnStruct zm = isObjectInList(verified_objects, TurnStruct.Objects.ZOMBIE);
				TurnStruct zmP = isObjectInList(verified_objects, TurnStruct.Objects.ZOMBIE_PART);
				TurnStruct temp_zombie = null;
				if (zm != null || zmP != null)
					temp_zombie = (zm == null) ? zmP : zm;

				
				
				 if (temp_zombie != null) {	// If have a zombie or a zombie with part: Attack !! 
					rotateTo(robot_nav, temp_zombie.dir);

					if (has_bullet) {
						Motion.shot();
						has_bullet = false;
					} else
						Motion.punch();

					if (possible_turns.size() > 0) {

						rotateTo(robot_nav, possible_turns.get(0).dir);
						robot_nav.move(1);
						Motion.moveForward(false);
					}
				} else if (temp_bullet != null && temp_bullet.smell != SMELL1  && temp_bullet.smell != SMELL3)  {	// Else if have a bullet, grab the bullet
					rotateTo(robot_nav, temp_bullet.dir);
					robot_nav.move(1);
					Motion.moveForward(false);
					Motion.bulletSound();
					has_bullet = true;
					
				} else if(getNumOfSmells(verified_objects,SMELL1) == 1 && smell != SMELL3 && verified_objects.get(0).smell == SMELL1 && (isObjectInList(verified_objects,TurnStruct.Objects.BULLET) != null ||  isObjectInList(verified_objects,TurnStruct.Objects.PART) != null )) {
					// | Move to smell 1 position if the heuristic is better and attack!
					boolean am = false;
					for(TurnStruct t:verified_objects)
					{
						if(t.smell == SMELL1)
						{
							rotateTo(robot_nav,t.dir);
							robot_nav.move(1);
							Motion.moveForward(false);
							//already_moved = true;
							break;
								
						}
					}
					if (has_bullet) {
						Motion.shot();
						has_bullet = false;
					} else
						Motion.punch();
				} else {
					// Else, simply move
					if (possible_turns.size() > 0) {

						rotateTo(robot_nav, possible_turns.get(0).dir);
						robot_nav.move(1);
						Motion.moveForward(false);
					}

				}
				
				if (robot_nav.getPosition().equals(moto_pos)) {	// If reaches moto position, delivery the object
					has_object = false;
					Motion.openGrab();
					num_delivered_objects++;
					thread.suspend();
					search_position = new Position(5, 5); // Set new search position to (5,5) 
				}

			} else {
				// Should find an object
				
				boolean already_moved = false;
				
				if (robot_nav.getPosition().equals(new Position(2, 2))) {
					search_position = new Position(5, 2);
				} else if (robot_nav.getPosition().equals(new Position(5, 2))) {
					search_position = new Position(5, 5);
				} else if (robot_nav.getPosition().equals(new Position(5, 5))) {
					search_position = new Position(2, 5);
				} else if (robot_nav.getPosition().equals(new Position(2, 5))) {
					search_position = new Position(2, 2);
				}

				calculateHeuristicByPosition(search_position, possible_turns);
				calculateHeuristicByPosition(search_position, verified_objects);
				Collections.sort(possible_turns);
				Collections.sort(verified_objects);
	
				
				TurnStruct temp_bullet = isObjectInList(verified_objects, TurnStruct.Objects.BULLET);
				TurnStruct temp_zombie = isObjectInList(verified_objects, TurnStruct.Objects.ZOMBIE);
				TurnStruct temp_zombie_part = isObjectInList(verified_objects, TurnStruct.Objects.ZOMBIE_PART);
				TurnStruct temp_part = isObjectInList(verified_objects, TurnStruct.Objects.PART,SMELL1);

				// Attack
				if (temp_zombie_part != null) {	// If zombie with part, attack and receive part!!
					rotateTo(robot_nav, temp_zombie_part.dir);
					if (has_bullet) {
						Motion.shot();
						has_bullet = false;
					} else
						Motion.punch();

					has_object = true;
					thread.resume();
					receivePart(); // Receive part from zombie

				} else if (temp_zombie != null) {	// Else if attack zombie without part
					rotateTo(robot_nav, temp_zombie.dir);
					if (has_bullet) {
						Motion.shot();
						has_bullet = false;
					} else
						Motion.punch();

				}
				else if(smell == SMELL2 && getNumOfSmells(verified_objects,SMELL1) == 1 && has_bullet)	// Special case, long distance shot
				{
					TurnStruct temp_smell = getSmellInList(verified_objects,SMELL1);
					rotateTo(robot_nav,temp_smell.dir);
					Motion.shot();
					has_bullet = false;
					
				}
				else if(getNumOfSmells(verified_objects,SMELL1) == 1 && smell != SMELL3 && verified_objects.get(0).smell == SMELL1 && (isObjectInList(verified_objects,TurnStruct.Objects.BULLET) == null ||  isObjectInList(verified_objects,TurnStruct.Objects.PART) == null )) {
					// | Move to smell 1 position if the heuristic is better and attack!
					boolean am = false;
					for(TurnStruct t:verified_objects)
					{
						if(t.smell == SMELL1)
						{
							rotateTo(robot_nav,t.dir);
							robot_nav.move(1);
							Motion.moveForward(false);
							already_moved = true;
							break;
								
						}
					}
					if (has_bullet) {
						Motion.shot();
						has_bullet = false;
					} else
						Motion.punch();
				}

				// Grab Something or move
				if(!already_moved)	// If not already moved
				{
					if (temp_bullet != null && temp_bullet.smell != SMELL1  && temp_bullet.smell != SMELL3) // If there is a bullet and the smell in bullet position is not smell1, then grab
					{
						rotateTo(robot_nav, temp_bullet.dir);
						robot_nav.move(1);
						Motion.moveForward(true);
						Motion.bulletSound();
						Motion.openGrab();
						has_bullet = true;

					} else if (!has_object && temp_part != null && temp_part.smell != SMELL1 && temp_part.smell != SMELL3) { // Else if, has one object and no smell1 in object position, then grab
						rotateTo(robot_nav, temp_part.dir);
						robot_nav.move(1);
						Motion.moveForward(true);
						has_object = true;
						thread.resume();
					} else // Move
					{
						if (possible_turns.size() > 0) {	// Else simply move!!

							rotateTo(robot_nav, possible_turns.get(0).dir);
							robot_nav.move(1);
							Motion.moveForward(false);
						}

					}
				}
				System.out.printf("R: (%d,%d) D:%s \n", robot_nav.getPosition().getX(), robot_nav.getPosition().getY(),
						robot_nav.getDir().toString());
				for (int i = 0; i < verified_objects.size(); i++) {
					TurnStruct ts = verified_objects.get(i);
					System.out.printf("%s (%d,%d) %.2f\n", ts.obj.toString(), ts.p.getX(), ts.p.getY(), ts.heuristic);
				}

			}

			

			/*
			 * // moving if(has_object) { Collections.sort(possible_turns); // Sort
			 * possible_turns list by heuristic; if(possible_turns.size()>0) {
			 * 
			 * rotateTo(robot_nav,possible_turns.get(0).dir); robot_nav.move(1);
			 * Motion.moveForward(false); }
			 * 
			 * 
			 * }else { Collections.sort(possible_turns); // Sort possible_turns list by
			 * heuristic;
			 * 
			 * 
			 * if(possible_turns.size()>0) {
			 * 
			 * rotateTo(robot_nav,possible_turns.get(0).dir); robot_nav.move(1);
			 * Motion.moveForward(false); }
			 * 
			 * }
			 */

			

		}
		
		
		thread.thread_close = true;
		
	
		
		
		System.exit(0);
		// Acabou o jogo, You win
	}

	
	static Navigation.Side getSideFromDir(Navigation.Direction dir)
	{
		int side = rotateSide(robot_nav.getDir(), dir);
		if(side >= 0)
			return Navigation.Side.RIGHT;
		return Navigation.Side.LEFT;
	}
	
	static void receivePart() {
		Motion.openGrab();
		while (Motion.getDistance() > 4);
		Motion.closeGrab();
		thread.resume();
	}

	static void waitTurn() {
		// LCD.clear();
		Button.LEDPattern(1);
		System.out.printf("Waiting my Turn!\n\n");

		// System.out.printf("Press ENTER to start my turn.");;
		while (Button.waitForAnyPress() != Button.ID_ENTER);
		Button.LEDPattern(6);

	}
	
	

	
	static int getNumbOfZombies(ArrayList<TurnStruct> t)
	{
		int num = 0;
		for (int i = 0; i < t.size(); i++) {
			if (t.get(i).obj == TurnStruct.Objects.ZOMBIE || t.get(i).obj == TurnStruct.Objects.ZOMBIE_PART )
				num++;
		}
		return num;
	}
	
	
	
	static int getNumOfSmells(ArrayList<TurnStruct> t, int smell)
	{
		int num = 0;
		for (int i = 0; i < t.size(); i++) {
			if (t.get(i).smell == smell)
				num++;
		}
		return num;
	}
	
	static TurnStruct getSmellInList(ArrayList<TurnStruct> t, int smell)
	{
		for (int i = 0; i < t.size(); i++) {
			if (t.get(i).smell == smell)
				return t.get(i);
		}
		
		return null;
	}
	
	static TurnStruct isObjectInList(ArrayList<TurnStruct> t, TurnStruct.Objects obj) {
		for (int i = 0; i < t.size(); i++) {
			if (t.get(i).obj == obj)
				return t.get(i);
		}

		return null;

	}
	
	static TurnStruct isObjectInList(ArrayList<TurnStruct> t, TurnStruct.Objects obj, int smell) {
		for (int i = 0; i < t.size(); i++) {
			if (t.get(i).obj == obj && t.get(i).smell != smell)
				return t.get(i);
		}

		return null;

	}

	static void removeByObjType(ArrayList<TurnStruct> t, TurnStruct.Objects obj) {
		Iterator<TurnStruct> iter = t.iterator();

		while (iter.hasNext()) {
			TurnStruct el = (TurnStruct) iter.next();
			if (el.obj == obj) {
				iter.remove();
			}

		}

	}

	static void removeBySmell(ArrayList<TurnStruct> t, int smell) {
		Iterator<TurnStruct> iter = t.iterator();

		while (iter.hasNext()) {
			TurnStruct el = (TurnStruct) iter.next();
			if (el.smell == smell) {
				iter.remove();
			}

		}

	}

	static void calculateHeuristicByPosition(Position p, ArrayList<TurnStruct> t) {
		Iterator<TurnStruct> iter = t.iterator();

		while (iter.hasNext()) {
			TurnStruct el = (TurnStruct) iter.next();
			el.heuristic = (1.5 * el.p.calcutateDirectDistance(p.getX(), p.getY())) + (0.5 * centerHeuristic(el.p));

		}

	}

	// Rotate robot to a direction
	static void rotateTo(Navigation nav, Navigation.Direction dir) {
		int dir_mov = rotateSide(robot_nav.getDir(), dir);
		while (nav.getDir() != dir) {
			if (dir_mov > 0) {
				nav.rotateRight();
				Motion.turn(Motion.side.RIGHT);
			} else if(dir_mov < 0) {
				nav.rotateLeft();
				Motion.turn(Motion.side.LEFT);
			}

		}
	}

	// Auxiliary Function to get best side to rotate
	private static int rotateSide(Navigation.Direction from, Navigation.Direction to) {
		int from_value = 0, to_value = 0;

		switch (from) {
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

		switch (to) {
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

	public static ArrayList<TurnStruct> verifyAction(boolean has_object) {
		ArrayList<TurnStruct> detected_objects = new ArrayList<TurnStruct>();
		int dis;

		for (int i = 0; i < 4; i++) {

			if (robot_nav.verify(1)) {
				dis = Motion.getDistance();
				if (dis >= 18 && dis <= 32) {
					TurnStruct ts;
					robot_nav.move(1);
					Motion.moveUntilObject(false);
					Delay.msDelay(500);
					int color = Motion.getColor(Motion.ColorSensor.TOP);
					
					if(has_object)
					{
						Motion.moveUntilDistance(7, false);
					}
					else
					{
						Motion.moveUntilDistance(4, false);
					}
					
					Delay.msDelay(500);
					int smell = Motion.getColor(Motion.ColorSensor.BOTTOM);
					Position p = robot_nav.getPosition();
					Navigation.Direction d = robot_nav.getDir();

					switch (color) {
					case ZOMBIE:
						ts = new TurnStruct(p, d, TurnStruct.Objects.ZOMBIE, p.calcutateDirectDistance(6, 6), smell);
						detected_objects.add(ts);
						break;
					case ZOMBIE_PART:
						ts = new TurnStruct(p, d, TurnStruct.Objects.ZOMBIE_PART, p.calcutateDirectDistance(6, 6),
								smell);
						detected_objects.add(ts);
						break;
					case BULLET:
						ts = new TurnStruct(p, d, TurnStruct.Objects.BULLET, p.calcutateDirectDistance(6, 6), smell);
						detected_objects.add(ts);
						break;
					case BULLET2:
						ts = new TurnStruct(p, d, TurnStruct.Objects.BULLET, p.calcutateDirectDistance(6, 6), smell);
						detected_objects.add(ts);
						break;
					case PARTS:
						ts = new TurnStruct(p, d, TurnStruct.Objects.PART, p.calcutateDirectDistance(6, 6), smell);
						detected_objects.add(ts);
						break;
					}

					Motion.moveUntilDistance(dis, true);
					robot_nav.move(-1);

				} else {
					robot_nav.move(1);
					Motion.moveForward(false);
					int smell = Motion.getColor(Motion.ColorSensor.BOTTOM);
					Position p = robot_nav.getPosition();
					Navigation.Direction d = robot_nav.getDir();
					TurnStruct ts = new TurnStruct(p, d, TurnStruct.Objects.VOID, p.calcutateDirectDistance(6, 6),
							smell);
					detected_objects.add(ts);
					Motion.moveForward(false, -1);
					robot_nav.move(-1);
				}

			}
			Motion.turn(Motion.side.RIGHT);
			robot_nav.rotateRight();
			Delay.msDelay(500);

		}

		return detected_objects;
	}

	static boolean isInCorner() {
		Position pos = robot_nav.getPosition();

		if (pos.equals(new Position(6, 6)) || pos.equals(new Position(1, 1)) || pos.equals(new Position(6, 1))|| pos.equals(new Position(1, 6)))
			return true;
		return false;

	}
	
	static Navigation.Direction getDirFromPos(Navigation nav, Position pos)
	{
		Position position = nav.getPosition().sub(pos);
		int x = position.getX();
		int y = position.getY();
		
		if(y == 0)
		{
			if(x > 0)
			{
				return Navigation.Direction.WEST;
			}
			else
			{
				return Navigation.Direction.EAST;
			}
		}
		else
		{
			if(y > 0)
			{
				return Navigation.Direction.SOUTH;
			}
			else
			{
				return Navigation.Direction.NORTH;
			}
		}
		
	}
	
	static double centerHeuristic(Position p)
	{
		double h = Math.abs(p.getX() - 3.5) + Math.abs(p.getY() - 3.5) - 1;
		
		return h;
	}

}

// Alarm Thread
class AlarmThread extends Thread {
	
	boolean thread_close = false;
	
	public void run() {
		while (true && !thread_close) {
			Sound.twoBeeps();
			Delay.msDelay(300);
		}
		
	
	}
}
