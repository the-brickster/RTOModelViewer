package com.rituals.main.controllers;


import com.rituals.main.JMEApp;
import com.rituals.main.controllers.utils.DragResizerXY;
import com.rituals.main.model.itemBoneProperties;
import com.rituals.main.model.itemProperties;
import io.datafx.controller.ViewController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


@ViewController(value = "/fxml/main.fxml", title = "Main Controller")
public class MainController implements Initializable{


    @FXML
    private BorderPane rootNode;

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem newItem;

    @FXML
    private MenuItem loadItem;

    @FXML
    private MenuItem baseModelSelect;

    @FXML
    private MenuItem closeApplication;

    @FXML
    private StackPane imageViewStackPane;

    @FXML
    private ImageView jmeImageView;

    @FXML
    private AnchorPane sideAnchorPane;

    @FXML
    private ListView<String> boneList;

    @FXML
    private ListView<?> animationsList;

    @FXML
    private ListView<itemBoneProperties> itemLayoutList;

    @FXML
    private Button addNewItemLayout;

    static ObservableList<itemBoneProperties> itemObservableList = FXCollections.observableArrayList();

    static {
        itemObservableList.addAll(new itemBoneProperties("Bone1",1,1,1,2,2,2,3,3,3),
                new itemBoneProperties("Bone2",1,1,1,2,2,2,3,3,3),
                new itemBoneProperties("Bone3",1,1,1,2,2,2,3,3,3),
                new itemBoneProperties("Bone4",1,1,1,2,2,2,3,3,3),
                new itemBoneProperties("Bone5",1,1,1,2,2,2,3,3,3),
                new itemBoneProperties("Bone6",1,1,1,2,2,2,3,3,3),
                new itemBoneProperties("Bone7",1,1,1,2,2,2,3,3,3),
                new itemBoneProperties("Bone8",1,1,1,2,2,2,3,3,3));
    }

    private File itemModel = null;
    public TextField itemPath;
    public itemProperties itemProperties;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jmeImageView.fitHeightProperty().bind(imageViewStackPane.heightProperty());
        jmeImageView.fitWidthProperty().bind(imageViewStackPane.widthProperty());
        DragResizerXY.makeResizable(sideAnchorPane);

        newItem.setOnAction(this::loadItemModel);
        boneList.setOnMouseClicked(this::boneListElementOnSelect);
        jmeImageView.setOnMouseEntered(this::imageViewMouseEntered);

        this.itemLayoutList.setEditable(true);
        this.itemLayoutList.setItems(itemObservableList);
        this.itemLayoutList.setCellFactory(e -> new ItemPropViewCell());

        this.itemLayoutList.setOnEditCommit(event -> {
            System.out.println("Test");
        });
    }

    public ImageView getJmeImageView() {
        return jmeImageView;
    }

    public void imageViewMouseEntered(MouseEvent event){
        JMEApp app = JMEApp.getInstance();
        jmeImageView.requestFocus();
    }

    public void boneListElementOnSelect(MouseEvent event){
        JMEApp app = JMEApp.getInstance();
        String value = this.boneList.getSelectionModel().getSelectedItem();
        if(value!=null){
            app.changeBonePonter(value);
        }

    }

    @FXML
    public void loadBaseModel(ActionEvent event){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Base Skeleton Model");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("j3o","*.j3o"));
        File baseModelo = chooser.showOpenDialog(imageViewStackPane.getScene().getWindow());
        if(baseModelo != null){
            JMEApp app = JMEApp.getInstance();
            app.addBaseModel(baseModelo.getName(),baseModelo.getParent());
            this.boneList.getItems().addAll(app.getBones());
        }
    }

    public void loadModel(ActionEvent event){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Item Model");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("j3o","*.j3o"));
        File itemModelFile = chooser.showOpenDialog(imageViewStackPane.getScene().getWindow());
        if(itemModelFile != null){
            System.out.println(itemModelFile);
            this.itemModel = itemModelFile;
            this.itemPath.setText(this.itemModel.toString());
        }
    }

    @FXML
    public void loadItemModel(ActionEvent event){
        Stage owner = (Stage)imageViewStackPane.getScene().getWindow();

        Wizard wizard = new Wizard(owner);
        wizard.setTitle("New Item");

        // Page 1
        WizardPane page1 = new WizardPane() {
            ValidationSupport vs = new ValidationSupport();
            {
                vs.initInitialDecoration();

                int row = 0;

                GridPane page1Grid = new GridPane();
                page1Grid.setVgap(10);
                page1Grid.setHgap(10);

                page1Grid.add(new Label("Item Name:"), 0, row);
                TextField itemName = createTextField("ItemName");
                vs.registerValidator(itemName, Validator.createEmptyValidator("ItemName is required"));
                page1Grid.add(itemName, 1, row++);

                page1Grid.add(new Label("Item Model Path:"), 0, row);
                MainController.this.itemPath = createTextField("itemModelLoc");
                page1Grid.add(itemPath , 1, row++);

                Button itemBrowse = createBut("itemBrowse","Browse");
                itemBrowse.setOnAction(MainController.this::loadModel);
                page1Grid.add(itemBrowse,0,row);

                setContent(page1Grid);
            }

        };

        // --- page 2
        final WizardPane page2 = new WizardPane() {
            @Override public void onEnteringPage(Wizard wizard) {
                String itemName = (String) wizard.getSettings().get("ItemName");
                String itemLoc = (String) wizard.getSettings().get("itemModelLoc");

                setContentText("New item name: " + itemName + "\n Item Location: " +itemLoc);
            }
        };
        page2.setHeaderText("New Item Details:");

        // create wizard
        wizard.setFlow(new Wizard.LinearFlow(page1,page2));

        // show wizard and wait for response
        wizard.showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                System.out.println("Wizard finished, settings: " + wizard.getSettings().keySet());
            }
        });
    }

    @FXML
    void closeApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    private TextField createTextField(String id) {
        TextField textField = new TextField();
        textField.setId(id);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        return textField;
    }
    private Button createBut(String id, String value){
        Button button = new Button();
        button.setText(value);
        button.setId(id);
        GridPane.setHgrow(button,Priority.ALWAYS);
        return button;
    }
}
