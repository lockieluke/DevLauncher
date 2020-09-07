package Main;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    List<String> paths = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("DevLauncher");
        stage.setWidth(1280);
        stage.setHeight(720);

        Pane parent = new Pane();

        Label header = new Label("DevLauncher");
        header.setTextAlignment(TextAlignment.CENTER);
        header.setFont(Font.font("SF Pro Display", 20));
        header.setLayoutX(50);
        header.setLayoutY(50);
        header.setPrefWidth(stage.getWidth() - 150);
        Label description = new Label("Launch your IDE and Code Editor in one place");
        description.setTextAlignment(TextAlignment.CENTER);
        description.setFont(Font.font("SF Pro Display", 10));
        description.setLayoutX(50);
        description.setLayoutY(80);
        description.setPrefWidth(stage.getWidth() - 150);
        ListView ides = new ListView();
        ides.setPrefWidth(stage.getWidth() - 100);
        ides.setPrefHeight(stage.getHeight() - 250);
        ides.setLayoutX(50);
        ides.setLayoutY(120);
        ides.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        InsertIDE(ides);
        Button addButton = new Button("Add IDE/Code Editor");
        addButton.setPrefWidth(130);
        addButton.setPrefHeight(stage.getHeight() - 1200);
        addButton.setLayoutY(stage.getHeight() - 100);
        addButton.setLayoutX(50);
        addButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Choose IDE/Code Editor Executable");
                    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Executable (*.exe)", "*.exe");
                    fileChooser.getExtensionFilters().add(extensionFilter);
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null && file.exists() && file.isFile()) {
                        paths.add(file.getPath());
                        if (!ides.getItems().contains(file.getName().substring(0, file.getName().length() - 4)) && !paths.contains(file.getName().substring(0, file.getName().length() - 4))) {
                            ides.getItems().add(file.getName().substring(0, file.getName().length() - 4));
                        } else {
                            Alert duplicateAlert = new Alert(Alert.AlertType.WARNING);
                            duplicateAlert.setTitle("DevLauncher");
                            duplicateAlert.setHeaderText("Attention");
                            duplicateAlert.setContentText("There is already an executable named\n " + file.getName().substring(0, file.getName().length() - 4) + " in the list.\nRemove it if you want to add a new one\n with a same name.");
                            duplicateAlert.showAndWait();
                        }
                    }
                }
            }
        });
        Button launchButton = new Button("Launch");
        launchButton.setPrefWidth(130);
        launchButton.setPrefHeight(stage.getHeight() - 1200);
        launchButton.setLayoutX(stage.getWidth() - 175);
        launchButton.setLayoutY(stage.getHeight() - 100);
        launchButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(new File(paths.get(ides.getSelectionModel().getSelectedIndex())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        parent.getChildren().addAll(header, description, ides, addButton, launchButton);

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNIFIED);

        stage.show();

        ChangeListener<Number> stageSizeListener = ((observableValue, number, t1) -> {
           ides.setPrefWidth(stage.getWidth() - 100);
           ides.setPrefHeight(stage.getHeight() - 250);
           header.setPrefWidth(stage.getWidth() - 150);
           description.setPrefWidth(stage.getWidth() - 150);
            if (stage.getWidth() < 200) {
                addButton.setPrefWidth(stage.getWidth() - 120);
                launchButton.setPrefWidth(100);
            } else {
                addButton.setPrefWidth(130);
                launchButton.setPrefWidth(130);
            }
            addButton.setPrefHeight(stage.getHeight() - 1200);
            addButton.setLayoutY(stage.getHeight() - 100);
            launchButton.setPrefWidth(130);
            launchButton.setPrefHeight(stage.getHeight() - 1200);
            launchButton.setLayoutX(stage.getWidth() - 175);
            launchButton.setLayoutY(stage.getHeight() - 100);
        });

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    void InsertIDE(ListView listview) {
        System.out.println("Scanning for IDEs/Code Editors on your computer...");
        File products = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\JetBrains Toolbox");
        ObservableList<String> names = FXCollections.observableArrayList();
        for (File product : products.listFiles()) {
            names.add(product.getName().substring(0, product.getName().length() - 4));
            paths.add(product.getPath());
            System.out.println("Adding " + product.getName().substring(0, product.getName().length() - 4) + " to the list");
        }
        products = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Visual Studio Code");
        for (File product : products.listFiles()) {
            names.add(product.getName().substring(0, product.getName().length() - 4));
            paths.add(product.getPath());
            System.out.println("Adding " + product.getName().substring(0, product.getName().length() - 4) + " to the list");
        }
        for (int i = 0; i < names.toArray().length; i++) {
            if (names.get(i).equals("")) {
                names.remove(i);
                paths.remove(i);
            }
        }
        listview.setCellFactory(listView -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            int iconIndex = 0;
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name.isBlank() || name.isEmpty()) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (iconIndex != names.size()) {
                        int currentIndex = names.indexOf(name);
                        imageView.setFitWidth(20);
                        imageView.setFitHeight(20);
                        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(new File(paths.get(currentIndex)));
                        icon = new SafeIcon(icon);
                        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics graphics = image.createGraphics();
                        icon.paintIcon(null, graphics, 0, 0);
                        graphics.dispose();
                        imageView.setImage(SwingFXUtils.toFXImage(image, null));
                        setText(name);
                        setGraphic(imageView);
                        System.out.println("Updating icon for " + name);
                        iconIndex++;
                    }
                }
            }
        });
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (listview.getSelectionModel().getSelectedIndex() == 0) {
                    listview.getItems().remove(0);
                    paths.remove(0);
                } else {
                    listview.getItems().remove(listview.getSelectionModel().getSelectedItem());
                    paths.remove(listview.getSelectionModel().getSelectedIndex() + 1);
                }
                System.out.println("Removing " + listview.getSelectionModel().getSelectedIndex());
            }
        });
        contextMenu.getItems().add(removeItem);
        listview.setItems(names);
        listview.setContextMenu(contextMenu);
        listview.getSelectionModel().select(0);
    }

    @Override
    public void init() throws Exception {

    }
}
