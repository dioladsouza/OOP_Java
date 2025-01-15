package main;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;

public class GardenSimulationAPI{


    private PrintWriter printWriter;
    private FileWriter fileWriter;
    private StringWriter stringWriter;
    private Garden garden;
    private List<String> parasites;

    public GardenSimulationAPI() throws IOException {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        // We don't want to have log file from previous
        fileWriter = new FileWriter("log.txt", false);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("");
        printWriter.close();
        garden = new Garden(this);
        parasites = Arrays.asList("cryptosporidium", "giardia", "tapeworm", "hookworm", "guinea worm");
    }

    public void initializeGarden() {
        garden.initializeGarden();

    }
    public void pestControl() {
        garden.pestControl();
    }

    public void addPlant(String plantName, int waterReq, int minTemp, int maxTemp, List<String> plantParasites) {
        garden.addPlant(plantName, waterReq, minTemp, maxTemp, plantParasites);
    }
    // list of all the parasites
    public List<String> getAllParasites() {
        return parasites;
    }

    public Map<String, Object> getPlants() {
        Map<String, Object> plantInfo = new HashMap<>();
        List<String> plantNames = new ArrayList<>();
        List<Integer> waterReqs = new ArrayList<>();
        List<List<String>> badParasites = new ArrayList<List<String>>();
        for(Plant e: garden.allPlantObjects()) {
            plantNames.add(e.getName());
            waterReqs.add(e.getWaterRequirement());
            badParasites.add(e.getParasites());
        }
        plantInfo.put("plants", plantNames);
        plantInfo.put("waterRequirement", waterReqs);
        plantInfo.put("parasites", badParasites);
        return plantInfo;
    }

    public void rain(int amount) {
        logAction("Event: rain");
        garden.rain(amount);
    }
    public void rain() {
        Random random = new Random();
        int rainDrops = random.nextInt(15) + 15;
        rain(rainDrops);
    }


    public void temperature(int temp) {
        logAction("Event: temperature");
        garden.temperature(temp);
    }

    public void parasite(String parasite) {
        logAction("Event: parasite");
        garden.parasite(parasite);
    }
    public void parasite() {
        String parasite = getRandomParasite();
        garden.parasite(parasite);
    }


    public String getState() {
        String state = "Current garden state:\n";
        for (Plant plant : garden.allPlantObjects()) {
            if (!plant.isDead()) {
                state += "Plant " + plant + " is alive with health " + plant.getHealth() + "\n";
            } else {
                state += "Plant " + plant + " died on day " + plant.getDayOfDeath() + ")\n";
            }
        }
        logAction(state); // Log the current state
        return state;
    }


    public String getLog() {
        return stringWriter.toString();
    }

    public void logAction(String action) {
        String time = "[" + LocalDateTime.now() + " ]: ";
        String actAction = time + "Day " + garden.getDay() + ": " + action;
        printWriter.println(actAction);
        printWriter.flush();
        // needs to be added to log file
        try (FileWriter fileWriter = new FileWriter("log.txt", true)) {
            PrintWriter fileLogWriter = new PrintWriter(fileWriter);
            fileLogWriter.println(actAction);
            fileLogWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simulateDay() {
        Random random = new Random();
        int events = 3;
        for (int i = 0; i < events; i++) {
            int eventType = random.nextInt(4);
            switch (eventType) {
                case 0:
                    parasite(getRandomParasite());
                    break;
                case 1:
                    rain(15);
                    break;
                case 2:
                    temperature(random.nextInt(35) + 50);
                    break;
                case 3:
                    rain(30);
                    break;

            }
        }

    }

    public void irrigate() {
        garden.irrigate();
    }

    public void fertilize() {
        garden.fertilize();
    }
    public String getRandomParasite() {
        Random random = new Random();
        return getAllParasites().get(random.nextInt(getAllParasites().size()));

    }



    public boolean areAllPlantsDead() {
        return garden.areAllPlantsDead();
    }


    public List<Plant> allPlantObjects() {
        return garden.allPlantObjects();
    }

    public void saveLog() throws IOException {
        try (FileWriter fileWriter = new FileWriter("log.txt", false)) { // Overwrite the file
            fileWriter.write(stringWriter.toString());
        }
    }

    public static void main(String[] args) throws IOException {
        GardenSimulationAPI api = new GardenSimulationAPI();
        api.initializeGarden();

        // Example simulation script
        while (!api.areAllPlantsDead()) {
            api.simulateDay();
            api.getState();
        }
        api.saveLog();
    }
}
