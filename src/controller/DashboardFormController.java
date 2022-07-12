package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import util.CrudUtil;
import view.tm.StudentTm;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardFormController {
    public TableView<StudentTm> tblStudent;
    public Button btnSave;
    public JFXTextField txtStudentId;
    public JFXTextField txtName;
    public JFXTextField txtEmail;
    public JFXTextField txtContact;
    public JFXTextField txtAddress;
    public JFXTextField txtNic;
    public JFXTextField txtSearch;


    public void initialize() {
        try {
            loadData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        tblStudent.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("studentId"));
        tblStudent.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("studentName"));
        tblStudent.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("email"));
        tblStudent.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("contact"));
        tblStudent.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblStudent.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("nic"));


        tblStudent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnSave.setText(newValue != null ? "Update" : "Save");
            txtStudentId.setEditable(false);
            if (newValue != null) {
                txtStudentId.setText(newValue.getStudentId());
                txtName.setText(newValue.getStudentName());
                txtEmail.setText(newValue.getEmail());
                txtContact.setText(newValue.getContact());
                txtAddress.setText(newValue.getAddress());
                txtNic.setText(newValue.getNic());
            }
        });

    }

    private void loadData() throws SQLException, ClassNotFoundException {

        ResultSet result = CrudUtil.execute("SELECT * FROM student");
        ObservableList<StudentTm> obList = FXCollections.observableArrayList();

        while (result.next()) {
            obList.add(
                    new StudentTm(
                            result.getString("student_id"),
                            result.getString("student_name"),
                            result.getString("email"),
                            result.getString("contact"),
                            result.getString("address"),
                            result.getString("nic")
                    )
            );
        }
        tblStudent.setItems(obList);


        FilteredList<StudentTm> filterData = new FilteredList<StudentTm>(obList, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData.setPredicate(Student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();


                if (Student.getStudentId().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else return Student.getStudentName().toLowerCase().indexOf(lowerCaseFilter) != -1;

            });
        });

        SortedList<StudentTm> sortedData = new SortedList<>(filterData);

        sortedData.comparatorProperty().bind(tblStudent.comparatorProperty());

        tblStudent.setItems(sortedData);

    }


    public void saveOnAction(ActionEvent actionEvent) {
        String id = txtStudentId.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String address = txtAddress.getText();
        String nic = txtNic.getText();

        if (btnSave.getText().equalsIgnoreCase("Save")) {

            try {
                CrudUtil.execute("INSERT INTO student VALUES (?,?,?,?,?,?)", id, name, email, contact, address, nic);


            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, id + " already exists").show();
                throw new RuntimeException(e);
            }

        } else {
            try {
                CrudUtil.execute("UPDATE Student SET student_name=?,email=?,contact=?,address=?,nic=? where student_id=?", name, email, contact, address, nic, id);


            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            StudentTm selectedStudent = tblStudent.getSelectionModel().getSelectedItem();
            selectedStudent.setStudentName(name);
            selectedStudent.setEmail(email);
            selectedStudent.setContact(contact);
            selectedStudent.setAddress(address);
            selectedStudent.setNic(nic);

        }

        tblStudent.refresh();
        try {
            loadData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void DeleteOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        try {
            CrudUtil.execute("DELETE FROM student WHERE student_id=?",txtStudentId.getText());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

       loadData();
        clear();
    }

    public void newOnAction(ActionEvent actionEvent) {
      clear();
    }
    private void clear(){
        txtStudentId.setEditable(true);
        btnSave.setText("Save");
        btnSave.setDisable(false);
        txtStudentId.clear();
        txtName.clear();
        txtEmail.clear();
        txtContact.clear();
        txtAddress.clear();
        txtNic.clear();
    }


}
