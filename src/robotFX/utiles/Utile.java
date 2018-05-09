package robotFX.utiles;

public class Utile {


	public static double map(double F ,double A ,double B  ,double C  ,double D) {
		return  (F - A) * (D - C) / (B - A) + C;
	}

	public static double inversmap(double X ,double A ,double B  ,double C  ,double D) {
		return -((X - A) * (D - C) / (B - A) + C) + C + D;
	}

}
