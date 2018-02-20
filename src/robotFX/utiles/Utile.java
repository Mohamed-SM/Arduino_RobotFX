package robotFX.utiles;

public class Utile {


	public static double map(double X ,double A ,double B  ,double C  ,double D) {
		return  (X - A) * (D - C) / (B - A) + C;
	}

	public static double inversmap(double X ,double A ,double B  ,double C  ,double D) {
		double Y = -((X - A) * (D - C) / (B - A) + C) + C + D;
		return  Y;
	}

}
