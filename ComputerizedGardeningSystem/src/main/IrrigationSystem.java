package main;

import java.util.List;

public class IrrigationSystem {
    private Garden garden;


    public IrrigationSystem(Garden garden) {
        this.garden = garden;
    }


    public void irrigating() {
        if (garden.isRainedHeavily()) {
            garden.setRainedHeavily(false);  //reset for next time
        }
        else {
            List<Plant> allPlants = garden.allPlantObjects();
            garden.logAction("Irrigation activated.");
            int waterAmt = garden.waterNeeded();
            for (Plant plant : allPlants) {
                if (!plant.isDead()) {
                    plant.setWaterLevel(plant.getWaterLevel() + waterAmt);
                    garden.logAction("Irrigation: " + plant.getName() + " water level increased by " + waterAmt);
                }
            }
        }

    }


}
