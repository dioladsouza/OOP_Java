package main;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitoringSystem {
    private final Garden garden;
    private final FertilizationSystem fertilizationSystem;
    private final PestControl pestControl;
    private final IrrigationSystem irrigationSystem;
    private ScheduledExecutorService scheduler;
    private int day; // Counter to track the number of days
    private boolean rainedHeavily;

    public MonitoringSystem(Garden garden, FertilizationSystem fertilizationSystem, PestControl pestControl, IrrigationSystem irrigationSystem) {
        this.garden = garden;
        this.fertilizationSystem = fertilizationSystem;
        this.pestControl = pestControl;
        this.irrigationSystem = irrigationSystem;
        this.scheduler = Executors.newScheduledThreadPool(5);
        this.day = 0; // Initialize to zeroth day
        this.rainedHeavily = false;
    }

    public void startSystems() {
        scheduler.scheduleAtFixedRate(fertilizationSystem::fertilize, 4, 4, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(pestControl::pestControl, 5, 5, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(this::checkWaterLevels, 1, 1, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(this::incrementDay, 1, 1, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(irrigationSystem::irrigating, 2, 2, TimeUnit.HOURS)   ;
    }

    public void stopSystems() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    /** Increment the minute counter. */
    private void incrementDay() {
        day++;
        garden.logAction("Beginning of day " + day);
    }

    /** Monitor water levels and apply changes to plant health. */
    public void checkWaterLevels() {
        for (Plant plant : garden.allPlantObjects()) {
            if (plant.isDead()) {
                continue;
            }
            int waterReceived = plant.getWaterLevel();
            if (waterReceived < plant.getWaterRequirement()) {
                garden.logAction(plant.getName() + " needed " + plant.getWaterRequirement() + " and only had " +
                        plant.getWaterLevel() + " of water so it lost 5 health");
                plant.setHealth(plant.getHealth() - 5);
                if (plant.getHealth() <= 0) {
                    plant.setDead(true);
                }
                plant.setWaterLevel(0);
            }
            else {
                plant.setWaterLevel(plant.getWaterLevel() - plant.getWaterRequirement());
            }

        }
    }
    /** Calculate the maximum water required for the plants in the garden. */
    public int waterNeeded() {
        List<Plant> allPlantObjects = garden.allPlantObjects();
        int maxWaterRequirement = 0;
        for (Plant plant : allPlantObjects) {
            if (plant.getWaterRequirement() > maxWaterRequirement) {
                maxWaterRequirement = plant.getWaterRequirement();
            }
        }
        return maxWaterRequirement * 2; // Double to account for 2 days worth of water
    }

    /** Getter for minutes passed (if needed elsewhere). */
    public int getDay() {
        return day;
    }
    public void detectRainfall(int amt) {
        if (amt >= waterNeeded()) {
            rainedHeavily = true;
        }
    }
    public void setRainedHeavily(boolean rainedHeavily) {
        this.rainedHeavily = rainedHeavily;
    }

    public boolean isRainedHeavily() {
        return rainedHeavily;
    }
}
