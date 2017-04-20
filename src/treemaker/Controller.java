package treemaker;

import Jama.Matrix;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Controller {

    private static final double MAX_CONSISTENCY_RATIO = 0.1;
    private static final double[] RANDOM_INDEXES = new double[] {0.5, 0.5247, 0.8816, 1.1086}; // 2,3,4,5

    private List<HBox> hBoxList = new ArrayList<>();
    private CustomTextField lastModified;

    @FXML
    private CustomTextField root;

    @FXML
    private BorderPane masterPane;

    @FXML
    private VBox hboxContainer;

    @FXML
    private ChoiceBox<Integer> alternatives;

    @FXML
    private GridPane valuesGridPane;

    public void initialize() {
        root.setContextMenu(new ContextMenu());
        alternatives.getItems().setAll(FXCollections.observableArrayList(2,3,4,5));
        alternatives.getSelectionModel().select(1);

        for(Node child : hboxContainer.getChildren()) {
            hBoxList.add((HBox)child);
        }

        HBox newContainer = new HBox();
        newContainer.setPrefWidth(2000);
        newContainer.setMaxWidth(2000);
        newContainer.setPrefHeight(100);

        root.setChildren(newContainer);
        hBoxList.get(1).getChildren().add(newContainer);
    }

    private void addChild(CustomTextField textField) {
        int currentHBoxId = extractCurrentHboxId(textField.getParent().getParent().getId());
        //System.out.println("Current hbox id: "+currentHBoxId);

        if(textField.hasNoChildren()) {
//            System.out.println("parent width: "+textField.getWidth());
//            System.out.println("parent maxwidth: "+textField.getMaxWidth());
//            System.out.println("parent prefwidth: "+textField.getPrefWidth());

            HBox newContainer = new HBox();
            newContainer.setPrefWidth(textField.getMaxWidth());
            newContainer.setMaxWidth(textField.getWidth());
            newContainer.setPrefHeight(textField.getHeight());
            hBoxList.get(++currentHBoxId).getChildren().add(newContainer);
            textField.setChildren(newContainer);
            System.out.println(currentHBoxId);
//            System.out.println("hboxparent width: "+hBoxList.get(currentHBoxId).getWidth());
//            System.out.println("hboxparent maxwidth: "+hBoxList.get(currentHBoxId).getMaxWidth());
//            System.out.println("hboxparent prefwidth: "+hBoxList.get(currentHBoxId).getPrefWidth());
//
//            System.out.println("hbox width: "+newContainer.getWidth());
//            System.out.println("hbox maxwidth: "+newContainer.getMaxWidth());
//            System.out.println("hbox prefwidth: "+newContainer.getPrefWidth());
        }

        HBox container = textField.getHBox();

        CustomTextField newChildTextField = new CustomTextField();
        newChildTextField.setContextMenu(new ContextMenu());
        newChildTextField.setOnMouseClicked(this::handleClick);
        newChildTextField.setMaxWidth(container.getPrefWidth());
        newChildTextField.setPrefWidth(container.getPrefWidth());
        newChildTextField.setPrefHeight(container.getPrefHeight());

        container.getChildren().add(newChildTextField);
    }

    private int extractCurrentHboxId(String id) {
        //System.out.println(id);
        return Integer.parseInt(id.split("_")[1]);
    }

    @FXML
    void handleClick(MouseEvent event) {
        lastModified = (CustomTextField)event.getSource();
        updateGridValues((CustomTextField)event.getSource());
        if(event.getButton() == MouseButton.SECONDARY) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem cut = new MenuItem("Add child");
            contextMenu.getItems().add(cut);
            cut.setOnAction(event1 -> addChild((CustomTextField)event.getSource()));
            contextMenu.show(masterPane, event.getScreenX(), event.getScreenY());
        }
    }

    @FXML
    void saveNode(ActionEvent event) {
        if(isInputConsistent()) {
            ((Button)event.getSource()).setText("Save");
            String resultRatios = getValuesFromGridPaneAsString();
            lastModified.setText(lastModified.getText() + ": " + resultRatios);
        } else {
            ((Button)event.getSource()).setText("INPUT EXCEEDS MAXIMUM ALLOWED CONSISTENCY RATIO! CHANGE VALUES");
        }
    }

    private boolean isInputConsistent() {
        return MAX_CONSISTENCY_RATIO >= computeConsistencyRatio();
    }

    private double computeConsistencyRatio() {
        List<Double> inputValues = getValuesFromGridPaneAsList();
        int n = getNumberOfAlternativesWhenComparisonCountEquals(inputValues.size());

        if(n<3) {
            return MAX_CONSISTENCY_RATIO;
        }

        double[][] arr = new double[n][n];
        for (double[] row: arr)
            Arrays.fill(row, 1.0);

        int i=0;
        for(int row=0; row<n-1; row++)
            for (int element=row+1; element<n; element++) {
                arr[row][element] = inputValues.get(i);
                arr[element][row] = 1 / inputValues.get(i);
                i++;
            }

        Matrix matrix = new Matrix(arr);
        for(double[] row : arr)
            System.out.println(Arrays.toString(row));

        double highestEigValue = 0.0;
        for (double value : matrix.eig().getRealEigenvalues())
            if (value > highestEigValue)
                highestEigValue = value;

        double consistencyIndex = (highestEigValue - n) / (n-1);

        return consistencyIndex / RANDOM_INDEXES[n-2];

    }

    private List<Double> getValuesFromGridPaneAsList() {
        List<Double> result = new ArrayList<>();
        for(int i=19; i>10; i--) {
            String value = ((TextField)valuesGridPane.getChildren().get(i)).getText();
            if(!value.equals(""))
                result.add(Double.parseDouble(((TextField)valuesGridPane.getChildren().get(i)).getText()));
        }
        return result;
    }

    private String getValuesFromGridPaneAsString() {
        String result = "";
        for(int i=19; i>10; i--) {
            String value = ((TextField)valuesGridPane.getChildren().get(i)).getText();
            if(!value.equals(""))
                result += ((TextField)valuesGridPane.getChildren().get(i)).getText() + " ";
        }
        return result;
    }

    private void updateGridValues(CustomTextField field) {
        int comparisonCount = obtainComparisonCount(field);
        updateGridGraphics(comparisonCount);
    }

    private int obtainComparisonCount(CustomTextField field) {
        if (field.hasNoChildren() || field.getChildrenCount()==0)
            return alternatives.getValue();
        else
            return field.getChildrenCount();
    }

    private void updateGridGraphics(int count) {
        setInitialValues();
        switch (count) {
            case 5:
                getTextFieldFromGridPane(3).setDisable(false);
                getTextFieldFromGridPane(6).setDisable(false);
                getTextFieldFromGridPane(8).setDisable(false);
                getTextFieldFromGridPane(9).setDisable(false);
            case 4:
                getTextFieldFromGridPane(2).setDisable(false);
                getTextFieldFromGridPane(5).setDisable(false);
                getTextFieldFromGridPane(7).setDisable(false);
            case 3:
                getTextFieldFromGridPane(1).setDisable(false);
                getTextFieldFromGridPane(4).setDisable(false);
        }
    }

    private int getNumberOfAlternativesWhenComparisonCountEquals(int comparisonCount) {
        switch (comparisonCount) {
            case 1:
                return 2;
            case 3:
                return 3;
            case 6:
                return 4;
            case 10:
                return 5;
            default:
                return 0;
        }
    }

    private void setInitialValues() {
        ((TextField)valuesGridPane.getChildren().get(19)).setText("");
        for(int i=0; i<valuesGridPane.getChildren().size()-1; i++)
            if(i>9) {
                TextField tf = (TextField)valuesGridPane.getChildren().get(i);
                tf.setText("");
                tf.setDisable(true);
            }
    }

    private TextField getTextFieldFromGridPane(int col) {
        return (TextField)valuesGridPane.getChildren().get(19-col);
    }

    @FXML
    public void generateXml() {
        new XmlMaker(root).generate();
    }
}
