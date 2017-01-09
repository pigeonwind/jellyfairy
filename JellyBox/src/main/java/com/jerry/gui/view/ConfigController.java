package com.jerry.gui.view;

import com.jerry.gui.model.WASServerLogMappingData;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Created by jerryDev on 2017. 1. 7..
 */
public class ConfigController implements Controller{
    @FXML
    private Button ihsPluginFileChooseBtn,mappingBtn;
    @FXML
    private TextField ihsPluginFileChooseField;
    @FXML
    private TableColumn<WASServerLogMappingData,String> nameFilterColumn;
    @FXML
    private TableColumn<WASServerLogMappingData,String> logDirectoryColumn;
    @FXML
    private TableColumn<WASServerLogMappingData,String> serverNameColumn;
    @FXML
    private TableView<WASServerLogMappingData> wasLogMappingTable;

    @FXML
    private void initialize(){
        /**
         * set table
         */
        {
            serverNameColumn.setCellValueFactory( callData -> callData.getValue().serverNameProperty() );
            logDirectoryColumn.setCellValueFactory( callData -> callData.getValue().logDirectoryProperty() );
            nameFilterColumn.setCellValueFactory( callData -> callData.getValue().nameFilterPatternProperty() );
        }
        /**
         * set hander on ihsPluginFileChooseBtn
         */
        {
            ihsPluginFileChooseBtn.setOnAction( event -> {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog( null );
                if (selectedFile != null) {
                    ihsPluginFileChooseField.setText( selectedFile.getAbsolutePath() );
                }
            } );
        }
        /**
         * set handler on mappingBtn
         */
        {
            mappingBtn.setOnAction( event -> {} );
        }
    }

    @Override
    public void setItems(Object items) {
        this.wasLogMappingTable.setItems( (ObservableList<WASServerLogMappingData>) items );

    }

    private void onIhsPluginFileChooseBtn(){

    }
}
