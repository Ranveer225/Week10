package viewmodel;

import dao.DbConnectivityClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import model.Person;
import service.MyLogger;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class DB_GUI_Controller implements Initializable {

    @FXML
    TextField first_name, last_name, department, major, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    @FXML
    private Button editBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button addBtn;
    @FXML
    private ComboBox<Major> majorDropdown;

    @FXML
    private Label statusLabel;

    @FXML
    private Button clearAllBtn;


    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);
        setupMajorDropdown();
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));

            tv.setEditable(true);


            tv_fn.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_ln.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_department.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_major.setCellFactory(TextFieldTableCell.forTableColumn());
            tv_email.setCellFactory(TextFieldTableCell.forTableColumn());


            tv_fn.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                person.setFirstName(event.getNewValue());
            });

            tv_ln.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                person.setLastName(event.getNewValue());
            });

            tv_department.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                person.setDepartment(event.getNewValue());
            });

            tv_major.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                person.setMajor(event.getNewValue());
            });

            tv_email.setOnEditCommit(event -> {
                Person person = event.getRowValue();
                person.setEmail(event.getNewValue());
            });

            tv.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && tv.getSelectionModel().getSelectedItem() == null) {
                    Person newPerson = new Person("", "", "", "", "", "");
                    data.add(newPerson);
                }
            });

            tv.setItems(data);

            tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                editBtn.setDisable(newValue == null);
                deleteBtn.setDisable(newValue == null);
            });

            setupFieldValidation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void setupMajorDropdown() {
        majorDropdown.setItems(FXCollections.observableArrayList(Major.values()));
        majorDropdown.setValue(Major.Sales);
    }
    private void setupFieldValidation() {

        String nameRegex = "^[A-Za-z\\s]+$";
        String departmentRegex = "^[A-Za-z\\s]+$";
        String emailRegex = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";

        addRegexValidation(first_name, nameRegex);
        addRegexValidation(last_name, nameRegex);
        addRegexValidation(department, departmentRegex);
        addRegexValidation(email, emailRegex);

        first_name.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        last_name.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        department.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        email.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        majorDropdown.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    private void addRegexValidation(TextField textField, String regex) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            return newText.matches(regex) || newText.isEmpty() ? change : null;
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    private void validateForm() {
        boolean isValid = !first_name.getText().trim().isEmpty() &&
                !last_name.getText().trim().isEmpty() &&
                !department.getText().trim().isEmpty() &&
                !major.getText().trim().isEmpty() &&
                email.getText().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$"); // Email validation
        addBtn.setDisable(!isValid); // Enable "Add" button if all fields are valid
    }
    @FXML
    protected void addNewRecord() {

            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(), majorDropdown.getValue().name(),
                    major.getText(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
         updateStatus("Record added successfully!");

    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        major.setText("");
        email.setText("");
        imageURL.setText("");
        majorDropdown.setValue(Major.Finance);
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                major.getText(), email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
        updateStatus("Record updated successfully.");
    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }
    @FXML
    protected void importCsvFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File to Import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                List<Person> importedData = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(","); // Assumes CSV is comma-separated
                    if (fields.length >= 6) { // Adjust based on your Person model
                        Person person = new Person(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]);
                        importedData.add(person);
                    }
                }
                data.setAll(importedData); // Update the TableView's data
                updateStatus("CSV imported successfully.");
            } catch (IOException e) {
                updateStatus("Failed to import CSV: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void exportCsvFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Person person : data) {
                    String csvLine = String.join(",",
                            person.getFirstName(),
                            person.getLastName(),
                            person.getDepartment(),
                            person.getMajor(),
                            person.getEmail(),
                            person.getImageURL());
                    writer.write(csvLine);
                    writer.newLine();
                }
                updateStatus("CSV exported successfully.");
            } catch (IOException e) {
                updateStatus("Failed to export CSV: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        major.setText(p.getMajor());
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    @FXML
    protected void clearAllFields() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        email.setText("");
        imageURL.setText("");

        majorDropdown.setValue(null);

        statusLabel.setText("All fields have been cleared!");
    }

    private static enum Major {Finance, Sales, Analyst}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}

// Documentation: The changes I implemented into this app were rebranding and styling the theme along with adding in new fields for this.
// It started off as a student registration form and I replaced the majors with roles as part of a business client system. I added in a clear all button
// as well that removes all the data in the form and resets dropdowns. I added in a new button in our interface fxml file for this and connected it with
// the controller. Then I added in a method that clears all fields to test this functioning. Next, I added in row editing for table view and the
// process of adding rows directly in the table. I had to make each column editable by using TextFieldTableCell and updated the data when editing finishes.
// Lastly, I updated the person class so it supports editing by adding constructors and methods properly and added functionality to add a new row.

