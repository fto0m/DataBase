package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AddCustomerApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Add Customer - Yes Baby");

		Label headerLabel = new Label("Add New Customer");
		headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		Button backButton = new Button("← Back to Customers");
		backButton.setOnAction(e -> {
			primaryStage.close();
			new CustomersApp().start(new Stage());
		});

		HBox header = new HBox(10, backButton, headerLabel);
		header.setPadding(new Insets(10));
		header.setStyle("-fx-background-color: #f0f0f0;");
		header.setSpacing(20);

		// Form fields
		TextField nameField = new TextField();
		nameField.setPromptText("Full Name");

		TextField phoneField = new TextField();
		phoneField.setPromptText("Phone Number");

		TextField cityField = new TextField();
		cityField.setPromptText("City");

		TextField streetField = new TextField();
		streetField.setPromptText("Street");

		TextField streetNumField = new TextField();
		streetNumField.setPromptText("Street Number");

		TextField streetNameField = new TextField();
		streetNameField.setPromptText("Street Name");

		TextField pointsField = new TextField();
		pointsField.setPromptText("Points");
		pointsField.setText("0");

		GridPane formGrid = new GridPane();
		formGrid.setVgap(10);
		formGrid.setHgap(10);
		formGrid.setPadding(new Insets(10));

		formGrid.add(new Label("Full Name:"), 0, 0);
		formGrid.add(nameField, 1, 0);
		formGrid.add(new Label("Phone Number:"), 0, 1);
		formGrid.add(phoneField, 1, 1);
		formGrid.add(new Label("City:"), 0, 2);
		formGrid.add(cityField, 1, 2);
		formGrid.add(new Label("Street:"), 0, 3);
		formGrid.add(streetField, 1, 3);
		formGrid.add(new Label("Street Number:"), 0, 4);
		formGrid.add(streetNumField, 1, 4);
		formGrid.add(new Label("Street Name:"), 0, 5);
		formGrid.add(streetNameField, 1, 5);
		formGrid.add(new Label("Points:"), 0, 6);
		formGrid.add(pointsField, 1, 6);

		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> {
			primaryStage.close();
			new CustomersApp().start(new Stage());
		});

		Button saveButton = new Button("Save Customer");
		saveButton.setOnAction(e -> {
			if (validateFields(nameField, phoneField, cityField, streetField, streetNumField, streetNameField,
					pointsField)) {

				Customer customer = new Customer(nameField.getText(), phoneField.getText(), cityField.getText(),
						streetField.getText(), streetNumField.getText(), streetNameField.getText(),
						Integer.parseInt(pointsField.getText()));

				if (DBConnect.addCustomer(customer)) {
					showAlert("Success", "Customer added successfully!");
					primaryStage.close();
					new CustomersApp().start(new Stage());
				} else {
					showAlert("Error", "Failed to add customer. Please try again.");
				}
			}
		});

		HBox buttons = new HBox(10, cancelButton, saveButton);
		buttons.setPadding(new Insets(10));
		buttons.setStyle("-fx-alignment: center;");

		VBox mainLayout = new VBox(15, header, formGrid, buttons);
		mainLayout.setPadding(new Insets(20));

		Scene scene = new Scene(mainLayout, 500, 500);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private boolean validateFields(TextField... fields) {
		for (TextField field : fields) {
			if (field.getText().isEmpty()) {
				showAlert("Validation Error", "All fields are required!");
				field.requestFocus();
				return false;
			}
		}

		try {
			Integer.parseInt(fields[6].getText()); // Points field
		} catch (NumberFormatException e) {
			showAlert("Validation Error", "Points must be a number!");
			fields[6].requestFocus();
			return false;
		}

		return true;
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}