import lejos.utility.Delay;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.*;
import java.util.concurrent.ThreadLocalRandom;


public class Robot {
	
	// Global Vars
	static Navigation.Direction robot_dir = Navigation.Direction.NORTH;
	static Position robot_pos = new Position(1,1);
	static Navigation robot_nav = new Navigation(robot_pos,robot_dir,6);
	static final Position moto_pos = new Position(6,6);
	
	static int ZOMBIE = 1;			// Green
	static int ZOMBIE_PART = 7;		// BLACK
	static int SMELL1 = 0;			// Red
	static int SMELL2 = 2;			// Blue
	static int PARTS = 6;			// White
	static int BULLET = 3;			// Yellow
	static int BULLET2 = 13;		// Brown
	
	
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
		
		int verified_objects[] = new int[3];
		int smell	= -1;
		int action = 0;
		
		while(true) {
			verified_objects = verifyAction();	// Verify Surrounding objects		
			smell = Motion.getColor(Motion.ColorSensor.BOTTOM);	// Get the smell
			int zombie_pos = -1;
			int part_pos = -1;
			int bullet_pos = -1;
			


				
			for(int i = 0;i<3; i++)
			{
				int object = verified_objects[i];
				if(object == ZOMBIE || object == ZOMBIE_PART)
				{
					zombie_pos = i;
				}
				else if(object == PARTS)
				{
					part_pos = i;
				}
				else if(object == BULLET)
				{
					bullet_pos = i;
				}
			}
			
				
				
			
			if(zombie_pos != -1)
			{
				System.out.printf("DANGER!\n");
				switch(zombie_pos)
				{
					case 0:	// Zombie on the front
						Motion.punch();
						if(robot_nav.move(-1))
						{
							
							Motion.moveForward(false, -1);
						}
						else
						{
							if(robot_nav.verify(1, Navigation.Side.LEFT))
							{
								Motion.turn(Motion.side.LEFT);
								robot_nav.rotateLeft();
								Motion.moveForward(false, 1);
								robot_nav.move(1);
							}
							else if(robot_nav.verify(1, Navigation.Side.RIGHT))
							{
								Motion.turn(Motion.side.RIGHT);
								robot_nav.rotateRight();
								Motion.moveForward(false, 1);
								robot_nav.move(1);
							}
							else if(robot_nav.move(-1))
							{
								Motion.moveForward(false,-1);
							}
						}
						break;
					case 1:	// Zombie on the right
						
						Motion.turn(Motion.side.RIGHT);
						robot_nav.rotateRight();
						Motion.punch();
						Motion.turn(Motion.side.LEFT);
						robot_nav.rotateLeft();
						
							
						
						if(robot_nav.verify(1, Navigation.Side.LEFT))
						{
							
							Motion.turn(Motion.side.LEFT);
							robot_nav.rotateLeft();
							Motion.moveForward(false, 1);
							robot_nav.move(1);
						}
						else if(robot_nav.move(1))
						{
							Motion.moveForward(false,1);
						}
						else if(robot_nav.move(-1))
						{
							Motion.moveForward(false,-1);
						}
						break;
					case 2:	// Zombie on the left
						
						Motion.turn(Motion.side.LEFT);
						robot_nav.rotateLeft();
						Motion.punch();
						Motion.turn(Motion.side.RIGHT);
						robot_nav.rotateRight();
						
						
						if(robot_nav.verify(1, Navigation.Side.RIGHT))
						{
			
							Motion.turn(Motion.side.RIGHT);
							robot_nav.rotateRight();
							Motion.moveForward(false, 1);
							robot_nav.move(1);
						}
						else if(robot_nav.move(1))
						{
							Motion.moveForward(false,1);
						}
						else if(robot_nav.move(-1))
						{
							Motion.moveForward(false,-1);
						}
						break;
				}
			}
			else if(part_pos != -1)
			{
				System.out.printf("GRAB!\n",part_pos);
				switch(part_pos)
				{
				case 0:
					if(robot_nav.move(1))
					{
						Motion.moveForward(true, 1);
						thread.resume();
						
					}
					break;
				case 1:
					if(robot_nav.verify(1, Navigation.Side.RIGHT))
					{
						thread.resume();
						Motion.turn(Motion.side.RIGHT);
						robot_nav.rotateRight();
						Motion.moveForward(true, 1);
						robot_nav.move(1);
					}
					break;
				case 2:
					if(robot_nav.verify(1, Navigation.Side.LEFT))
					{
						thread.resume();
						Motion.turn(Motion.side.LEFT);
						robot_nav.rotateLeft();
						Motion.moveForward(true, 1);
						robot_nav.move(1);
					}
					break;
				}
			}
			else if(smell == BULLET)
			{
				Motion.bulletSound();
				
				
			}
			else if(smell == SMELL1)
			{
				System.out.printf("SMELL 1 Square Away\n");
				randomMovement();
			}
				
			else if(smell == SMELL2)
			{
				System.out.printf("SMELL 2 Squares Away\n");
				randomMovement();
			}
				
			else
			{
				randomMovement();
				
			}
			
			waitTurn();
			
			
		
		}
		
		
		
		
		
