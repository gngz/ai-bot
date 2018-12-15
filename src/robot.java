import lejos.utility.Delay;
import lejos.hardware.Button;
import lejos.hardware.lcd.*;
import lejos.robotics.Color;







public class Robot {
	
	
	static Navigation.Direction robot_dir = Navigation.Direction.NORTH;
	static Position robot_pos = new Position(1,1);
	static Navigation robot_nav = new Navigation(robot_pos,robot_dir,6);
	static final Position moto_pos = new Position(6,6);
	
	

	public static void main(String[] args) {
	

		
		Motion.initialize();
		System.out.printf("   SUViVOR BOT   \n\n");
		System.out.printf("Click any button to start!");
		Button.waitForAnyPress();
		run();

	
	}
	
	public static void run()
	{
		//Motion.moveForward(false,5);
		demo();
		while(true)
		{
			Button.waitForAnyPress();
			Motion.shot();
		}
		//verifyAction();

	
		
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
	
	public static void verifyAction()
	{
		
		int dis = Motion.getDistance();
		
		if(dis >= 20 && dis <= 30)
		{
			Motion.moveUntilObject(false);
			System.out.printf("Color:%d \n",Motion.getColor(Motion.ColorSensor.TOP));
			Motion.moveUntilDistance(dis, true);
		}
		Motion.turn(Motion.side.RIGHT);
		dis = Motion.getDistance();
		if(dis >= 20 && dis <= 30)
		{
			Motion.moveUntilObject(false);
			System.out.printf("Color:%d \n",Motion.getColor(Motion.ColorSensor.TOP));
			Motion.moveUntilDistance(dis, true);
		}
		Motion.turn(Motion.side.LEFT);
		Motion.turn(Motion.side.LEFT);
		dis = Motion.getDistance();
		if(dis >= 20 && dis <= 30)
		{
			Motion.moveUntilObject(false);
			System.out.printf("Color:%d \n",Motion.getColor(Motion.ColorSensor.TOP));
			Motion.moveUntilDistance(dis, true);

		}
		
		Motion.turn(Motion.side.RIGHT);
		
		
		
	}

}
