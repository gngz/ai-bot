import lejos.utility.Delay;
import lejos.hardware.Button;
import lejos.hardware.sensor.*;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.hardware.motor.*;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.lcd.*;
import lejos.ev3.*;
import java.util.Random;


import lejos.robotics.SampleProvider;





public class robot {
	
	
	static Navigation.Direction robot_dir = Navigation.Direction.NORTH;
	static Position robot_pos = new Position(0,0);
	static Navigation robot_nav = new Navigation(robot_pos,robot_dir);
	

	public static void main(String[] args) {
	

		
		Motion.initialize();
		System.out.println("Click any button to start! v1");
		boolean grab_isopen = true;
		while(true) {
			int button = Button.waitForAnyPress();
			
			switch(button)
			{
				
				case Button.ID_UP:
					Delay.msDelay(1000);
					if(robot_nav.move(1))
						Motion.moveForward();
					break;
				case Button.ID_DOWN:
					Delay.msDelay(1000);
					if(robot_nav.move(-1))
						Motion.moveForward(-1);
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
		
		
		
		

		//Motion.moveForward(2);
		//verificar();
		//Button.waitForAnyPress();
		
		
		
		

	


	
	}
	
	public static void verificar()
	{
		final int max = 35,min =10;
		int d;
		int[] ad = new int[4];
		 
		for(int i=0;i<4;i++)
		{
			Delay.msDelay(3000);
			d =Motion.getDistance();
			
			if((d <= max) && (d >= min))
			{
				ad[i]=1;
			}
			else
			{
				ad[i]=0;
			}
			
			System.out.println("AD["+i+"]:"+ad[i]+ " - d:"+d);
			
			
			Motion.turn(Motion.side.LEFT);
		}
		
		
		
		
	}

}
