package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Stats extends Application {

	private TextArea queryOutput;
	private ComboBox<String> predefinedQueriesCombo;
	private Map<String, String> predefinedQueryMap;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Employee Query Tool - Yes Baby");

		Label headerLabel = new Label("Database Query Executor");
		headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		initializePredefinedQueries();

		predefinedQueriesCombo = new ComboBox<>();
		predefinedQueriesCombo.setPromptText("Select a Predefined Query");
		predefinedQueriesCombo.getItems().addAll(predefinedQueryMap.keySet());

		Button executeButton = new Button("Execute Selected Query");
		executeButton.setOnAction(e -> executeQuery());

		queryOutput = new TextArea();
		queryOutput.setEditable(false);
		queryOutput.setPromptText("Query results will appear here after execution.\n"
				+ "For queries requiring parameters (e.g., Order ID, Month Number), "
				+ "you will be prompted to enter the value.");
		queryOutput.setWrapText(true);
		queryOutput.setPrefRowCount(15);

		Button backButton = new Button("← Back to Employee Home");
		backButton.setOnAction(e -> {
			primaryStage.close();
		});

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(15));
		layout.getChildren().addAll(headerLabel, predefinedQueriesCombo, executeButton, queryOutput, backButton);

		Scene scene = new Scene(layout, 800, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initializePredefinedQueries() {
		predefinedQueryMap = new LinkedHashMap<>();

		predefinedQueryMap.put("Products Sorted by Category",
				"SELECT item_name, item_type, item_price FROM Items ORDER BY item_type");
		predefinedQueryMap.put("Products Sorted by Price",
				"SELECT item_name, item_type, item_price FROM Items ORDER BY item_price");
		predefinedQueryMap.put("Recent Customers (Last Month)",
				"SELECT c.customer_id, c.customer_name, c.customer_phone_number, c.customer_address_city, c.street, c.street_number, c.street_name, c.customer_points FROM Customers c JOIN Sale s ON c.customer_id = s.customer_id WHERE s.sale_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) ORDER BY c.customer_name");
		predefinedQueryMap.put("Top 5 Best-Selling Products",
				"SELECT i.item_name, i.item_type, i.item_price, SUM(s.quantity) as total_sold FROM Items i JOIN Sale s ON i.item_id = s.item_id GROUP BY i.item_id ORDER BY total_sold DESC LIMIT 5");
		predefinedQueryMap.put("Quarterly Revenue by Category",
				"SELECT i.item_type, SUM(s.sale_price) as total_revenue FROM Items i JOIN Sale s ON i.item_id = s.item_id WHERE s.sale_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) GROUP BY i.item_type");
		predefinedQueryMap.put("All Suppliers/Publishers",
				"SELECT p.publisher_id, p.publisher_phone, s.store_Name FROM Publisher p JOIN Store s ON p.store_id = s.store_id ORDER BY s.store_Name");
		predefinedQueryMap.put("All Pending Orders",
				"SELECT sale_id, sale_date, sale_price, quantity, store_id, employee_id, customer_id, item_id FROM Sale WHERE sale_date >= DATE('now') ORDER BY sale_date");
		predefinedQueryMap.put("Total Stock by Category",
				"SELECT i.item_type, SUM(inv.quantity) as total_stock FROM Items i JOIN Inventory inv ON i.item_id = inv.item_id GROUP BY i.item_type");
		predefinedQueryMap.put("Highest Paid Employees", "SELECT * FROM Employee ORDER BY employee_salary DESC");
		predefinedQueryMap.put("All Order Details (ID, Date, Customer)",
				"SELECT s.sale_id, s.sale_date, c.customer_name FROM Sale s JOIN Customers c ON s.customer_id = c.customer_id ORDER BY s.sale_date");
		predefinedQueryMap.put("Available Products (Stock > 10)",
				"SELECT i.item_name, i.item_type, inv.quantity FROM Items i JOIN Inventory inv ON i.item_id = inv.item_id WHERE inv.quantity > 10 ORDER BY inv.quantity");
		// Parameterized queries now handled via TextInputDialog
		predefinedQueryMap.put("Order Details by ID",
				"SELECT i.item_name, s.quantity, s.sale_price FROM Sale s JOIN Items i ON s.item_id = i.item_id WHERE s.sale_id = ?");
		predefinedQueryMap.put("Total Purchases by Customer",
				"SELECT c.customer_name, COUNT(s.sale_id) as purchase_count FROM Customers c LEFT JOIN Sale s ON c.customer_id = s.customer_id GROUP BY c.customer_id ORDER BY purchase_count DESC");
		predefinedQueryMap.put("Total Revenue by Product Category",
				"SELECT i.item_type, SUM(s.sale_price) as total_revenue FROM Items i JOIN Sale s ON i.item_id = s.item_id GROUP BY i.item_type");
		predefinedQueryMap.put("Credit Card Payments",
				"SELECT payment_id, amount, payment_date FROM Payments WHERE payment_method = 'Credit Card' ORDER BY payment_date");
		predefinedQueryMap.put("Most Popular Product",
				"SELECT i.item_name, SUM(s.quantity) as total_quantity FROM Items i JOIN Sale s ON i.item_id = s.item_id GROUP BY i.item_id ORDER BY total_quantity DESC LIMIT 1");
		predefinedQueryMap.put("Products by Price Descending",
				"SELECT item_name, item_type, item_price FROM Items ORDER BY item_price DESC");
		predefinedQueryMap.put("Transactions by Month",
				"SELECT p.payment_method, COUNT(p.payment_id) as transaction_count, \n"
						+ "       SUM(p.amount) as total_amount \n" + "FROM Payments p \n"
						+ "WHERE MONTH(p.payment_date) = ? \n" + "GROUP BY p.payment_method;\n");
		predefinedQueryMap.put("Top Sales Employees",
				"SELECT e.employee_name, SUM(s.sale_price) as total_sales FROM Employee e JOIN Sale s ON e.employee_id = s.employee_id GROUP BY e.employee_id ORDER BY total_sales DESC");
		predefinedQueryMap.put("Total Product Quantity Sold by Category",
				"SELECT i.item_type, SUM(s.quantity) as total_sold FROM Items i JOIN Sale s ON i.item_id = s.item_id GROUP BY i.item_type");
	}

	/**
	 * Executes the SQL query based on the selected predefined query. Handles
	 * parameterized queries by prompting the user for input.
	 */
	private void executeQuery() {
		String selectedQueryName = predefinedQueriesCombo.getValue();
		if (selectedQueryName == null) {
			queryOutput.setText("Please select a query from the dropdown.");
			return;
		}

		String sql = predefinedQueryMap.get(selectedQueryName);
		if (sql == null) {
			queryOutput.setText("Error: Selected query not found.");
			return;
		}

		// Handle parameterized queries
		if (selectedQueryName.equals("Order Details by ID")) {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Enter Order ID");
			dialog.setHeaderText("Enter the Order ID:");
			dialog.setContentText("Order ID:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent() && !result.get().trim().isEmpty()) {
				try {
					int orderId = Integer.parseInt(result.get().trim());
					// Use PreparedStatement for parameterized queries
					executeParameterizedQuery(sql, orderId);
					return; // Exit here as parameterized query is handled
				} catch (NumberFormatException e) {
					queryOutput.setText("Invalid Order ID. Please enter a number.");
					return;
				}
			} else {
				queryOutput.setText("Order ID cannot be empty. Query cancelled.");
				return;
			}
		} else if (selectedQueryName.equals("Transactions by Month")) {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Enter Month Number");
			dialog.setHeaderText("Enter the Month Number (1-12):");
			dialog.setContentText("Month Number:");
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent() && !result.get().trim().isEmpty()) {
				try {
					int monthNum = Integer.parseInt(result.get().trim());
					if (monthNum < 1 || monthNum > 12) {
						queryOutput.setText("Invalid month number. Please enter a value between 1 and 12.");
						return;
					}
					// Format month number to two digits (e.g., "01" for January) for STRFTIME
					String monthStr = String.format("%02d", monthNum);
					// Use PreparedStatement for parameterized queries
					executeParameterizedQuery(sql, monthStr);
					return; // Exit here as parameterized query is handled
				} catch (NumberFormatException e) {
					queryOutput.setText("Invalid Month Number. Please enter a number.");
					return;
				}
			} else {
				queryOutput.setText("Month Number cannot be empty. Query cancelled.");
				return;
			}
		}

		// For non-parameterized queries, proceed with direct execution
		try (Connection conn = DBConnect.getConnection(); Statement stmt = conn.createStatement()) {

			if (sql.toLowerCase().startsWith("select")) {
				ResultSet rs = stmt.executeQuery(sql);
				displayResults(rs);
				rs.close();
			} else {
				int rowsAffected = stmt.executeUpdate(sql);
				queryOutput.setText("Query executed successfully.\nRows affected: " + rowsAffected);
			}

		} catch (SQLException ex) {
			queryOutput.setText("Error executing query: " + ex.getMessage() + "\n"
					+ "Please check your SQL syntax or database connection.");
			ex.printStackTrace();
		}
	}

	/**
	 * Helper method to execute parameterized queries using PreparedStatement.
	 */
	private void executeParameterizedQuery(String sql, Object param) {
		try (Connection conn = DBConnect.getConnection();
				java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

			if (param instanceof Integer) {
				pstmt.setInt(1, (Integer) param);
			} else if (param instanceof String) {
				pstmt.setString(1, (String) param);
			}
			// Add more else if blocks for other parameter types if needed

			if (sql.toLowerCase().startsWith("select")) {
				ResultSet rs = pstmt.executeQuery();
				displayResults(rs);
				rs.close();
			} else {
				int rowsAffected = pstmt.executeUpdate();
				queryOutput.setText("Query executed successfully.\nRows affected: " + rowsAffected);
			}

		} catch (SQLException ex) {
			queryOutput.setText("Error executing query: " + ex.getMessage() + "\n"
					+ "Please check your SQL syntax or database connection.");
			ex.printStackTrace();
		}
	}

	/**
	 * Helper method to format and display ResultSet data in the queryOutput
	 * TextArea.
	 */
	private void displayResults(ResultSet rs) throws SQLException {
		StringBuilder results = new StringBuilder();
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		// Append column headers
		for (int i = 1; i <= columnCount; i++) {
			results.append(String.format("%-25s", metaData.getColumnName(i)));
		}
		results.append("\n");
		for (int i = 1; i <= columnCount; i++) {
			results.append("-------------------------");
		}
		results.append("\n");

		// Append data
		while (rs.next()) {
			for (int i = 1; i <= columnCount; i++) {
				results.append(String.format("%-25s", rs.getString(i) != null ? rs.getString(i) : "NULL"));
			}
			results.append("\n");
		}
		queryOutput.setText(results.toString());
	}

	public static void main(String[] args) {
		launch(args);
	}
}
