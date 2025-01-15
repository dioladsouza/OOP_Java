package main;

import java.util.Random;

public class PestControl {

    private Garden garden;

    public PestControl(Garden garden) {
        this.garden = garden;

    }


    public void pestControl() {
        garden.logAction("Pest control applied.");
        for (Plant plant : garden.allPlantObjects()) {
            if (!plant.isDead()) {
                int newHealthLevel = plant.getHealth() - new Random().nextInt(3);
                plant.setHealth(newHealthLevel);
                plant.setPestResistant(true);
                garden.logAction("Pest control: " + plant.getName() + " has new health level of " + newHealthLevel + " points.");
                //System.out.println(plant.getHealth() + " is the new level of health");
            }
        }
    }


}