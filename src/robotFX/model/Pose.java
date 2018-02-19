package robotFX.model;

import java.text.DecimalFormat;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import robotFX.utiles.ik;

public class Pose {
    private StringProperty name = new SimpleStringProperty("");
    private IntegerProperty base = new SimpleIntegerProperty(0);
    private IntegerProperty shoulder = new SimpleIntegerProperty(0);
    private IntegerProperty elbow = new SimpleIntegerProperty(0);
    private IntegerProperty pinch = new SimpleIntegerProperty(0);

    private IntegerProperty X = new SimpleIntegerProperty(15);
    private IntegerProperty Y = new SimpleIntegerProperty(15);
    private IntegerProperty Z = new SimpleIntegerProperty(15);

    public Pose(String name,int base, int shoulder, int elbow, int pinch) {
        this(base,shoulder, elbow,pinch);
        this.name.set(name);

    }

    public Pose(int base, int shoulder, int elbow, int pinch) {
        this.base.set(base);
        this.shoulder.set(shoulder);
        this.elbow.set(elbow);
        this.pinch.set(pinch);

    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getBase() {
        return base.get();
    }

    public IntegerProperty baseProperty() {
        return base;
    }

    public void setBase(int base) {
        this.base.set(base);
    }

    public int getShoulder() {
        return shoulder.get();
    }

    public IntegerProperty shoulderProperty() {
        return shoulder;
    }

    public void setShoulder(int shoulder) {
        this.shoulder.set(shoulder);
    }

    public int getElbow() {
        return elbow.get();
    }

    public IntegerProperty elbowProperty() {
        return elbow;
    }

    public void setElbow(int elbow) {
        this.elbow.set(elbow);
    }

    public int getPinch() {
        return pinch.get();
    }

    public IntegerProperty pinchProperty() {
        return pinch;
    }

    public void setPinch(int pinch) {
        this.pinch.set(pinch);
    }

    public int getX() {
        return X.get();
    }

    public IntegerProperty xProperty() {
        return X;
    }

    public void setX(int x) {
        this.X.set(x);
    }

    public int getY() {
        return Y.get();
    }

    public IntegerProperty yProperty() {
        return Y;
    }

    public void setY(int y) {
        this.Y.set(y);
    }

    public int getZ() {
        return Z.get();
    }

    public IntegerProperty zProperty() {
        return Z;
    }

    public void setZ(int z) {
        this.Z.set(z);
    }

    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return new DecimalFormat("000").format(base.get()) + " "
                + new DecimalFormat("000").format(shoulder.get()) + " "
                + new DecimalFormat("000").format(elbow.get()) + " "
                + new DecimalFormat("000").format(pinch.get()) + " " + X.get() +  Y.get() + Z.get();
    }
    
    public Pose getCopy() {
        return new Pose(this.getName(), this.base.get(), this.shoulder.get(), this.elbow.get(), this.pinch.get());

    }

    public void sync(){
        Pose temp = ik.solve(X.get(),Y.get(),Z.get(),pinch.get());
        setBase(temp.base.get());
        setShoulder(temp.shoulder.get());
        setElbow(temp.pinch.get());
        setElbow(temp.elbow.get());
    }

    public void setFromPose(Pose pose) {
        this.setBase(pose.getBase());
        this.setShoulder(pose.getShoulder());
        this.setElbow(pose.getElbow());
        this.setPinch(pose.getPinch());
    }
}
