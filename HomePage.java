package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class HomePage extends Application {

	private Stage primaryStage;
	private ComboBox<String> roleCombo;
	private TextField emailField;
	private PasswordField passwordField;
	private Button proceedBtn;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		showWelcomePage();
	}

	private void showWelcomePage() {
		Label welcome = new Label("Welcome to Yes Baby 🎈");
		welcome.setFont(Font.font("Arial", 30));
		welcome.setTextFill(Color.DARKMAGENTA);

		Button loginBtn = new Button("Login");
		Button signupBtn = new Button("Sign Up");
		loginBtn.setPrefWidth(100);
		signupBtn.setPrefWidth(100);

		loginBtn.setOnAction(e -> showAuthPage("Login"));
		signupBtn.setOnAction(e -> showAuthPage("Sign Up"));

		Label contactTitle = new Label("Contact Us");
		contactTitle.setFont(Font.font("Arial", 20));

		Hyperlink instagram = new Hyperlink("Instagram");
		instagram.setGraphic(new ImageView(
				new Image("https://cdn-icons-png.flaticon.com/512/1384/1384063.png", 20, 20, true, true)));
		instagram.setOnAction(e -> getHostServices().showDocument("https://www.instagram.com/yesbaby376"));

		Hyperlink facebook = new Hyperlink("Facebook");
		facebook.setGraphic(
				new ImageView(new Image("https://cdn-icons-png.flaticon.com/512/733/733547.png", 20, 20, true, true)));
		facebook.setOnAction(e -> getHostServices().showDocument("https://www.facebook.com/yes22baby"));

		VBox contactBox = new VBox(10, contactTitle, instagram, facebook);
		contactBox.setAlignment(Pos.CENTER);
		contactBox.setPadding(new Insets(20));

		Label footer = new Label("© 2025 Developed by Fatima Hani & Mohammad Muhaisen");
		footer.setFont(Font.font(12));
		footer.setTextFill(Color.GRAY);
		HBox footerBox = new HBox(footer);
		footerBox.setAlignment(Pos.CENTER);
		footerBox.setPadding(new Insets(10));

		VBox box = new VBox(20, welcome, loginBtn, signupBtn, contactBox, footerBox);
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(50));

		Scene scene = new Scene(box, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Yes Baby 🎈 - Welcome");
		primaryStage.show();
	}

	private void showAuthPage(String type) {
		Label title = new Label(type);
		title.setFont(Font.font("Arial", 24));

		roleCombo = new ComboBox<>();
		roleCombo.getItems().addAll("Employee", "Customer");
		roleCombo.setPromptText("Select Role");

		emailField = new TextField();
		emailField.setPromptText("Email");

		passwordField = new PasswordField();
		passwordField.setPromptText("Password");

		proceedBtn = new Button(type);
		proceedBtn.setOnAction(e -> {
			String role = roleCombo.getValue();
			String emailText = emailField.getText();
			String passwordText = passwordField.getText();

			if (role == null || emailText.isEmpty() || passwordText.isEmpty()) {
				showAlert("Please fill all fields");
				return;
			}

			try {
				Label welcomeLabel = new Label();
				welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

				if (type.equals("Login")) {
					User user = DBConnect.authenticate(emailText, passwordText, role.toLowerCase());
					if (user != null) {
						welcomeLabel.setText("Welcome, " + user.getUsername());

						if (role.equals("Employee")) {
							VBox content = new VBox(10);
							content.getChildren().addAll(welcomeLabel, createEmployeeContent());
							content.setPadding(new Insets(20));
							primaryStage.setScene(new Scene(content, 800, 600));
						} else {
							VBox content = new VBox(10);
							content.getChildren().addAll(welcomeLabel, createCustomerContent(user.getReferenceId()));
							content.setPadding(new Insets(20));
							primaryStage.setScene(new Scene(content, 800, 600));
						}

					} else {
						showAlert("Invalid credentials");
					}
				} else if (type.equals("Sign Up")) {
					boolean success = DBConnect.register(emailText, passwordText, role.toLowerCase());
					if (success) {
						showAlert("Account created successfully. Please login.");
						showAuthPage("Login");
					} else {
						showAlert("Failed to create account. Email might already be used.");
					}
				}
			} catch (SQLException ex) {
				showAlert("Database error: " + ex.getMessage());
			}
		});

		Button backBtn = new Button("Back");
		backBtn.setOnAction(e -> showWelcomePage());

		VBox form = new VBox(15, title, roleCombo, emailField, passwordField, proceedBtn, backBtn);
		form.setAlignment(Pos.CENTER);
		form.setPadding(new Insets(40));

		Scene scene = new Scene(form, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
	}

	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private VBox createEmployeeContent() {
		Label title = new Label("Yes Baby 🎈");
		title.getStyleClass().add("main-title");
		title.setFont(Font.font("Arial", 30));
		title.setTextFill(Color.DARKMAGENTA);
		HBox header = new HBox(title);
		header.setAlignment(Pos.CENTER);
		header.setPadding(new Insets(20));
		Label heroTitle = new Label("Welcome to Yes Baby!");
		heroTitle.getStyleClass().add("hero-title");
		heroTitle.setFont(Font.font("Arial", 24));
		Label heroDesc = new Label("Discover the best toys for your little ones 🚗🧸🎠");

		heroDesc.getStyleClass().add("hero-desc");

		Button shopNow = new Button("Shop Now");

		shopNow.getStyleClass().add("shop-button");
		VBox hero = new VBox(10, heroTitle, heroDesc, shopNow);
		hero.setAlignment(Pos.CENTER);
		hero.setPadding(new Insets(20));

		VBox iconsBox = new VBox(10);
		iconsBox.setAlignment(Pos.CENTER);
		iconsBox.setPadding(new Insets(10));

		String[][] icons = { { "🛒", "Items" }, { "👩‍💼", "Employees" }, { "👨‍👩‍👧‍👦", "Customers" },
				{ "🏢", "Publishers" }, { "🧩", "Items Type" }, { "📝", "Orders" }, { "⚙️", "Statical" } // <--- ADD
																											// THIS LINE
		};

		FlowPane iconPane = new FlowPane(20, 20);
		iconPane.setAlignment(Pos.CENTER);

		for (String[] icon : icons) {
			Label emoji = new Label(icon[0]);
			emoji.setFont(Font.font(30));
			Label text = new Label(icon[1]);

			text.getStyleClass().add("icon-label");

			VBox box = new VBox(emoji, text);
			box.setAlignment(Pos.CENTER);
			box.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 15px; -fx-cursor: hand;");

			switch (icon[1]) {
			case "Items" -> box.setOnMouseClicked(e -> launchApp(new ItemsManagementApp()));
			case "Employees" -> box.setOnMouseClicked(e -> launchApp(new EmployeeApp()));
			case "Customers" -> box.setOnMouseClicked(e -> launchApp(new CustomersApp()));
			case "Publishers" -> box.setOnMouseClicked(e -> launchApp(new PublisherManagementApp()));
			case "Items Type" -> box.setOnMouseClicked(e -> launchApp(new ItemTypesManagementApp()));
			case "Orders" -> box.setOnMouseClicked(e -> launchApp(new YesBabyOrdersApp()));
			case "Statical" -> box.setOnMouseClicked(e -> launchApp(new Stats()));
			}

			iconPane.getChildren().add(box);
		}

		iconsBox.getChildren().add(iconPane);

		Button backBtn = new Button("Log Out");
		backBtn.setOnAction(e -> showAuthPage("Login"));

		backBtn.getStyleClass().add("back-button");

		VBox root = new VBox(20, header, hero, iconsBox, backBtn);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));
		root.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		return root;
	}

	private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

	private VBox createCustomerContent(int customerId) {
		Label welcomeLabel = new Label("Welcome to Yes Baby Store!");
		welcomeLabel.setFont(Font.font("Arial", 24));
		welcomeLabel.setTextFill(Color.DARKBLUE);

		Label pointsLabel = new Label("Your current points: 150");
		pointsLabel.setFont(Font.font("Arial", 16));
		pointsLabel.setTextFill(Color.GREEN);

		TableView<Item> productsTable = new TableView<>();
		productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		ObservableList<Item> items = FXCollections.observableArrayList(DBConnect.getAllItems());
		productsTable.setItems(items);

		TableColumn<Item, String> nameCol = new TableColumn<>("Product");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setPrefWidth(200);

		TableColumn<Item, String> typeCol = new TableColumn<>("Type");
		typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
		typeCol.setPrefWidth(150);

		TableColumn<Item, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setCellFactory(tc -> new TableCell<>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				setText(empty || price == null ? null : String.format("$%.2f", price));
			}
		});
		priceCol.setPrefWidth(100);

		TableColumn<Item, Void> actionCol = new TableColumn<>("Action");
		actionCol.setCellFactory(param -> new TableCell<>() {
			private final Button addBtn = new Button("Add to Cart");
			{
				addBtn.setStyle("-fx-background-color: lightblue; -fx-font-size: 12px;");
				addBtn.setOnAction(e -> {
					Item item = getTableView().getItems().get(getIndex());
					addToCart(item, customerId); // هنا فقط نمرر العنصر والعميل
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : addBtn);
			}
		});

		productsTable.getColumns().addAll(nameCol, typeCol, priceCol, actionCol);
		productsTable.setPrefHeight(300);

		Label cartLabel = new Label("Cart: 0 items");
		cartLabel.setFont(Font.font("Arial", 16));
		cartLabel.setTextFill(Color.DARKRED);

		Button viewCartBtn = new Button("View Cart (" + cartItems.size() + ")");
		viewCartBtn.setStyle("-fx-background-color: lightgreen; -fx-font-size: 14px;");
		viewCartBtn.setOnAction(e -> showCartWindow(cartItems));

		Button backBtn = new Button("Log Out");
		backBtn.setOnAction(e -> showAuthPage("Login"));

		// تحديث نص الزر والعلامة عند تغير محتوى السلة
		cartItems.addListener((ListChangeListener<CartItem>) c -> {
			viewCartBtn.setText("View Cart (" + cartItems.size() + ")");
			cartLabel.setText("Cart: " + cartItems.size() + " items");
		});

		VBox layout = new VBox(15, welcomeLabel, pointsLabel, productsTable, cartLabel, viewCartBtn, backBtn);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(30));
		layout.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		return layout;
	}

	// تعديل دالة إضافة للسلة لتضيف CartItem إلى cartItems بدلاً من items (قائمة
	// المنتجات)
	private void addToCart(Item item, int customerId) {
		if (item == null)
			return;

		TextInputDialog dialog = new TextInputDialog("1");
		dialog.setTitle("Add to Cart");
		dialog.setHeaderText("Add " + item.getName() + " to cart");
		dialog.setContentText("Enter quantity:");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(qtyStr -> {
			try {
				int quantity = Integer.parseInt(qtyStr);
				if (quantity <= 0)
					throw new NumberFormatException();

				boolean success = DBConnect.addToCart(customerId, item.getId(), quantity);
				if (success) {
					// إضافة CartItem جديد أو تحديث الموجود (يمكنك تعديل هذا حسب منطقك)
					CartItem existing = null;
					for (CartItem ci : cartItems) {
						if (ci.getItemId() == item.getId()) {
							existing = ci;
							break;
						}
					}
					if (existing != null) {
						existing.setQuantity(existing.getQuantity() + quantity);
						cartItems.set(cartItems.indexOf(existing), existing); // لتحديث الـ ObservableList
					} else {
						cartItems.add(new CartItem(customerId, item.getId(), quantity));
					}

					System.out.println("✅ Added " + quantity + " of " + item.getName() + " to cart.");
				} else {
					System.out.println("❌ Failed to add to cart.");
				}
			} catch (NumberFormatException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid quantity entered!", ButtonType.OK);
				alert.showAndWait();
			}
		});
	}

	private ObservableList<CartDisplayItem> getCartItemsForDisplay(CartItem selectedCartItem) {
		ObservableList<CartDisplayItem> displayList = FXCollections.observableArrayList();

		try {
			Item item = DBConnect.getItemById(selectedCartItem.getItemId());
			if (item != null) {
				displayList.add(new CartDisplayItem(item.getName(), item.getPrice(), selectedCartItem.getQuantity()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return displayList;
	}

	private void showCartWindow(List<CartItem> selectedCartItems) {
		ObservableList<CartDisplayItem> cartItems = FXCollections.observableArrayList();

		for (CartItem cartItem : selectedCartItems) {
			try {
				Item item = DBConnect.getItemById(cartItem.getItemId());
				if (item != null) {
					cartItems.add(new CartDisplayItem(item.getName(), item.getPrice(), cartItem.getQuantity()));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		Stage cartStage = new Stage();
		cartStage.setTitle("Your Cart");

		Label cartTitle = new Label("Items in your cart:");
		cartTitle.setFont(Font.font("Arial", 18));

		TableView<CartDisplayItem> cartTable = new TableView<>(cartItems);

		cartTable.setPrefHeight(300);
		cartTable.setPrefWidth(500);

		TableColumn<CartDisplayItem, String> nameCol = new TableColumn<>("Product");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setPrefWidth(200);

		TableColumn<CartDisplayItem, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setCellFactory(tc -> new TableCell<>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				setText(empty || price == null ? null : String.format("$%.2f", price));
			}
		});
		priceCol.setPrefWidth(100);

		TableColumn<CartDisplayItem, Integer> quantityCol = new TableColumn<>("Quantity");
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		quantityCol.setPrefWidth(100);

		TableColumn<CartDisplayItem, Void> removeCol = new TableColumn<>("Remove");
		removeCol.setCellFactory(param -> new TableCell<>() {
			private final Button removeBtn = new Button("❌");
			{
				removeBtn.setOnAction(e -> {
					CartDisplayItem item = getTableView().getItems().get(getIndex());
					cartItems.remove(item);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : removeBtn);
			}
		});

		cartTable.getColumns().addAll(nameCol, priceCol, quantityCol, removeCol);

		Label totalLabel = new Label("Total: $" + calculateTotal(cartItems));
		totalLabel.setFont(Font.font("Arial", 16));

		cartItems.addListener((ListChangeListener<CartDisplayItem>) c -> {
			totalLabel.setText("Total: $" + calculateTotal(cartItems));
		});

		Label customerInfoLabel = new Label("Customer Information");
		customerInfoLabel.setFont(Font.font("Arial", 18));

		TextField nameField = new TextField();
		nameField.setPromptText("Full Name");

		TextField phoneField = new TextField();
		phoneField.setPromptText("Phone Number");

		TextField emailField = new TextField();
		emailField.setPromptText("Email");

		TextField cityField = new TextField();
		cityField.setPromptText("City");

		TextField streetField = new TextField();
		streetField.setPromptText("Street");

		TextField streetNumberField = new TextField();
		streetNumberField.setPromptText("Street Number");

		TextField streetNameField = new TextField();
		streetNameField.setPromptText("Street Name");

		// Replace existing checkoutBtn with:
		HBox paymentButtons = new HBox(10);
		paymentButtons.setAlignment(Pos.CENTER);

		Button checkoutBtn = new Button("Checkout");

		Button cashBtn = new Button("Pay with Cash");
		Button visaBtn = new Button("Pay with Visa");

		cashBtn.setOnAction(e -> {
			if (validateCustomerInfo(nameField, emailField, phoneField)) {
				processOrderWithPayment(cartItems, nameField, streetNameField, phoneField, "Cash",
						customerId -> showAlert("✅ Purchase Successful with Cash!"));
				cartStage.close();
			}
		});

		visaBtn.setOnAction(e -> {
			if (validateCustomerInfo(nameField, emailField, phoneField)) {
				showVisaForm(cartItems, nameField, streetNameField, phoneField, customer -> {
					processOrderWithPayment(cartItems, nameField, streetNameField, phoneField, "Credit Card",
							customerId -> showAlert("✅ Payment Successful via Visa!"));
					cartStage.close();
				});
			}
		});

		paymentButtons.getChildren().addAll(cashBtn, visaBtn);

		VBox customerInfoBox = new VBox(10, customerInfoLabel, nameField, emailField, phoneField, cityField,
				streetField, streetNumberField, streetNameField, paymentButtons);
		customerInfoBox.setPadding(new Insets(20));

		VBox layout = new VBox(15, cartTitle, cartTable, totalLabel, customerInfoBox);
		layout.setPadding(new Insets(20));

		Scene scene = new Scene(layout, 550, 550);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		cartStage.setScene(scene);
		cartStage.show();
	}

	private double calculateTotal(ObservableList<CartDisplayItem> cartItems) {
		double total = 0;
		for (CartDisplayItem item : cartItems) {
			total += item.getPrice() * item.getQuantity();
		}
		return total;
	}

	private boolean validateCustomerInfo(TextField name, TextField email, TextField phone) {
		if (name.getText().isEmpty() || email.getText().isEmpty() || phone.getText().isEmpty()) {
			showAlert("Please fill all customer information fields");
			return false;
		}
		return true;
	}

	private void processOrder(ObservableList<CartDisplayItem> items, String name, String email, String phone) {
		// Here you would normally save the order to database
		double total = calculateTotal(items);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Order Complete");
		alert.setHeaderText(null);
		alert.setContentText("Thank you for your purchase, " + name + "!\n\n" + "Items: " + items.size() + "\n"
				+ "Total: $" + String.format("%.2f", total) + "\n\n" + "A confirmation has been sent to " + email);
		alert.showAndWait();

		// Clear the cart after successful order
		items.clear();
	}

	private void launchApp(Application app) {
		try {
			app.start(new Stage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void processOrderWithPayment(ObservableList<CartDisplayItem> items, TextField nameField,
			TextField streetField, TextField phoneField, String paymentMethod,
			java.util.function.Consumer<Integer> afterPaymentCallback) {
		String name = nameField.getText();
		String phone = phoneField.getText();
		String streetName = streetField.getText();

		Customer customer = new Customer(name, phone, "", "", "", streetName, 0);
		DBConnect.addCustomer(customer); // Should return or update with ID
		int customerId = customer.getId();

		double total = calculateTotal(items);

		Order order = new Order(0, new Date(), total, 0, "", "", name, "", 1, 5, customerId, 2);
		DBConnect.addOrder(order);
		int saleId = DBConnect.getLastSaleId(); // You’ll need to implement this if not already

		DBConnect.addPayment(saleId, customerId, total, paymentMethod);

		afterPaymentCallback.accept(customerId);
		items.clear();
	}

	private void showVisaForm(List<CartDisplayItem> cartItems, TextField nameField, TextField streetField,
			TextField phoneField, java.util.function.Consumer<Customer> onVisaSubmitted) {
		Stage visaStage = new Stage();
		visaStage.setTitle("Enter Card Details");

		TextField cardNumberField = new TextField();
		cardNumberField.setPromptText("Card Number");

		TextField cardHolderField = new TextField();
		cardHolderField.setPromptText("Card Holder Name");

		TextField expiryField = new TextField();
		expiryField.setPromptText("Expiry (MM/YY)");

		PasswordField cvvField = new PasswordField();
		cvvField.setPromptText("CVV");

		Button payBtn = new Button("Confirm Payment");
		payBtn.setStyle("-fx-background-color: darkgreen; -fx-text-fill: white;");

		payBtn.setOnAction(e -> {
			// In real app: validate, encrypt, process
			if (cardNumberField.getText().isEmpty() || cvvField.getText().isEmpty()) {
				showAlert("Card details incomplete!");
				return;
			}
			visaStage.close();
			onVisaSubmitted.accept(null); // we don’t need full customer here
		});

		VBox form = new VBox(10, new Label("Enter Visa Details"), cardNumberField, cardHolderField, expiryField,
				cvvField, payBtn);
		form.setPadding(new Insets(20));
		form.setAlignment(Pos.CENTER);

		Scene scene = new Scene(form, 300, 300);
		visaStage.setScene(scene);
		visaStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}