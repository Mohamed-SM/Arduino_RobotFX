package robotFX.model;

public class Cords {
	
	double x = 0;
	double y = 0;
	double r = 0;
	double theta = 0;
	
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
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getR() {
		return r;
	}
	public void setR(double r) {
		this.r = r;
	}
	public double getTheta() {
		return theta;
	}
	public void setTheta(double theta) {
		this.theta = theta;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Cord : (" + this.x + "," + this.y + ").(" + this.r + "," + Math.toDegrees(this.theta) + ")";
	}
}