		/*
		verifyAction();
		
		
		Button.waitForAnyPress();
		
		
		
		AlarmThread thread = new AlarmThread();
		thread.start();
		thread.suspend();
	
		  
	
		//demo();
		while(true)
		{
			Button.waitForAnyPress();
			thread.resume();
			Motion.moveForward(true,1);
			thread.suspend();
			Button.waitForAnyPress();
			Motion.shot();
			Button.waitForAnyPress();
		}
		//verifyAction();
*/
	
		
	}
	
	static void randomMovement() {
		int action =  ThreadLocalRandom.current().nextInt(1, 5);
		switch(action)
		{
		case 1:
			if(robot_nav.move(1))
			{
				Motion.moveForward(false);
			}
			break;
		case 2:
			if(robot_nav.move(-1))
			{
				Motion.moveForward(false,-1);
			}
			break;
		case 3:
			if(robot_nav.verify(1, Navigation.Side.RIGHT))
			{
				Motion.turn(Motion.side.RIGHT);
				robot_nav.rotateRight();
				Motion.moveForward(false);
				robot_nav.move(1);
			}
			break;
		case 4:
			if(robot_nav.verify(1, Navigation.Side.LEFT))
			{
				Motion.turn(Motion.side.LEFT);
				robot_nav.rotateLeft();
				Motion.moveForward(false);
				robot_nav.move(1);
			}
		}
	}

	
	static void demo()
	{
		boolean grab_isopen = true;
		while(true) {
			int button = Button.waitForAnyPress();
			
			switch(button)
			{
				
				case Button.ID_UP:
					Delay.msDelay(1000);
					if(robot_nav.move(1))
						Motion.moveForward(false);
					break;
				case Button.ID_DOWN:
					Delay.msDelay(1000);
					if(robot_nav.move(-1))
						Motion.moveForward(false,-1);
					break;
				case Button.ID_LEFT:
					Delay.msDelay(1000);
					robot_nav.rotateLeft();
					Motion.turn(Motion.side.LEFT);
					break;
				case Button.ID_RIGHT:
					Delay.msDelay(1000);
					robot_nav.rotateRight();
					Motion.turn(Motion.side.RIGHT);
					break;
				case Button.ID_ENTER:
					if(!grab_isopen)
					{
						Motion.openGrab();
						
					}
					else
					{
						Motion.closeGrab();
					}
					grab_isopen = !grab_isopen;
					break;
					
			}
			
			System.out.printf("POS(%d,%d) D=%s\n",robot_pos.getX(),robot_pos.getY(),robot_nav.getDir().name());
		}
	}
	
	static void waitTurn()
	{
		LCD.clear();
		
		System.out.printf("Waiting my Turn!\n\n");
		
		System.out.printf("Press ENTER to start my turn.");;
		while(Button.waitForAnyPress() != Button.ID_ENTER);
		
		
	}
	
	public static int[] verifyAction()
	{
		int detected_objects[] = new int[3];
		
		for(int i = 0; i < 3;i++)
			detected_objects[i] = -1;
		
		
		
		int dis = Motion.getDistance();
		if(robot_nav.verify(1))
		{
			if(dis >= 20 && dis <= 30)
			{
					Motion.moveUntilObject(false);
					Delay.msDelay(500);
					detected_objects[0] = Motion.getColor(Motion.ColorSensor.TOP);
					Motion.moveUntilDistance(dis, true);
				
				
			}
		}
		
		if(robot_nav.verify(1,Navigation.Side.RIGHT))
		{
			Motion.turn(Motion.side.RIGHT);
			dis = Motion.getDistance();
			if(dis >= 20 && dis <= 30)
			{
				
					Motion.moveUntilObject(false);
					Delay.msDelay(500);
					detected_objects[1] = Motion.getColor(Motion.ColorSensor.TOP);
					Motion.moveUntilDistance(dis, true);
				
				
			}
			Motion.turn(Motion.side.LEFT);
		}
		
		
		if(robot_nav.verify(1,Navigation.Side.LEFT))
		{
			Motion.turn(Motion.side.LEFT);

			dis = Motion.getDistance();
			if(dis >= 20 && dis <= 30)
			{
				if(robot_nav.verify(1))
				{
					Motion.moveUntilObject(false);
					Delay.msDelay(500);
					detected_objects[2] = Motion.getColor(Motion.ColorSensor.TOP);
					Motion.moveUntilDistance(dis, true);
				}
			}
			
			Motion.turn(Motion.side.RIGHT);
		}
		
		return detected_objects;
		
		
		
	}

}

 class AlarmThread extends Thread {

    public void run(){
       while(true)
       {
    	   Sound.twoBeeps();
       }
    }
  }
