package es.horm.cart.example.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.horm.cart.lib.annotation.InputField;
import es.horm.cart.lib.annotation.OutputField;

public class IndiaDiabetes {

    @JsonProperty("Times Pregnant")
    @InputField
    private int timesPregnant;
    @JsonProperty("Plasma Glucose")
    @InputField
    private int plasmaGlucose;
    @JsonProperty("Blood Pressure")
    @InputField
    private int bloodPressure;
    @JsonProperty("Triceps Skin Thickness")
    @InputField
    private int tricepsSkinThickness;
    @JsonProperty("Serum Insulin")
    @InputField
    private int serumInsulin;
    @JsonProperty("Body Mass Index")
    @InputField
    private float bodyMassIndex;
    @JsonProperty("Diabetes Pedigree function")
    @InputField
    private float diabetesPedigreeFunction;
    @JsonProperty("Age")
    @InputField
    private int age;

    @JsonProperty("Diabetes positive")
    @OutputField
    private boolean diabetesPositive;

    public IndiaDiabetes() {
    }

    public IndiaDiabetes(int timesPregnant, int plasmaGlucose, int bloodPressure, int tricepsSkinThickness, int serumInsulin, float bodyMassIndex, float diabetesPedigreeFunction, int age, boolean diabetesPositive) {
        this.timesPregnant = timesPregnant;
        this.plasmaGlucose = plasmaGlucose;
        this.bloodPressure = bloodPressure;
        this.tricepsSkinThickness = tricepsSkinThickness;
        this.serumInsulin = serumInsulin;
        this.bodyMassIndex = bodyMassIndex;
        this.diabetesPedigreeFunction = diabetesPedigreeFunction;
        this.age = age;
        this.diabetesPositive = diabetesPositive;
    }

    public int getTimesPregnant() {
        return timesPregnant;
    }

    public void setTimesPregnant(int timesPregnant) {
        this.timesPregnant = timesPregnant;
    }

    public int getPlasmaGlucose() {
        return plasmaGlucose;
    }

    public void setPlasmaGlucose(int plasmaGlucose) {
        this.plasmaGlucose = plasmaGlucose;
    }

    public int getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(int bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public int getTricepsSkinThickness() {
        return tricepsSkinThickness;
    }

    public void setTricepsSkinThickness(int tricepsSkinThickness) {
        this.tricepsSkinThickness = tricepsSkinThickness;
    }

    public int getSerumInsulin() {
        return serumInsulin;
    }

    public void setSerumInsulin(int serumInsulin) {
        this.serumInsulin = serumInsulin;
    }

    public float getBodyMassIndex() {
        return bodyMassIndex;
    }

    public void setBodyMassIndex(float bodyMassIndex) {
        this.bodyMassIndex = bodyMassIndex;
    }

    public float getDiabetesPedigreeFunction() {
        return diabetesPedigreeFunction;
    }

    public void setDiabetesPedigreeFunction(float diabetesPedigreeFunction) {
        this.diabetesPedigreeFunction = diabetesPedigreeFunction;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isDiabetesPositive() {
        return diabetesPositive;
    }

    public void setDiabetesPositive(boolean diabetesPositive) {
        this.diabetesPositive = diabetesPositive;
    }

    /*@Override
    public String toString() {
        return "IndiaDiabetes{" +
                "timesPregnant=" + timesPregnant +
                ", plasmaGlucose=" + plasmaGlucose +
                ", bloodPressure=" + bloodPressure +
                ", tricepsSkinThickness=" + tricepsSkinThickness +
                ", serumInsulin=" + serumInsulin +
                ", bodyMassIndex=" + bodyMassIndex +
                ", diabetesPedigreeFunction=" + diabetesPedigreeFunction +
                ", age=" + age +
                ", diabetesPositive=" + diabetesPositive +
                '}';
    }*/
}
