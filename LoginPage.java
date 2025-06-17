package application;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginPage extends Application {

	@Override
	public void start(Stage stage) {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setVgap(10);
		grid.setHgap(10);
		grid.setPadding(new Insets(25));

		grid.add(new Label("Username:"), 0, 1);
		TextField user = new TextField();
		grid.add(user, 1, 1);

		grid.add(new Label("Password:"), 0, 2);
		PasswordField pass = new PasswordField();
		grid.add(pass, 1, 2);

		Button login = new Button("Log in");
		Text result = new Text();
		login.setOnAction(e -> {
			if ("admin".equals(user.getText()) && "pass123".equals(pass.getText()))
				result.setText("✅ Login Successful!");
			else
				result.setText("❌ Invalid Credentials.");
		});

		grid.add(login, 1, 3);
		grid.add(result, 1, 5);

		Scene scene = new Scene(grid, 300, 250);
		stage.setTitle("Login - Yes Baby");
		stage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.show();
	}
}
