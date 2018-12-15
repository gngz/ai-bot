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


	
	private final static double STEP_FORWARD_ROT = -2.55;		// Number of rotations to advance one step
	private final static double TURN_ROT =-0.85;				// Number of rotations for turning
	private final static int GRAB_CLOSED_ANGLE = 180;
	private final static int GRAB_OPEN_ANGLE = -180;			
	private final static int TURN_SPEED = 200;					// Speed in turns
	private final static int NORMAL_SPEED = 250;				// Normal speed
	private final static int OBJECT_THRESHOLD = 15;				// Distance from Distance Sensor to an Object be in bottom of top color sensor (in cm).
	private final static int CATCH_THRESHOLD = 5;
	
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
	
	private static void stop() 
	{
		motorLeft.startSynchronization();
		motorLeft.stop();
		motorRight.stop();
		motorLeft.endSynchronization();
	}
	
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

	public static void moveForward(boolean catchSomething) {
		
		rotate(STEP_FORWARD_ROT, true,catchSomething);
	}

	public static void moveForward(boolean catchSomething,double x) {
		rotate(STEP_FORWARD_ROT*x, true,catchSomething);
	}
	
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
	
	public static void moveUntilColor(int c,boolean backward) {
		
		motorLeft.setSpeed(TURN_SPEED);
		motorRight.setSpeed(TURN_SPEED);
		move(backward);
		while(getColor(ColorSensor.BOTTOM) != c);
		stop();
		motorLeft.setSpeed(NORMAL_SPEED);
		motorRight.setSpeed(NORMAL_SPEED);
	}

	public static void turn(side s) {
		motorLeft.setSpeed(TURN_SPEED);
		motorRight.setSpeed(TURN_SPEED);
		rotate(TURN_ROT,true,s);
		motorLeft.setSpeed(NORMAL_SPEED);
		motorRight.setSpeed(NORMAL_SPEED);
	}
	
	
	public static int getDistance()
	{
		SampleProvider sp = distanceSensor.getDistanceMode();
			
		float [] sample = new float[sp.sampleSize()];
	    sp.fetchSample(sample, 0);
	        
	    return ((int)(sample[0]*100));
			
		
	}
	
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
	
	public static void shot()
	{
		
		File gunshotWav = new File("/home/lejos/programs/gunshot.wav");
		Sound.playSample(gunshotWav,100);
	}
	
	
	
}
