/*
 * Group 6 - Survival Bot
 * Class Motion
 * Robot Movements, Sounds and Sensor Data
 * 
 */

import java.io.File;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;


public class Motion {


	// Constants
	private final static double STEP_FORWARD_ROT = -2.55;		// Number of rotations to advance one step
	private final static double TURN_ROT =-0.86;				// Number of rotations for turning
	private final static int GRAB_CLOSED_ANGLE = 180;			// not used
	private final static int GRAB_OPEN_ANGLE = -180;			// not used
	private final static int TURN_SPEED = 200;					// Speed in turns
	private final static int NORMAL_SPEED = 250;				// Normal speed
	private final static int OBJECT_THRESHOLD = 15;				// Distance from Distance Sensor to an Object be in bottom of top color sensor (in cm).
	private final static int CATCH_THRESHOLD = 5;				// Distance of the object to which the grab closes
	
	// Motors Declarations
	private static RegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.C);
	private static RegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.D);
	private static EV3MediumRegulatedMotor motorGrab = new EV3MediumRegulatedMotor(MotorPort.B);
	
	// Sensors Declarations
	private static EV3UltrasonicSensor distanceSensor = new EV3UltrasonicSensor(SensorPort.S2);
	private static EV3ColorSensor bottomColorSensor = new EV3ColorSensor(SensorPort.S1);
	private static EV3ColorSensor topColorSensor = new EV3ColorSensor(SensorPort.S3);
	
	
	
	// Util Enums
	public enum ColorSensor {TOP,BOTTOM};
	public enum side {LEFT ,RIGHT };
	

	
	// Initialization of motors
	
	public static void initialize()
	{
	
		motorLeft.setAcceleration(500);
		motorRight.setAcceleration(500);
		motorGrab.rotateTo(0);
		motorLeft.setSpeed(NORMAL_SPEED);
		motorRight.setSpeed(NORMAL_SPEED);
		motorLeft.synchronizeWith(new RegulatedMotor[] { motorRight });
		motorLeft.flt();
		motorRight.flt();
		openGrab();
		motorGrab.flt();
	
	}
	
	// Auxiliary Function to rotate wheels one amount of turns.
	// if catchSomething is true, the grab will close if encounters an object in movement.
	
	private static void rotate(double turns, boolean immediateReturn,boolean catchSomething) {
		motorLeft.resetTachoCount();
		motorRight.resetTachoCount();
		motorLeft.startSynchronization();
		motorLeft.rotateTo((int) (turns * 360), immediateReturn);
		motorRight.rotateTo((int) (turns * 360), immediateReturn);
		motorLeft.endSynchronization();
		while(motorLeft.isMoving())
		{
			if(catchSomething)
			{

				int d = Motion.getDistance();
				if(d == CATCH_THRESHOLD)
					closeGrab();
			}
		}
		
	}
	
	// Auxiliary function to move robot without limit.
	
	private static void move(boolean backward) 
	{
		motorLeft.startSynchronization();
		if(backward)
		{
			motorLeft.forward();
			motorRight.forward();
		}
		else
		{
			motorLeft.backward();
			motorRight.backward();
		}
		motorLeft.endSynchronization();
		
	}
	
	
	// Auxiliary function to stop robot
	
	private static void stop() 
	{
		motorLeft.startSynchronization();
		motorLeft.stop();
		motorRight.stop();
		motorLeft.endSynchronization();
	}
	
	
	// Auxiliary Function to rotate wheels one amount of turns and steer for one side.
	private static void rotate(double turns, boolean immediateReturn,side s) {
		motorLeft.resetTachoCount();
		motorRight.resetTachoCount();
		switch(s)
		{
			case LEFT:

				motorLeft.startSynchronization();
				motorLeft.rotateTo(((int)( turns * 360)) * -1, immediateReturn);
				motorRight.rotateTo(((int) (turns * 360)), immediateReturn);
				motorLeft.endSynchronization();
				break;
			case RIGHT:
				motorLeft.startSynchronization();
				motorLeft.rotateTo(((int) (turns * 360)),immediateReturn);
				motorRight.rotateTo(((int) (turns * 360))* -1, immediateReturn);
				motorLeft.endSynchronization();
				break;
				
		}
		while(motorLeft.isMoving());
		
	}
	
	// Function to move forward one step.

	public static void moveForward(boolean catchSomething) {
		
		rotate(STEP_FORWARD_ROT, true,catchSomething);
	}

	// Function to move forward x steps.

	public static void moveForward(boolean catchSomething,double x) {
		rotate(STEP_FORWARD_ROT*x, true,catchSomething);
	}
	
	// Function to move robot until an object
	
	public static void moveUntilObject(boolean backward)
	{
		motorLeft.setSpeed(TURN_SPEED);
		motorRight.setSpeed(TURN_SPEED);
		move(backward);
		while(getDistance() > OBJECT_THRESHOLD);
		stop();
		motorLeft.setSpeed(NORMAL_SPEED);
		motorRight.setSpeed(NORMAL_SPEED);
		
		
	}
	
	// Function to move robot until a distance from distance sensor.
	
	public static void moveUntilDistance(int d,boolean backward)
	{
		motorLeft.setSpeed(TURN_SPEED);
		motorRight.setSpeed(TURN_SPEED);
		move(backward);
		while(getDistance() != d);
		stop();
		motorLeft.setSpeed(NORMAL_SPEED);
		motorRight.setSpeed(NORMAL_SPEED);
		Delay.msDelay(500);
	}
	
	// Move robot until a color
	
	public static void moveUntilColor(int c,boolean backward) {
		
		motorLeft.setSpeed(TURN_SPEED);
		motorRight.setSpeed(TURN_SPEED);
		move(backward);
		while(getColor(ColorSensor.BOTTOM) != c);
		stop();
		motorLeft.setSpeed(NORMAL_SPEED);
		motorRight.setSpeed(NORMAL_SPEED);
	}

	// Rotate robot to a side
	
	public static void turn(side s) {
		motorLeft.setSpeed(TURN_SPEED);
		motorRight.setSpeed(TURN_SPEED);
		rotate(TURN_ROT,true,s);
		motorLeft.setSpeed(NORMAL_SPEED);
		motorRight.setSpeed(NORMAL_SPEED);
	}
	
	
	// get the distance from distance sensor
	
	public static int getDistance()
	{
		SampleProvider sp = distanceSensor.getDistanceMode();
			
		float [] sample = new float[sp.sampleSize()];
	    sp.fetchSample(sample, 0);
	        
	    return ((int)(sample[0]*100));
			
		
	}
	
	// get color from color sensors
	
	public static int getColor(ColorSensor sensor) {
		if(sensor == ColorSensor.TOP)
			return topColorSensor.getColorID();
		else

			return bottomColorSensor.getColorID();
	}

	
	
	public static void openGrab() {
		motorGrab.rotateTo(-600, true);
		motorGrab.waitComplete();
		motorGrab.flt();
		
	}
	
	public static void closeGrab() {
		motorGrab.rotateTo(600, true);
		motorGrab.waitComplete();
		motorGrab.flt();
	}
	
	// Play a shot sound
	
	public static void shot()
	{
		
		File gunshotWav = new File("/home/lejos/programs/sounds/gunshot.wav");
		Sound.playSample(gunshotWav,100);
	}
	
	public static void punch()
	{
		File punchWav = new File("/home/lejos/programs/sounds/punch.wav");
		Sound.playSample(punchWav,100);
	}
	
	public static void bulletSound()
	{
		File bulletWav = new File("/home/lejos/programs/sounds/bullet.wav");
		Sound.playSample(bulletWav,100);
	}
	
	
}
