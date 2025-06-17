package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ItemTypesManagementApp extends Application {

	public static class ItemType {
		private final String typeId;
		private final String typeName;
		private final String description;

		public ItemType(String typeId, String typeName, String description) {
			this.typeId = typeId;
			this.typeName = typeName;
			this.description = description;
		}

		public String getTypeId() {
			return typeId;
		}

		public String getTypeName() {
			return typeName;
		}

		public String getDescription() {
			return description;
		}
	}

	private final ObservableList<ItemType> itemTypes = FXCollections
			.observableArrayList(new ItemType("T001", "Educational", "Educational toys for kids")
			// Add more items if needed
			);

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Item Types - Yes Baby");

		// Table setup
		TableView<ItemType> table = new TableView<>();
		table.setItems(itemTypes);

		TableColumn<ItemType, String> colTypeId = new TableColumn<>("Type ID");
		colTypeId.setCellValueFactory(new PropertyValueFactory<>("typeId"));

		TableColumn<ItemType, String> colTypeName = new TableColumn<>("Type Name");
		colTypeName.setCellValueFactory(new PropertyValueFactory<>("typeName"));

		TableColumn<ItemType, String> colDescription = new TableColumn<>("Description");
		colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

		TableColumn<ItemType, Void> colActions = new TableColumn<>("Actions");
		colActions.setCellFactory(col -> new TableCell<>() {
			private final Button editBtn = new Button("Edit");
			private final Button deleteBtn = new Button("Delete");
			private final HBox pane = new HBox(10, editBtn, deleteBtn);

			{
				pane.setAlignment(Pos.CENTER);

				editBtn.setOnAction(e -> {
					ItemType item = getTableView().getItems().get(getIndex());
					System.out.println("Edit clicked for " + item.getTypeName());
					// Add your edit logic here
				});

				deleteBtn.setOnAction(e -> {
					ItemType item = getTableView().getItems().get(getIndex());
					System.out.println("Delete clicked for " + item.getTypeName());
					itemTypes.remove(item);
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

		table.getColumns().addAll(colTypeId, colTypeName, colDescription, colActions);

		// Header and layout
		Label headerLabel = new Label("Item Types Management");
		headerLabel.setStyle("-fx-font-size: 24px; -fx-padding: 10px;");

		BorderPane root = new BorderPane();
		root.setTop(headerLabel);
		BorderPane.setAlignment(headerLabel, Pos.CENTER);
		root.setCenter(table);

		Scene scene = new Scene(root, 700, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
