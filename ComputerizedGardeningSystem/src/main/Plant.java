package main;

import java.util.List;

import java.util.ArrayList;

public class Plant {
    private String name;
    private List<String> parasites;
    private int[] tempTolerance; // [minTemp, maxTemp]
    private int waterRequirement; // Amount of water required each day
    private int health; // Health points for a plant
    private boolean dead;
    private int waterLevel;
    private int dayOfDeath;
    private boolean pestResistant;


    // Parameterized constructor
    public Plant(String name, List<String> parasites, int[] tempTolerance, int waterRequirement, int health) {
        this.name = name;
        this.parasites = new ArrayList<>(parasites);
        this.tempTolerance = tempTolerance.clone();
        this.waterRequirement = waterRequirement;
        this.health = 100;
        this.dead = false;
        this.waterLevel = this.waterRequirement * 2;
        this.dayOfDeath = -1;
        this.pestResistant = false;
    }

    // Getters and Setters

    public boolean isPestResistant() {
        return pestResistant;
    }

    public void setPestResistant(boolean pestResistant) {
        this.pestResistant = pestResistant;
    }

    public int getDayOfDeath() {
        return dayOfDeath;
    }
    public void setDayOfDeath(int dayOfDeath) {
        this.dayOfDeath = dayOfDeath;
    }
    public int getWaterLevel() {
        return waterLevel;
    }
    public void setWaterLevel(int waterLevel){
        this.waterLevel = waterLevel;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParasites() {
        return new ArrayList<>(parasites);
    }

    public void setParasites(List<String> parasites) {
        this.parasites = new ArrayList<>(parasites);
    }

    public int[] getTempTolerance() {
        return tempTolerance.clone();
    }

    public void setTempTolerance(int[] tempTolerance) {
        this.tempTolerance = tempTolerance.clone();
    }

    public int getWaterRequirement() {
        return waterRequirement;
    }

    public void setWaterRequirement(int waterRequirement) {
        this.waterRequirement = waterRequirement;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        this.dead = this.health == 0;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }






    @Override
    public String toString() {
        return "Plant{name='" + name + '\'' +
                ", parasites=" + parasites +
                ", tempTolerance=[" + tempTolerance[0] + ", " + tempTolerance[1] + "]" +
                ", waterRequirement=" + waterRequirement +
                ", health=" + health +
                ", dead=" + dead +
                '}';
    }
}
