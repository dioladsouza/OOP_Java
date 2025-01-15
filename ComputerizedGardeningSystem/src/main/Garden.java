package main;

import java.io.*;
import java.util.*;

public class Garden {

    private List<Plant> allPlants;


    private StringWriter stringWriter;
    private FileWriter fileWriter;
    private IrrigationSystem irrigationSystem;
    private PestControl pestControl;
    private MonitoringSystem monitoringSystem;
    private FertilizationSystem fertilizationSystem;
    private GardenSimulationAPI gardenSimulationAPI;

    public Garden(GardenSimulationAPI gardenSimulationAPI) throws IOException {

        stringWriter = new StringWriter();

        // Clear the log file at the start
        clearLogFile();
        this.gardenSimulationAPI = gardenSimulationAPI;
    }



    private void clearLogFile() throws IOException {
        fileWriter = new FileWriter("log.txt", false);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("");
        printWriter.close();
    }

    public void initializeGarden() {
        // Read from configuration file.
        String filePath = "src/config.txt";
        allPlants = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(", ");

                String name = parts[0];
                int lowTemp = Integer.parseInt(parts[1]);
                int highTemp = Integer.parseInt(parts[2]);
                int waterRequirement = Integer.parseInt(parts[3]);
                List<String> parasites = new ArrayList<>();

                // Check for "none" in the parasites list
                if (parts.length > 4 && !parts[4].equals("none")) {
                    for (int i = 4; i < parts.length; i++) {
                        parasites.add(parts[i]);
                    }
                }

                int[] tempTolerance = {lowTemp, highTemp};
                Plant plant = new Plant(name, parasites, tempTolerance, waterRequirement, 100);
                allPlants.add(plant);
            }
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        }
        irrigationSystem = new IrrigationSystem(this);
        pestControl = new PestControl(this);
        fertilizationSystem = new FertilizationSystem(this);
        monitoringSystem = new MonitoringSystem(this, fertilizationSystem, pestControl, irrigationSystem);
        monitoringSystem.startSystems();
        logAction("Garden initialized with plants: " + getPlants().get("plants"));
    }
    public boolean isRainedHeavily() {
        return monitoringSystem.isRainedHeavily();
    }
    public void setRainedHeavily(boolean rainedHeavily) {
        monitoringSystem.setRainedHeavily(rainedHeavily);
    }
    public void pestControl() {
        logAction("Pest control applied.");
        //System.out.println("Pest control applied");
        for (Plant plant : allPlants) {
            if (!plant.isDead()) {
                int newHealthLevel = plant.getHealth() - new Random().nextInt(3);
                plant.setHealth(newHealthLevel);
                plant.setPestResistant(true);
                logAction("Pest control: " + plant.getName() + " has new health level of " + newHealthLevel + " points.");

            }
        }
    }

    public void addPlant(String plantName, int waterReq, int minTemp, int maxTemp, List<String> plantParasites) {
        Plant newPlant = new Plant(plantName, plantParasites, new int[] {minTemp, maxTemp}, waterReq, 100);
        allPlants.add(newPlant);
        logAction("Added new plant: " + plantName + " with water requirement: " + waterReq + ", temperature tolerance: " + minTemp + "-" + maxTemp + ", and parasites: " + plantParasites);
    }

    public Map<String, Object> getPlants() {
        return gardenSimulationAPI.getPlants();
    }

    public void rain(int amount) {
        for (Plant plant : allPlants) {
            if (!plant.isDead()) {
                int newWaterLevel = plant.getWaterLevel() + amount;
                plant.setWaterLevel(newWaterLevel);
                monitoringSystem.detectRainfall(amount);
                logAction("Rain: " + amount + " units for plant " + plant.getName());
            }
        }
    }

    public void temperature(int temp) {
        logAction("Temperature is " + temp + " degrees");
        for (Plant plant: allPlants) {
            if (!plant.isDead()) {
                int[] range = plant.getTempTolerance();
                if (temp < range[0] || temp > range[1]) {
                    int healthReduction = 5;
                    plant.setHealth(plant.getHealth() - healthReduction);
                    logAction("Plant " + plant + " health decreased due to improper temperature.");
                }
                else {
                    int healthIncrease = 5;
                    plant.setHealth(plant.getHealth() + healthIncrease);
                    logAction("Plant " + plant + "health increased due to right temperature.");
                }
                if (plant.getHealth() <= 0) {
                    plant.setDead(true);
                    plant.setDayOfDeath(getDay());
                    logAction("Plant " + plant + " died due to extreme conditions.");
                }
            }
        }
    }

    public void parasite(String parasite) {
        for (Plant plant : allPlants) {
            if (!plant.isDead() && plant.getParasites().contains(parasite)) {
                if (plant.isPestResistant()) {
                    plant.setPestResistant(false);
                    logAction("Pest control protected Parasite " + parasite + " against " + plant.getName());
                }
                else {
                    int newHealth = plant.getHealth() - 5;
                    plant.setHealth(newHealth);
                    logAction("Parasite " + parasite + " affected plant " + plant.getName());
                }

                if (plant.getHealth() <= 0) {
                    plant.setDead(true);
                    plant.setDayOfDeath(getDay());
                    logAction("Plant " + plant.getName() + " died due to parasite infestation.");
                }
            }
        }
    }

    public int getDay() {
        return monitoringSystem.getDay();
    }

    public void logAction(String action) {
        gardenSimulationAPI.logAction(action);
    }

    public void irrigate() {
        irrigationSystem.irrigating();
    }

    public void fertilize() {
        fertilizationSystem.fertilize();
    }


    public boolean areAllPlantsDead() {
        for (Plant plant: allPlants) {
            if (!plant.isDead()) {
                return false;
            }
        }
        return true;
    }
    public List<Plant> allPlantObjects() {
        return this.allPlants;
    }


    public int waterNeeded() {
        return monitoringSystem.waterNeeded();
    }
}

