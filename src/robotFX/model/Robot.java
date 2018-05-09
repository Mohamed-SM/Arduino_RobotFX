package robotFX.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Robot {
    private IntegerProperty base = new SimpleIntegerProperty(0);
    private IntegerProperty shoulder = new SimpleIntegerProperty(0);
    private IntegerProperty elbow = new SimpleIntegerProperty(0);
    private IntegerProperty pinch = new SimpleIntegerProperty(0);

    private IntegerProperty X = new SimpleIntegerProperty(15);
    private IntegerProperty Y = new SimpleIntegerProperty(15);
    private IntegerProperty Z = new SimpleIntegerProperty(15);

    
}
