package es.horm.cart.example.data;

import es.horm.cart.lib.annotation.InputField;
import es.horm.cart.lib.annotation.OutputField;

public class BostonHousing {

    @InputField
    private float crim;
    @InputField
    private float zn;
    @InputField
    private float indus;
    @InputField
    private float chas;
    @InputField
    private float nox;
    @InputField
    private float rm;
    @InputField
    private float age;
    @InputField
    private float dis;
    @InputField
    private float rad;
    @InputField
    private float tax;
    @InputField
    private float ptratio;
    @InputField
    private float b;
    @InputField
    private float lstat;
    @OutputField
    private float medv;

    public BostonHousing() {
    }

    public float getCrim() {
        return crim;
    }

    public void setCrim(float crim) {
        this.crim = crim;
    }

    public float getZn() {
        return zn;
    }

    public void setZn(float zn) {
        this.zn = zn;
    }

    public float getIndus() {
        return indus;
    }

    public void setIndus(float indus) {
        this.indus = indus;
    }

    public float getChas() {
        return chas;
    }

    public void setChas(float chas) {
        this.chas = chas;
    }

    public float getNox() {
        return nox;
    }

    public void setNox(float nox) {
        this.nox = nox;
    }

    public float getRm() {
        return rm;
    }

    public void setRm(float rm) {
        this.rm = rm;
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }

    public float getDis() {
        return dis;
    }

    public void setDis(float dis) {
        this.dis = dis;
    }

    public float getRad() {
        return rad;
    }

    public void setRad(float rad) {
        this.rad = rad;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public float getPtratio() {
        return ptratio;
    }

    public void setPtratio(float ptratio) {
        this.ptratio = ptratio;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getLstat() {
        return lstat;
    }

    public void setLstat(float lstat) {
        this.lstat = lstat;
    }

    public float getMedv() {
        return medv;
    }

    public void setMedv(float medv) {
        this.medv = medv;
    }
}
