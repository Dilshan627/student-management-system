package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
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


    public void initialize(){
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
    }

    private void loadData() throws SQLException, ClassNotFoundException {

        ResultSet result = CrudUtil.execute("SELECT * FROM student");
        ObservableList<StudentTm> obList = FXCollections.observableArrayList();

        while (result.next()){
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
                } else if (Student.getStudentName().toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                else
                    return false;

            });
        });

        SortedList<StudentTm> sortedData = new SortedList<>(filterData);

        sortedData.comparatorProperty().bind(tblStudent.comparatorProperty());

        tblStudent.setItems(sortedData);

    }


    public void saveOnAction(ActionEvent actionEvent) {
    }

    public void DeleteOnAction(ActionEvent actionEvent) {
    }
}
