package es.horm.cart.example.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.horm.cart.lib.annotation.InputField;
import es.horm.cart.lib.annotation.OutputField;

@JsonIgnoreProperties(value = { "Name" })
public class TitanicData {

    @JsonProperty("Survived")
    @OutputField
    private boolean survived;
    @JsonProperty("Pclass")
    @InputField
    private int passengerClass;
    @JsonProperty("Sex")
    @InputField
    private Sex sex;
    @JsonProperty("Age")
    @InputField
    private float age;
    @JsonProperty("Siblings/Spouses Aboard")
    @InputField
    private int siblingsOrSpousesAboard;
    @JsonProperty("Parents/Children Aboard")
    @InputField
    private int parentsOrChildrenAboard;
    @JsonProperty("Fare")
    @InputField
    private float fare;

    public TitanicData() {
    }

    public boolean isSurvived() {
        return survived;
    }

    public void setSurvived(boolean survived) {
        this.survived = survived;
    }

    public int getPassengerClass() {
        return passengerClass;
    }

    public void setPassengerClass(int passengerClass) {
        this.passengerClass = passengerClass;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }

    public int getSiblingsOrSpousesAboard() {
        return siblingsOrSpousesAboard;
    }

    public void setSiblingsOrSpousesAboard(int siblingsOrSpousesAboard) {
        this.siblingsOrSpousesAboard = siblingsOrSpousesAboard;
    }

    public int getParentsOrChildrenAboard() {
        return parentsOrChildrenAboard;
    }

    public void setParentsOrChildrenAboard(int parentsOrChildrenAboard) {
        this.parentsOrChildrenAboard = parentsOrChildrenAboard;
    }

    public float getFare() {
        return fare;
    }

    public void setFare(float fare) {
        this.fare = fare;
    }
}
