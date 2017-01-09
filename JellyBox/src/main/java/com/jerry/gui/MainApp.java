package com.jerry.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.jerry.gui.model.WASServerLogMappingData;
import com.jerry.gui.view.Controller;
import com.jerry.util.function.TriFunction;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private static final String APP_TITLE = "IWATool";
	private static final String APP_ROOT_LAYOUT = "view/RootLayout.fxml";
	private static final String VIEW_VERTICAL_MENU_BOX_FXML = "view/VerticalMenuBox.fxml";
	private static final String VIEW_CONFIG_FXML = "view/Config.fxml" ;

	private static final int SCENE_WIDTH = 1024;
	private static final int SCENE_HEIGHT = 768;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Function<String,Node> getNodeFunction = (String resurceName) -> {
			try {
				return FXMLLoader.load( getClass().getResource( resurceName ) );
			} catch (IOException e) {
				throw new RuntimeException("load fail : "+resurceName ,e );
			}
		};

		BorderPane root;
		{
			root = (BorderPane) getNodeFunction.apply( APP_ROOT_LAYOUT );
		}
		// set primaryStage
		{
			primaryStage.setTitle(APP_TITLE);
			primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT ));
			primaryStage.show();
		}
		// set root
		{
			root.setLeft(getNodeFunction.apply(VIEW_VERTICAL_MENU_BOX_FXML));

		}
		// set config
		{
			FXMLLoader loader = new FXMLLoader(  );
			loader.setLocation(  getURL( VIEW_CONFIG_FXML ));

			root.setCenter( loader.load() );

			Controller controller=loader.getController();

			ObservableList<WASServerLogMappingData> items;{
				items=FXCollections.observableArrayList();
				List<String> dummyServerNames = Arrays.asList(new String[]{"MES2.App1","MES2.App2","BIGDATA.App1"} );
				String dummyLogDirectory = "/log/bpm/SEC/";
				String regex = "_SystemOut(.?*).log";
				TriFunction<String,String,String,WASServerLogMappingData> supplier = WASServerLogMappingData::new;
				dummyServerNames.stream().map((serverName)->supplier.apply( serverName,dummyLogDirectory+serverName,regex)).forEach( items::add );
			}
			controller.setItems( items );
		}
	}
	private URL getURL(String resourceName){
		return getClass().getResource( resourceName );
	}

	public static void main(String[] args) {
		launch(args);
	}
}
