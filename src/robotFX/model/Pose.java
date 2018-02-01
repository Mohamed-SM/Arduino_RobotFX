package robotFX.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pose {
    private StringProperty name = new SimpleStringProperty("");
    private IntegerProperty base = new SimpleIntegerProperty(0);
    private IntegerProperty shoulder = new SimpleIntegerProperty(0);
    private IntegerProperty elbow = new SimpleIntegerProperty(0);
    private IntegerProperty pinch = new SimpleIntegerProperty(0);

    public Pose(String name,int base, int shoulder, int elbow, int pinch) {
        this.name.set(name);
        this.base.set(base);
        this.shoulder.set(shoulder);
        this.elbow.set(elbow);
        this.pinch.set(pinch);
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

    public int getShouler() {
        return shoulder.get();
    }

    public IntegerProperty shoulerProperty() {
        return shoulder;
    }

    public void setShouler(int shoulder) {
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
}
