package com.jerry.gui;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private static final String APP_TITLE = "IWATool";
	private static final String APP_ROOT_LAYOUT = "view/RootLayout.fxml";

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root;
		{
			root = FXMLLoader.load(getURL(APP_ROOT_LAYOUT));
			primaryStage.setTitle(APP_TITLE);
			primaryStage.setScene(new Scene(root, 1024, 768));
			primaryStage.show();
		}
		{
			Node node = FXMLLoader.load(getURL("view/VerticalMenuBox.fxml"));
			root.setLeft(node);
		}

	}

	private URL getURL(String resourceName) {
		// TODO Auto-generated method stub
		return getClass().getResource(resourceName);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
