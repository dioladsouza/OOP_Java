package main;


public class FertilizationSystem {

    private Garden garden;

    public FertilizationSystem(Garden garden) {
        this.garden = garden;
    }


    public void fertilize() {
        for (Plant plant: garden.allPlantObjects()) {
            if (!plant.isDead()) {
                plant.setHealth(plant.getHealth() + 5);
            }
        }
        garden.logAction("Fertilzation completed.");
    }
}
