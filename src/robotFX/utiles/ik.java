package robotFX.utiles;

import robotFX.model.Cords;
import robotFX.model.Pose;



public class ik {
	private static final double L1 = 15;
	private static final double L2 = 17;
	private static double cosangle(double opp, double adj1, double adj2)
	{
	    // Cosine rule:
	    // C^2 = A^2 + B^2 - 2*A*B*cos(angle_AB)
	    // cos(angle_AB) = (A^2 + B^2 - C^2)/(2*A*B)
	    // C is opposite
	    // A, B are adjacent
	    double den = 2*adj1*adj2;

	    if(den==0) {
	    	System.out.println(den);
	    	return Double.NaN;
	    }
	    double c = (adj1*adj1 + adj2*adj2 - opp*opp)/den;

	    if(c>1 || c<-1) {
	    	System.out.println(c);
	    	 return Double.NaN;
	    }

	    return Math.acos(c);
	}
	
	public static Pose solve(double x, double y, double z, int pinch)
	{
		y=-y;
	    // Solve top-down view
	    Cords c1 = new Cords(x, y);
	    //System.out.println(c1);
	    
	    // In arm plane, convert to polar
	    Cords c2 = new Cords(c1.getR(), z);
	    //System.out.println(c2);
	    
	    // Solve arm inner angles as required
	    double B, C;

	    B = cosangle(L2, L1, c2.getR());
	    //System.out.println("B = " + Math.toDegrees(B));
	    
	    C = cosangle(c2.getR(), L1, L2);
	    //System.out.println("C = " + Math.toDegrees(C));
	    if(Double.compare(B, Double.NaN) == 0  && Double.compare(C, Double.NaN) == 0) return null;
	    
	    double a1 = Math.toDegrees(c1.getTheta());
	    double a2 = Math.toDegrees(c2.getTheta() + B);
	    double a3 = Math.toDegrees(C);
	    // Solve for servo angles from horizontal
	    Pose pose = new Pose((int)a1 + 90,
				90 + (90 - (int)a2),
				((int)Utile.inversmap(a3, 40, 180,40, 180)),
				pinch
		);

		System.out.println("in sync : " + pose);
	    return pose;
	}
	
	public static void main(String[] args) {
		int x = 20;
		int y = 0;
		int z = 10;
		
		Pose pose = ik.solve(x, y, z, 120);
		System.out.println(pose);
	}

}