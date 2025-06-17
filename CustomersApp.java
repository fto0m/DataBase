package application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CustomersApp extends Application {

	private TableView<Customer> table;
	private ObservableList<Customer> customerList;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Customers - Yes Baby");

		Label headerLabel = new Label("Customers Management");
		headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		Button backButton = new Button("← Back to Home");
		backButton.setOnAction(e -> {
			primaryStage.close();
		});

		HBox header = new HBox(10, backButton, headerLabel);
		header.setPadding(new Insets(10));
		header.setSpacing(20);
		header.setStyle("-fx-background-color: #f0f0f0;");

		// Table setup
		table = new TableView<>();
		customerList = FXCollections.observableArrayList(DBConnect.getAllCustomers());
		table.setItems(customerList);

		TableColumn<Customer, Number> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());

		TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

		TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
		phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

		TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
		addressCol.setCellValueFactory(cellData -> {
			Customer c = cellData.getValue();
			return new SimpleStringProperty(c.getStreetNumber() + " " + c.getStreetName() + ", " + c.getCity());
		});

		TableColumn<Customer, Number> pointsCol = new TableColumn<>("Points");
		pointsCol.setCellValueFactory(cellData -> cellData.getValue().pointsProperty());

		TableColumn<Customer, Void> actionCol = new TableColumn<>("Actions");
		actionCol.setCellFactory(col -> new TableCell<>() {
			private final Button editButton = new Button("Edit");
			private final Button deleteButton = new Button("Delete");
			private final HBox pane = new HBox(10, editButton, deleteButton);

			{
				editButton.setOnAction(e -> {
					Customer customer = getTableView().getItems().get(getIndex());
					openEditCustomerDialog(customer, primaryStage);
				});

				deleteButton.setOnAction(e -> {
					Customer customer = getTableView().getItems().get(getIndex());
					if (DBConnect.deleteCustomer(customer.getId())) {
						customerList.remove(customer);
						showAlert("Success", "Customer deleted successfully.");
					} else {
						showAlert("Error", "Failed to delete customer.");
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(pane);
				}
			}
		});

		table.getColumns().addAll(idCol, nameCol, phoneCol, addressCol, pointsCol, actionCol);

		Button addCustomerBtn = new Button("Add Customer");
		addCustomerBtn.setOnAction(e -> {
			primaryStage.close();
			new AddCustomerApp().start(new Stage());
		});

		VBox mainLayout = new VBox(10, header, addCustomerBtn, table);
		mainLayout.setPadding(new Insets(20));

		Scene scene = new Scene(mainLayout, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void openEditCustomerDialog(Customer customer, Stage owner) {
		Stage dialog = new Stage();
		dialog.initOwner(owner);
		dialog.setTitle("Edit Customer");

		// Create form fields with current customer data
		TextField nameField = new TextField(customer.getName());
		TextField phoneField = new TextField(customer.getPhone());
		TextField cityField = new TextField(customer.getCity());
		TextField streetField = new TextField(customer.getStreet());
		TextField streetNumField = new TextField(customer.getStreetNumber());
		TextField streetNameField = new TextField(customer.getStreetName());
		TextField pointsField = new TextField(String.valueOf(customer.getPoints()));

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

		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> {
			customer.setName(nameField.getText());
			customer.setPhone(phoneField.getText());
			customer.setCity(cityField.getText());
			customer.setStreet(streetField.getText());
			customer.setStreetNumber(streetNumField.getText());
			customer.setStreetName(streetNameField.getText());
			customer.setPoints(Integer.parseInt(pointsField.getText()));

			if (DBConnect.updateCustomer(customer)) {
				table.refresh();
				showAlert("Success", "Customer updated successfully.");
				dialog.close();
			} else {
				showAlert("Error", "Failed to update customer.");
			}
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> dialog.close());

		HBox buttons = new HBox(10, saveButton, cancelButton);
		buttons.setPadding(new Insets(10));
		buttons.setStyle("-fx-alignment: center;");

		VBox dialogLayout = new VBox(15, formGrid, buttons);
		dialogLayout.setPadding(new Insets(20));

		Scene dialogScene = new Scene(dialogLayout, 450, 400);
		dialog.setScene(dialogScene);
		dialog.show();
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}