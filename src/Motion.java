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

	//private final static double STEP_FORWARD_ROT = 3.2;
	//private final static double TURN_ROT =1.2;
	
	private final static double STEP_FORWARD_ROT = -2.55;
	private final static double TURN_ROT =-0.83;
	private final static int GRAB_CLOSED_ANGLE = 180;
	private final static int GRAB_OPEN_ANGLE = -180;
	private final static int TURN_SPEED = 200;
	private final static int NORMAL_SPEED = 300;

	
	
	// Motors Declarations
	private static RegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.C);
	private static RegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.D);
	private static EV3MediumRegulatedMotor motorGrab = new EV3MediumRegulatedMotor(MotorPort.B);
	
	// Sensors Declarations
	private static EV3UltrasonicSensor distanceSensor = new EV3UltrasonicSensor(SensorPort.S2);
	
	private static EV3ColorSensor bottomColorSensor = new EV3ColorSensor(SensorPort.S1);
	private static EV3ColorSensor topColorSensor = new EV3ColorSensor(SensorPort.S3);
	
	
	public enum colorSensor {TOP,BOTTOM};
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
		motorGrab.flt();
	
	}
	
	private static void rotate(double turns, boolean immediateReturn) {
		motorLeft.resetTachoCount();
		motorRight.resetTachoCount();
		motorLeft.startSynchronization();
		motorLeft.rotateTo((int) (turns * 360), immediateReturn);
		motorRight.rotateTo((int) (turns * 360), immediateReturn);
		motorLeft.endSynchronization();
		while(motorLeft.isMoving());
		
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

	public static void moveForward() {
		
		rotate(STEP_FORWARD_ROT, true);
	}

	public static void moveForward(int x) {
		rotate(STEP_FORWARD_ROT*x, true);
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
	
	public static int getColor(colorSensor sensor) {
		if(sensor == colorSensor.TOP)
			return topColorSensor.getColorID();
		else
			return bottomColorSensor.getColorID();
	}

	public static void openGrab() {
		motorGrab.rotateTo(GRAB_OPEN_ANGLE);
		
	}
	
	public static void closeGrab() {
		motorGrab.rotateTo(GRAB_CLOSED_ANGLE);
		
		
	}
	
}
