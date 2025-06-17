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

import java.sql.Date;
import java.time.LocalDate;

public class YesBabyOrdersApp extends Application {

    private ObservableList<Order> orders;
    private TableView<Order> table;
    private ObservableList<String> customers;
    private ObservableList<String> employees;
    private ObservableList<String> stores;
    private ObservableList<String> items;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Orders Management - Yes Baby");

        // Initialize data
        orders = FXCollections.observableArrayList(DBConnect.getAllOrders());
        customers = FXCollections.observableArrayList(DBConnect.getAllCustomerNames());
        employees = FXCollections.observableArrayList(DBConnect.getAllEmployeeNames());
        stores = FXCollections.observableArrayList(DBConnect.getAllStoreNames());
        items = FXCollections.observableArrayList(DBConnect.getAllItemNames());

        // Create the main layout
        VBox mainLayout = createMainLayout(primaryStage);

        Scene scene = new Scene(mainLayout, 1000, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createMainLayout(Stage primaryStage) {
        // Header
        Label titleLabel = new Label("Orders Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button backButton = new Button("← Back to Home");
        backButton.setOnAction(e -> primaryStage.close());

        HBox header = new HBox(20, titleLabel, backButton);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f0f0f0;");
        header.setSpacing(20);

        // Table
        table = new TableView<>(orders);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order, Integer> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Order, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Order, String> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<Order, Double> priceCol = new TableColumn<>("Price");
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

        TableColumn<Order, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Order, String> storeCol = new TableColumn<>("Store");
        storeCol.setCellValueFactory(new PropertyValueFactory<>("storeName"));

        TableColumn<Order, String> employeeCol = new TableColumn<>("Employee");
        employeeCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<Order, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(deleteBtn);

            {
                pane.setAlignment(Pos.CENTER);

                deleteBtn.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    if (DBConnect.deleteOrder(order.getId())) {
                        orders.remove(order);
                        showAlert("Success", "Order deleted successfully.");
                    } else {
                        showAlert("Error", "Failed to delete order.");
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

        table.getColumns().addAll(idCol, dateCol, customerCol, itemCol, priceCol, qtyCol, storeCol, employeeCol, actionCol);

        // Add button
        Button addButton = new Button("Add New Order");
        addButton.setOnAction(e -> openAddOrderWindow());

        // Layout
        VBox mainLayout = new VBox(15, header, table, addButton);
        mainLayout.setPadding(new Insets(20));
        return mainLayout;
    }

    private void openAddOrderWindow() {
        Stage addStage = new Stage();
        addStage.setTitle("Add New Order");
        addStage.initModality(Modality.APPLICATION_MODAL);

        // Form fields
        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<String> customerCombo = new ComboBox<>(customers);
        customerCombo.setPromptText("Select Customer");

        ComboBox<String> employeeCombo = new ComboBox<>(employees);
        employeeCombo.setPromptText("Select Employee");

        ComboBox<String> storeCombo = new ComboBox<>(stores);
        storeCombo.setPromptText("Select Store");

        ComboBox<String> itemCombo = new ComboBox<>(items);
        itemCombo.setPromptText("Select Item");

        TextField quantityField = new TextField("1");
        quantityField.setPromptText("Quantity");

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                // Get IDs from the selected names
                int customerId = DBConnect.getCustomerIdByName(customerCombo.getValue());
                int employeeId = DBConnect.getEmployeeIdByName(employeeCombo.getValue());
                int storeId = DBConnect.getStoreIdByName(storeCombo.getValue());
                int itemId = DBConnect.getItemIdByName(itemCombo.getValue());

                double price = DBConnect.getItemPriceById(itemId);
                int quantity = Integer.parseInt(quantityField.getText());

                Order newOrder = new Order(
                        0, // ID will be generated by database
                        Date.valueOf(datePicker.getValue()),
                        price * quantity,
                        quantity,
                        storeCombo.getValue(),
                        employeeCombo.getValue(),
                        customerCombo.getValue(),
                        itemCombo.getValue(),
                        storeId,
                        employeeId,
                        customerId,
                        itemId
                );

                if (DBConnect.addOrder(newOrder)) {
                    orders.add(newOrder);
                    showAlert("Success", "Order added successfully!");
                    addStage.close();
                } else {
                    showAlert("Error", "Failed to add order.");
                }
            } catch (Exception ex) {
                showAlert("Error", "Please fill all fields correctly. Quantity must be a number.");
                ex.printStackTrace();
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

        form.add(new Label("Date:"), 0, 0);
        form.add(datePicker, 1, 0);
        form.add(new Label("Customer:"), 0, 1);
        form.add(customerCombo, 1, 1);
        form.add(new Label("Employee:"), 0, 2);
        form.add(employeeCombo, 1, 2);
        form.add(new Label("Store:"), 0, 3);
        form.add(storeCombo, 1, 3);
        form.add(new Label("Item:"), 0, 4);
        form.add(itemCombo, 1, 4);
        form.add(new Label("Quantity:"), 0, 5);
        form.add(quantityField, 1, 5);
        form.add(buttons, 1, 6);

        Scene scene = new Scene(form, 500, 350);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        addStage.setScene(scene);
        addStage.showAndWait();
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