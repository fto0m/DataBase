package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ItemsManagementApp extends Application {

    private ObservableList<Item> items;
    private TableView<Item> table;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Items Management - Yes Baby");

        // Initialize data
        items = FXCollections.observableArrayList(DBConnect.getAllItems());

        // Create the main layout
        VBox mainLayout = createMainLayout(primaryStage);

        Scene scene = new Scene(mainLayout, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMainLayout(Stage primaryStage) {
        // Header
        Label titleLabel = new Label("Items Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button backButton = new Button("← Back to Home");
        backButton.setOnAction(e -> primaryStage.close());

        HBox header = new HBox(20, titleLabel, backButton);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f0f0f0;");
        header.setSpacing(20);

        // Table
        table = new TableView<>(items);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Item, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Item, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Item, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        TableColumn<Item, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Item, String> publisherCol = new TableColumn<>("Publisher");
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisherPhone"));

        TableColumn<Item, String> storeCol = new TableColumn<>("Store");
        storeCol.setCellValueFactory(new PropertyValueFactory<>("storeName"));

        TableColumn<Item, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    openEditItemWindow(item);
                });

                deleteBtn.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    if (DBConnect.deleteItem(item.getId())) {
                        items.remove(item);
                        showAlert("Success", "Item deleted successfully.");
                    } else {
                        showAlert("Error", "Failed to delete item.");
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

        table.getColumns().addAll(idCol, nameCol, priceCol, typeCol, publisherCol, storeCol, actionCol);

        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("Search items...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterItems(newVal);
        });

        // Add button
        Button addButton = new Button("Add New Item");
        addButton.setOnAction(e -> openAddItemWindow());

        // Layout
        VBox mainLayout = new VBox(15, header, searchField, table, addButton);
        mainLayout.setPadding(new Insets(20));
        return mainLayout;
    }

    private void filterItems(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            table.setItems(items);
        } else {
            ObservableList<Item> filteredItems = FXCollections.observableArrayList();
            for (Item item : items) {
                if (item.getName().toLowerCase().contains(searchText.toLowerCase())){
                    filteredItems.add(item);
                }
            }
            table.setItems(filteredItems);
        }
    }

    private void openAddItemWindow() {
        Stage addStage = new Stage();
        addStage.setTitle("Add New Item");
        addStage.initModality(Modality.APPLICATION_MODAL);

        // Form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        TextField typeField = new TextField();
        typeField.setPromptText("Item Type");

        TextField publisherIdField = new TextField();
        publisherIdField.setPromptText("Publisher ID");

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                Item newItem = new Item(
                        0, // ID will be generated by database
                        nameField.getText(),
                        Double.parseDouble(priceField.getText()),
                        typeField.getText(),
                        Integer.parseInt(publisherIdField.getText()),
                        "", "" // These will be filled by the database
                );

                if (DBConnect.addItem(newItem)) {
                    items.add(newItem);
                    showAlert("Success", "Item added successfully!");
                    addStage.close();
                } else {
                    showAlert("Error", "Failed to add item.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid numbers for price and publisher ID.");
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> addStage.close());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(15));

        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Price:"), 0, 1);
        form.add(priceField, 1, 1);
        form.add(new Label("Type:"), 0, 2);
        form.add(typeField, 1, 2);
        form.add(new Label("Publisher ID:"), 0, 3);
        form.add(publisherIdField, 1, 3);
        form.add(buttons, 1, 4);

        Scene scene = new Scene(form, 400, 250);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        addStage.setScene(scene);
        addStage.showAndWait();
    }

    private void openEditItemWindow(Item item) {
        Stage editStage = new Stage();
        editStage.setTitle("Edit Item");
        editStage.initModality(Modality.APPLICATION_MODAL);

        // Form fields with current values
        TextField nameField = new TextField(item.getName());
        TextField priceField = new TextField(String.valueOf(item.getPrice()));
        TextField typeField = new TextField(item.getType());
        TextField publisherIdField = new TextField(String.valueOf(item.getPublisherId()));

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                item.setName(nameField.getText());
                item.setPrice(Double.parseDouble(priceField.getText()));
                item.setType(typeField.getText());
                item.setPublisherId(Integer.parseInt(publisherIdField.getText()));

                if (DBConnect.updateItem(item)) {
                    table.refresh();
                    showAlert("Success", "Item updated successfully!");
                    editStage.close();
                } else {
                    showAlert("Error", "Failed to update item.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid numbers for price and publisher ID.");
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> editStage.close());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(15));

        form.add(new Label("Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Price:"), 0, 1);
        form.add(priceField, 1, 1);
        form.add(new Label("Type:"), 0, 2);
        form.add(typeField, 1, 2);
        form.add(new Label("Publisher ID:"), 0, 3);
        form.add(publisherIdField, 1, 3);
        form.add(buttons, 1, 4);

        Scene scene = new Scene(form, 400, 250);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        editStage.setScene(scene);
        editStage.showAndWait();
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
