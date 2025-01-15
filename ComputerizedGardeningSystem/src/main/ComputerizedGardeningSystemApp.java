package main;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.*;

public class ComputerizedGardeningSystemApp extends Application {

    private static final String APP_BACKGROUND_COLOR = "green";
    private static final String CONTROL_AREA_COLOR = "rgba(255,255,199)";
    private static final String LOG_AREA_COLOR = "rgba(255,255,199)";
    private static final String GARDEN_GRID_COLOR = "rgba(15, 205, 15, 0.3)";

    private GardenSimulationAPI gardenManager;
    private TextArea eventLogger;
    private GridPane gardenGrid;

    @Override
    public void start(Stage primaryStage) throws Exception {
        gardenManager = new GardenSimulationAPI();
        gardenManager.initializeGarden();

        // Set up the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + APP_BACKGROUND_COLOR + ";");
        // Set up title
        mainLayout.setTop(createHeaderBox());

        // Set up the garden grid
        gardenGrid = new GridPane();
        gardenGrid.setStyle("-fx-background-color: " + GARDEN_GRID_COLOR + "; " +
                "-fx-border-color: green; " +
                "-fx-border-width: 7px;");
        initializeGardenGrid();
        mainLayout.setCenter(createGardenScrollPane());

        VBox controlArea = createControlArea();
        mainLayout.setRight(controlArea);
        // Set up the log area and buttons
        mainLayout.setBottom(createLogArea());
        // Set up the scene and stage
        Scene appScene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Gardening System");
        primaryStage.setScene(appScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        startSimulation();
    }

    private HBox createHeaderBox() {
        Label appTitle = new Label("Gardening System");
        appTitle.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox headerBox = new HBox(appTitle);
        headerBox.setStyle("-fx-alignment: center;");
        return headerBox;
    }

    private ScrollPane createGardenScrollPane() {
        ScrollPane scrollableGarden = new ScrollPane(gardenGrid);
        scrollableGarden.setFitToWidth(true);
        scrollableGarden.setFitToHeight(true);
        return scrollableGarden;
    }

    private VBox createControlArea() {
        VBox controlPanel = new VBox(10);
        controlPanel.setStyle("-fx-background-color: " + CONTROL_AREA_COLOR + "; " +
                "-fx-border-color: green; " +
                "-fx-border-width: 2px;");
        controlPanel.setPrefWidth(600);

        Label controlTitle = new Label();
        controlTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: green");
        VBox controlTitleBox = new VBox(controlTitle);
        controlTitleBox.setStyle("-fx-alignment: center;");
        controlPanel.getChildren().add(controlTitleBox);

        GridPane buttonGrid = new GridPane();
        buttonGrid.setPrefWidth(200);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setStyle("-fx-background-color: rgba(173, 216, 230); " +
                "-fx-border-color: blue; " +
                "-fx-border-width: 2px;");

        buttonGrid.add(createActionButton("Rain", e -> gardenManager.rain()), 0, 0);
        buttonGrid.add(createActionButton("Parasite", e -> gardenManager.parasite(gardenManager.getRandomParasite())), 0, 1);
        buttonGrid.add(createActionButton("Pest Control", e -> gardenManager.pestControl()), 0, 2);
        buttonGrid.add(createActionButton("Irrigation", e -> gardenManager.irrigate()), 0, 3);
        buttonGrid.add(createActionButton("Fertilize", e -> gardenManager.fertilize()), 0, 4);

        VBox addPlantPanel = createAddPlantPanel();
        HBox combinedPanel = new HBox(10);
        combinedPanel.setAlignment(Pos.CENTER);
        combinedPanel.getChildren().addAll(addPlantPanel, buttonGrid);
        controlPanel.getChildren().add(combinedPanel);

        return controlPanel;
    }

    private Button createActionButton(String label, EventHandler<ActionEvent> handler) {
        Label buttonLabel = new Label(label);
        buttonLabel.setStyle("-fx-font-weight: bold;");

        VBox buttonContent = new VBox(5);
        buttonContent.setStyle("-fx-alignment: center;");
        buttonContent.getChildren().addAll(buttonLabel);

        Button actionButton = new Button("", buttonContent);
        actionButton.setOnAction(handler);
        actionButton.setStyle("-fx-padding: 10px;");
        actionButton.setPrefWidth(150);
        return actionButton;
    }

    private VBox createAddPlantPanel() {
        VBox addPlantPanel = new VBox(10);
        addPlantPanel.setStyle("-fx-border-color: blue; -fx-border-width: 2px; -fx-background-color: rgba(173, 216, 230);");

        Label panelTitle = new Label("Add New Plant");
        panelTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black");

        TextField plantNameField = new TextField();
        plantNameField.setPromptText("Plant Name");
        plantNameField.setStyle("-fx-font-weight: bold;");

        TextField waterRequirementField = new TextField();
        waterRequirementField.setPromptText("Water Requirement");
        waterRequirementField.setStyle("-fx-font-weight: bold;");

        TextField minTemperatureField = new TextField();
        minTemperatureField.setPromptText("Min Temperature");
        minTemperatureField.setStyle("-fx-font-weight: bold;");

        TextField maxTemperatureField = new TextField();
        maxTemperatureField.setPromptText("Max Temperature");
        maxTemperatureField.setStyle("-fx-font-weight: bold;");

        // checkboxes for selecting parasites
        VBox parasiteSelectionBox = new VBox(5);
        Label parasiteLabel = new Label("Select Susceptible Parasites:");
        parasiteLabel.setStyle("-fx-font-weight: bold;");
        parasiteSelectionBox.getChildren().add(parasiteLabel);

        List<String> availableParasites = gardenManager.getAllParasites();
        List<CheckBox> parasiteCheckboxes = new ArrayList<>();

        for (String parasite : availableParasites) {
            CheckBox checkBox = new CheckBox(parasite);
            checkBox.setStyle("-fx-font-weight: bold;");
            parasiteCheckboxes.add(checkBox);
            parasiteSelectionBox.getChildren().add(checkBox);
        }

        Button addPlantButton = new Button("Add Plant");
        addPlantButton.setStyle("-fx-font-weight: bold;");
        addPlantButton.setOnAction(e -> {
            String plantName = plantNameField.getText();
            int waterRequirement = Integer.parseInt(waterRequirementField.getText());
            int minTemperature = Integer.parseInt(minTemperatureField.getText());
            int maxTemperature = Integer.parseInt(maxTemperatureField.getText());

            List<String> selectedParasites = new ArrayList<>();
            for (CheckBox checkBox : parasiteCheckboxes) {
                if (checkBox.isSelected()) {
                    selectedParasites.add(checkBox.getText());
                }
            }

            gardenManager.addPlant(plantName, waterRequirement, minTemperature, maxTemperature, selectedParasites);
            initializeGardenGrid();
            updateEventLog();
        });

        addPlantPanel.getChildren().addAll(
                panelTitle,
                plantNameField,
                waterRequirementField,
                minTemperatureField,
                maxTemperatureField,
                parasiteSelectionBox,
                addPlantButton
        );
        return addPlantPanel;
    }


    private VBox createLogArea() {
        HBox logBox = new HBox(10);
        logBox.setStyle("-fx-background-color: " + LOG_AREA_COLOR + "; " +
                "-fx-border-color: green; " +
                "-fx-border-width: 4px;");

        Label logTitle = new Label("Event Log");
        logTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green;");

        eventLogger = new TextArea();
        eventLogger.setEditable(false);
        eventLogger.setWrapText(true);
        eventLogger.setPrefHeight(180);
        eventLogger.setPrefWidth(1000);

        Button stateButton = new Button("Get State");
        stateButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        stateButton.setOnAction(e -> {
            String state = gardenManager.getState();
            gardenManager.logAction(state);
            updateEventLog();
        });

        Button logButton = new Button("Get Updated Log");
        logButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        logButton.setOnAction(e -> updateEventLog());

        logBox.getChildren().addAll(logTitle, stateButton, logButton);

        VBox logArea = new VBox(10);
        logArea.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-color: green; " +
                "-fx-border-width: 2px;");
        logArea.getChildren().addAll(logBox, eventLogger);
        return logArea;
    }

