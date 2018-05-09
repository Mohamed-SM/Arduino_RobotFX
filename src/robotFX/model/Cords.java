package robotFX.model;

public class Cords {

	private double x;
	private double y;
	private double r;
	private double theta = 0;
	
	public Cords(double x,double y) {
		this.x = x;
		this.y = y;
		
		r = Math.sqrt(x*x + y*y);

	    // Don't try to calculate zero-magnitude vectors' angles
	    if(r == 0) return;

	    double c = x / r;
	    double s = y / r;

	    // Safety!
	    if(s > 1) s = 1;
	    if(c > 1) c = 1;
	    if(s < -1) s = -1;
	    if(c < -1) c = -1;

	    // Calculate angle in 0..PI
	    theta = Math.acos(c);

	    // Convert to full range
	    if(s < 0) theta *= -1;
	}

	public double getR() {
		return r;
	}

	public double getTheta() {
		return theta;
	}

	@Override
	public String toString() {
		return "Cord : (" + this.x + "," + this.y + ").(" + this.r + "," + Math.toDegrees(this.theta) + ")";
	}
}