    private void updateEventLog() {
        eventLogger.setText(gardenManager.getLog());
    }

    private void initializeGardenGrid() {
        updateGardenGrid();
    }

    private void updateGardenGrid() {
        gardenGrid.getChildren().clear();
        List<Plant> allPlants = gardenManager.allPlantObjects();

        int r = 0;
        int c = 0;

        for (Plant plant : allPlants) {
            VBox plantBox = new VBox(5);
            plantBox.setStyle("-fx-border-color: green; -fx-border-width: 1px;");
            plantBox.setPrefWidth(150);
            HBox imageContainer = new HBox();
            imageContainer.setStyle("-fx-alignment: center;");

            Image plantImage = loadImage("/images/" + plant.getName().toLowerCase() + ".png");
            ImageView userImage = new ImageView(plantImage);
            userImage.setFitWidth(50);
            userImage.setFitHeight(50);
            imageContainer.getChildren().add(userImage);

            Label plantName = new Label(plant.getName());
            plantName.setWrapText(true);
            plantName.setMaxWidth(120);
            plantName.setStyle("-fx-font-weight: bold;");

            Label plantHealth = new Label("Health: " + plant.getHealth());
            plantHealth.setWrapText(true);
            plantHealth.setMaxWidth(120);

            Label plantWaterLevel = new Label("Water level: " + plant.getWaterLevel());
            plantWaterLevel.setWrapText(true);
            plantWaterLevel.setMaxWidth(120);

            if (plant.isDead()) {
                plantBox.setStyle("-fx-border-color: red; -fx-border-width: 2px; " +
                        "-fx-background-color: rgba(255, 0, 0, 0.3);");
            }

            plantBox.getChildren().addAll(imageContainer, plantName, plantHealth, plantWaterLevel);
            gardenGrid.add(plantBox, c, r);

            c++;
            if (c > 4) {
                c = 0;
                r++;
            }
        }
    }

    private Image loadImage(String imagePath) {
        Image plantImage;
        try {
            plantImage = new Image(getClass().getResourceAsStream(imagePath));
            if (plantImage.isError()) {
                throw new Exception("Image not found");
            }
        } catch (Exception e) {
            plantImage = new Image(getClass().getResourceAsStream("/images/generic.png"));
        }
        return plantImage;
    }

    private void startSimulation() {
        Timer simulationTimer = new Timer();
        simulationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!gardenManager.areAllPlantsDead()) {
                    gardenManager.simulateDay();
                    String log = gardenManager.getLog();

                    javafx.application.Platform.runLater(() -> {
                        updateGardenGrid();
                        eventLogger.setText(log);
                    });
                } else {
                    simulationTimer.cancel(); // Stop the timer when garden is done for
                }
            }
        }, 0, 3600000); // Run every 1 hour
    }

    public static void main(String[] args) {
        launch(args);
    }
}
