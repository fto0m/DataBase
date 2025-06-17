package application;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnect {
	private static final String URL = "jdbc:mysql://localhost:3306/yesBaby?useSSL=false";
	private static final String USER = "root";
	private static final String PASSWORD = "16122004@fatimah";

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("MySQL JDBC Driver not found!", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}

	public static boolean addToCart(int customerId, int itemId, int quantity) {
		String sql = "INSERT INTO Cart (customer_id, item_id, quantity) VALUES (?, ?, ?)";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
			stmt.setInt(1, customerId);
			stmt.setInt(2, itemId);
			stmt.setInt(3, quantity);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static List<CartItem> getAllCarts() {
		List<CartItem> cartItems = new ArrayList<>();
		String query = "SELECT * FROM Cart";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				int customerId = rs.getInt("customer_id");
				int itemId = rs.getInt("item_id");
				int quantity = rs.getInt("quantity");

				cartItems.add(new CartItem(customerId, itemId, quantity));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return cartItems;
	}

	public static Item getItemById(int itemId) throws SQLException {
		String query = "SELECT * FROM Items WHERE item_id = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, itemId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Item(rs.getInt("item_id"), rs.getString("item_name"), rs.getDouble("item_price"),
						rs.getString("item_type"), 0, " ", " ");
			}
		}
		return null;
	}

	public static int createOrder(int customerId, double totalAmount) {
		String sql = "INSERT INTO Orders (customer_id, total_amount) VALUES (?, ?)";
		try (Connection conn = getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, customerId);
			stmt.setDouble(2, totalAmount);
			stmt.executeUpdate();

			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static boolean addOrderItems(int orderId, List<Item> items) {
		String sql = "INSERT INTO Order_Items (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			for (Item item : items) {
				stmt.setInt(1, orderId);
				stmt.setInt(2, item.getId());
				stmt.setInt(3, 1);
				stmt.setDouble(4, item.getPrice());
				stmt.addBatch();
			}

			stmt.executeBatch();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean clearCart(int customerId) {
		String sql = "DELETE FROM Cart WHERE customer_id = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
			stmt.setInt(1, customerId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static int getCustomerPoints(int customerId) {
		String sql = "SELECT points FROM Customers WHERE customer_id = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, customerId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("points");
				}
			}
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	// Customer-related database operations
	public static List<Customer> getAllCustomers() {
		List<Customer> customers = new ArrayList<>();
		String query = "SELECT * FROM Customers";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Customer customer = new Customer(rs.getString("customer_name"), rs.getString("customer_phone_number"),
						rs.getString("customer_address_city"), rs.getString("street"), rs.getString("street_number"),
						rs.getString("street_name"), rs.getInt("customer_points"));
				customer.setId(rs.getInt("customer_id"));
				customers.add(customer);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return customers;
	}

	public static boolean addCustomer(Customer customer) {
		String query = "INSERT INTO Customers (customer_name, customer_phone_number, customer_address_city, "
				+ "street, street_number, street_name, customer_points) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, customer.getName());
			pstmt.setString(2, customer.getPhone());
			pstmt.setString(3, customer.getCity());
			pstmt.setString(4, customer.getStreet());
			pstmt.setString(5, customer.getStreetNumber());
			pstmt.setString(6, customer.getStreetName());
			pstmt.setInt(7, customer.getPoints());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						customer.setId(generatedKeys.getInt(1));
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateCustomer(Customer customer) {
		String query = "UPDATE Customers SET customer_name=?, customer_phone_number=?, "
				+ "customer_address_city=?, street=?, street_number=?, street_name=?, "
				+ "customer_points=? WHERE customer_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, customer.getName());
			pstmt.setString(2, customer.getPhone());
			pstmt.setString(3, customer.getCity());
			pstmt.setString(4, customer.getStreet());
			pstmt.setString(5, customer.getStreetNumber());
			pstmt.setString(6, customer.getStreetName());
			pstmt.setInt(7, customer.getPoints());
			pstmt.setInt(8, customer.getId());

			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteCustomer(int customerId) {
		String query = "DELETE FROM Customers WHERE customer_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.execute("SET FOREIGN_KEY_CHECKS = 0");
			pstmt.setInt(1, customerId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Add these methods to your existing DBConnect class

	public static List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<>();
		String query = "SELECT e.employee_id, e.employee_name, e.employee_salary, e.epmloyee_phone_number, "
				+ "e.store_id, s.store_Name FROM Employee e JOIN Store s ON e.store_id = s.store_id";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Employee employee = new Employee(rs.getInt("employee_id"), rs.getString("employee_name"),
						rs.getDouble("employee_salary"), rs.getString("epmloyee_phone_number"), rs.getInt("store_id"),
						rs.getString("store_Name"));
				employees.add(employee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}

	public static boolean addEmployee(Employee employee) {
		String query = "INSERT INTO Employee (employee_name, employee_salary, epmloyee_phone_number, store_id) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, employee.getName());
			pstmt.setDouble(2, employee.getSalary());
			pstmt.setString(3, employee.getPhone());
			pstmt.setInt(4, employee.getStoreId());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						employee.setId(generatedKeys.getInt(1));
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateEmployee(Employee employee) {
		String query = "UPDATE Employee SET employee_name=?, employee_salary=?, "
				+ "epmloyee_phone_number=?, store_id=? WHERE employee_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, employee.getName());
			pstmt.setDouble(2, employee.getSalary());
			pstmt.setString(3, employee.getPhone());
			pstmt.setInt(4, employee.getStoreId());
			pstmt.setInt(5, employee.getId());

			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteEmployee(int employeeId) {
		String query = "DELETE FROM Employee WHERE employee_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.execute("SET FOREIGN_KEY_CHECKS = 0");

			pstmt.setInt(1, employeeId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("Error closing connection");
				e.printStackTrace();
			}
		}
	}

	// Add these methods to your DBConnect class

	// ITEMS MANAGEMENT
	public static List<Item> getAllItems() {
		List<Item> items = new ArrayList<>();
		String query = "SELECT i.item_id, i.item_name, i.item_price, i.item_type, "
				+ "p.publisher_id, p.publisher_phone, s.store_Name " + "FROM Items i "
				+ "JOIN Publisher p ON i.publisher_id = p.publisher_id " + "JOIN Store s ON p.store_id = s.store_id";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Item item = new Item(rs.getInt("item_id"), rs.getString("item_name"), rs.getDouble("item_price"),
						rs.getString("item_type"), rs.getInt("publisher_id"), rs.getString("publisher_phone"),
						rs.getString("store_Name"));
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	public static boolean addItem(Item item) {
		String query = "INSERT INTO Items (item_name, item_price, item_type, publisher_id) " + "VALUES (?, ?, ?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, item.getName());
			pstmt.setDouble(2, item.getPrice());
			pstmt.setString(3, item.getType());
			pstmt.setInt(4, item.getPublisherId());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						item.setId(generatedKeys.getInt(1));
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateItem(Item item) {
		String query = "UPDATE Items SET item_name=?, item_price=?, item_type=?, publisher_id=? " + "WHERE item_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, item.getName());
			pstmt.setDouble(2, item.getPrice());
			pstmt.setString(3, item.getType());
			pstmt.setInt(4, item.getPublisherId());
			pstmt.setInt(5, item.getId());

			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteItem(int itemId) {
		String query = "DELETE FROM Items WHERE item_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.execute("SET FOREIGN_KEY_CHECKS = 0");
			pstmt.setInt(1, itemId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// PUBLISHERS MANAGEMENT
	public static List<Publisher> getAllPublishers() {
		List<Publisher> publishers = new ArrayList<>();
		String query = "SELECT p.publisher_id, p.publisher_phone, p.store_id, s.store_Name "
				+ "FROM Publisher p JOIN Store s ON p.store_id = s.store_id";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Publisher publisher = new Publisher(rs.getInt("publisher_id"), rs.getString("publisher_phone"),
						rs.getInt("store_id"), rs.getString("store_Name"));
				publishers.add(publisher);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return publishers;
	}

	public static boolean addPublisher(Publisher publisher) {
		String query = "INSERT INTO Publisher (publisher_phone, store_id) VALUES (?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, publisher.getPhone());
			pstmt.setInt(2, publisher.getStoreId());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						publisher.setId(generatedKeys.getInt(1));
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updatePublisher(Publisher publisher) {
		String query = "UPDATE Publisher SET publisher_phone=?, store_id=? WHERE publisher_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, publisher.getPhone());
			pstmt.setInt(2, publisher.getStoreId());
			pstmt.setInt(3, publisher.getId());

			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deletePublisher(int publisherId) {
		String query = "DELETE FROM Publisher WHERE publisher_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.execute("SET FOREIGN_KEY_CHECKS = 0");

			pstmt.setInt(1, publisherId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// ORDERS MANAGEMENT
// Add these methods to your DBConnect class

	// For Orders
	public static List<Order> getAllOrders() {
		List<Order> orders = new ArrayList<>();
		String query = "SELECT s.sale_id, s.sale_date, s.sale_price, s.quantity, "
				+ "st.store_id, st.store_Name, e.employee_id, e.employee_name, "
				+ "c.customer_id, c.customer_name, i.item_id, i.item_name " + "FROM Sale s "
				+ "JOIN Store st ON s.store_id = st.store_id " + "JOIN Employee e ON s.employee_id = e.employee_id "
				+ "JOIN Customers c ON s.customer_id = c.customer_id " + "JOIN Items i ON s.item_id = i.item_id";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Order order = new Order(rs.getInt("sale_id"), rs.getDate("sale_date"), rs.getDouble("sale_price"),
						rs.getInt("quantity"), rs.getString("store_Name"), rs.getString("employee_name"),
						rs.getString("customer_name"), rs.getString("item_name"), rs.getInt("store_id"),
						rs.getInt("employee_id"), rs.getInt("customer_id"), rs.getInt("item_id"));
				orders.add(order);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	public static boolean addOrder(Order order) {
		String query = "INSERT INTO Sale (sale_date, sale_price, quantity, store_id, "
				+ "employee_id, customer_id, item_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setDate(1, new Date(order.getDate().getTime()));
			pstmt.setDouble(2, order.getPrice());
			pstmt.setInt(3, order.getQuantity());
			pstmt.setInt(4, order.getStoreId());
			pstmt.setInt(5, order.getEmployeeId());
			pstmt.setInt(6, order.getCustomerId());
			pstmt.setInt(7, order.getItemId());

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						order.setId(generatedKeys.getInt(1));
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteOrder(int orderId) {
		String query = "DELETE FROM Sale WHERE sale_id=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.execute("SET FOREIGN_KEY_CHECKS = 0");
			pstmt.setInt(1, orderId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Helper methods for getting IDs from names
	public static List<String> getAllCustomerNames() {
		List<String> names = new ArrayList<>();
		String query = "SELECT customer_name FROM Customers";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				names.add(rs.getString("customer_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return names;
	}

	public static int getCustomerIdByName(String name) {
		String query = "SELECT customer_id FROM Customers WHERE customer_name = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("customer_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	// In your DBConnect class

	public static Customer getCustomerByPhone(String phone) throws SQLException {
		String query = "SELECT * FROM Customers WHERE customer_phone_number = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, phone);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return new Customer(rs.getString("customer_name"), rs.getString("customer_phone_number"),
						rs.getString("customer_address_city"), rs.getString("street"), rs.getString("street_number"),
						rs.getString("street_name"), rs.getInt("customer_id"));
			}
		}
		return null;
	}

	public static List<String> getAllEmployeeNames() {
		List<String> names = new ArrayList<>();
		String query = "SELECT employee_name FROM Employee";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				names.add(rs.getString("employee_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return names;
	}

	public static int getEmployeeIdByName(String name) {
		String query = "SELECT employee_id FROM Employee WHERE employee_name = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("employee_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static List<String> getAllStoreNames() {
		List<String> names = new ArrayList<>();
		String query = "SELECT store_Name FROM Store";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				names.add(rs.getString("store_Name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return names;
	}

	public static int getStoreIdByName(String name) {
		String query = "SELECT store_id FROM Store WHERE store_Name = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("store_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static List<String> getAllItemNames() {
		List<String> names = new ArrayList<>();
		String query = "SELECT item_name FROM Items";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				names.add(rs.getString("item_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return names;
	}

	public static int getItemIdByName(String name) {
		String query = "SELECT item_id FROM Items WHERE item_name = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("item_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static double getItemPriceById(int itemId) {
		String query = "SELECT item_price FROM Items WHERE item_id = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, itemId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getDouble("item_price");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	// ITEM TYPES MANAGEMENT
	public static List<String> getAllItemTypes() {
		List<String> types = new ArrayList<>();
		String query = "SELECT DISTINCT item_type FROM Items";

		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				types.add(rs.getString("item_type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return types;
	}

	public static boolean addItemType(String itemType) {
		// Since item_type is just a field in Items table, we can't add types
		// independently
		// This would need to be handled when adding/updating items
		return false;
	}

	// sign in
	public static User authenticate(String username, String password, String role) throws SQLException {
		// Your database schema includes a `User` table with username, password, role,
		// and reference_id.
		// It does NOT have separate email/password columns in Employee or Customers
		// table for authentication directly.
		// We should authenticate against the `User` table.

		String query = "SELECT user_id, username, password, role, reference_id FROM User WHERE username = ? AND password = ? AND role = ?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, username);
			pstmt.setString(2, password); // **SECURITY WARNING: PLAIN TEXT PASSWORD USAGE**
			pstmt.setString(3, role);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int userId = rs.getInt("user_id");
					String dbUsername = rs.getString("username");
					String dbPassword = rs.getString("password"); // Hashed password in production
					String dbRole = rs.getString("role");
					int referenceId = rs.getInt("reference_id");

					// Create and return the User object with all 5 arguments
					return new User(userId, dbUsername, dbPassword, dbRole, referenceId);
				}
			}
		}
		return null; // Authentication failed
	}

	public static boolean createUser(User user) throws SQLException {
		String query = "INSERT INTO User (username, password, role, reference_id) VALUES (?, ?, ?, ?)";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.setInt(4, user.getReferenceId());

			return pstmt.executeUpdate() > 0;
		}
	}

	public static boolean register(String username, String password, String role) throws SQLException {
		String query = "INSERT INTO User (username, password, role, reference_id) VALUES (?, ?, ?, ?)";
		int referenceId = 0;
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password); // ⚠️ يجب تشفير كلمة السر في الإنتاج
			stmt.setString(3, role);
			stmt.setInt(4, referenceId);

			int rows = stmt.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	// Query 1: Retrieve the names, categories, and prices of all products, sorted by category
	public static List<Item> getProductsSortedByCategory() {
		List<Item> items = new ArrayList<>();
		String query = "SELECT item_name, item_type, item_price FROM Items ORDER BY item_type";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Item item = new Item(0, rs.getString("item_name"), rs.getDouble("item_price"),
						rs.getString("item_type"), 0, "", "");
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	// Query 2: Retrieve the names, categories, and prices of all products, sorted by price
	public static List<Item> getProductsSortedByPrice() {
		List<Item> items = new ArrayList<>();
		String query = "SELECT item_name, item_type, item_price FROM Items ORDER BY item_price";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Item item = new Item(0, rs.getString("item_name"), rs.getDouble("item_price"),
						rs.getString("item_type"), 0, "", "");
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	public static List<Customer> getRecentCustomers() {
		List<Customer> customers = new ArrayList<>();
		String query = "SELECT DISTINCT c.* FROM Customers c " +
				"JOIN Sale s ON c.customer_id = s.customer_id " +
				"WHERE s.sale_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
				"ORDER BY c.customer_name";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Customer customer = new Customer(
						rs.getString("customer_name"),
						rs.getString("customer_phone_number"),
						rs.getString("customer_address_city"),
						rs.getString("street"),
						rs.getString("street_number"),
						rs.getString("street_name"),
						rs.getInt("customer_points")
				);
				customer.setId(rs.getInt("customer_id"));
				customers.add(customer);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching recent customers: " + e.getMessage());
		}
		return customers;
	}

	// Query 4: Retrieve top 5 best-selling products
	public static List<Item> getTopSellingProducts() {
		List<Item> items = new ArrayList<>();
		String query = "SELECT i.item_name, i.item_type, i.item_price, SUM(s.quantity) as total_sold " +
				"FROM Items i JOIN Sale s ON i.item_id = s.item_id " +
				"GROUP BY i.item_id ORDER BY total_sold DESC LIMIT 5";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Item item = new Item(0, rs.getString("item_name"), rs.getDouble("item_price"),
						rs.getString("item_type"), 0, "", "");
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	// Query 5: Retrieve total sales revenue for last quarter by category
	public static List<String[]> getQuarterlyRevenueByCategory() {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT i.item_type, SUM(s.sale_price) as total_revenue " +
				"FROM Items i JOIN Sale s ON i.item_id = s.item_id " +
				"WHERE s.sale_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) " +
				"GROUP BY i.item_type";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String[] row = {rs.getString("item_type"), rs.getString("total_revenue")};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 6: Retrieve names and contact details of all suppliers (publishers)
	public static List<Publisher> getAllSuppliers() {
		List<Publisher> publishers = new ArrayList<>();
		String query = "SELECT p.publisher_id, p.publisher_phone, s.store_Name " +
				"FROM Publisher p JOIN Store s ON p.store_id = s.store_id " +
				"ORDER BY s.store_Name";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Publisher publisher = new Publisher(
						rs.getInt("publisher_id"),
						rs.getString("publisher_phone"),
						rs.getInt("store_id"),
						rs.getString("store_Name")
				);
				publishers.add(publisher);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return publishers;
	}

	// Query 7: Retrieve all pending orders (assuming pending means not delivered)
	public static List<Order> getPendingOrders() {
		List<Order> orders = new ArrayList<>();
		String query = "SELECT * FROM Sale WHERE sale_date >= CURDATE() ORDER BY sale_date";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Order order = new Order(
						rs.getInt("sale_id"),
						rs.getDate("sale_date"),
						rs.getDouble("sale_price"),
						rs.getInt("quantity"),
						"", "", "", "",
						rs.getInt("store_id"),
						rs.getInt("employee_id"),
						rs.getInt("customer_id"),
						rs.getInt("item_id")
				);
				orders.add(order);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	// Query 8: Retrieve total stock quantity by product category
	public static List<String[]> getStockByCategory() {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT i.item_type, SUM(inv.quantity) as total_stock " +
				"FROM Items i JOIN Inventory inv ON i.item_id = inv.item_id " +
				"GROUP BY i.item_type";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String[] row = {rs.getString("item_type"), rs.getString("total_stock")};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 9: Retrieve highest paid employees
	public static List<Employee> getHighestPaidEmployees() {
		List<Employee> employees = new ArrayList<>();
		String query = "SELECT * FROM Employee ORDER BY employee_salary DESC";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Employee employee = new Employee(
						rs.getInt("employee_id"),
						rs.getString("employee_name"),
						rs.getDouble("employee_salary"),
						rs.getString("epmloyee_phone_number"),
						rs.getInt("store_id"),
						""  // storeName will be empty
				);
				employees.add(employee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}

	// Query 10: Retrieve order IDs, dates, and customer names for all orders
	public static List<String[]> getOrderDetails() {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT s.sale_id, s.sale_date, c.customer_name " +
				"FROM Sale s JOIN Customers c ON s.customer_id = c.customer_id " +
				"ORDER BY s.sale_date";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String[] row = {
						rs.getString("sale_id"),
						rs.getString("sale_date"),
						rs.getString("customer_name")
				};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 11: Retrieve available products with stock > 10
	public static List<Item> getAvailableProducts() {
		List<Item> items = new ArrayList<>();
		String query = "SELECT i.item_name, i.item_type, inv.quantity " +
				"FROM Items i JOIN Inventory inv ON i.item_id = inv.item_id " +
				"WHERE inv.quantity > 10 ORDER BY inv.quantity";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Item item = new Item(0, rs.getString("item_name"), 0,
						rs.getString("item_type"), 0, "", "");
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	// Query 12: Retrieve order details for specific order ID
	public static List<String[]> getOrderDetailsById(int orderId) {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT i.item_name, s.quantity, s.sale_price " +
				"FROM Sale s JOIN Items i ON s.item_id = i.item_id " +
				"WHERE s.sale_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, orderId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String[] row = {
						rs.getString("item_name"),
						rs.getString("quantity"),
						rs.getString("sale_price")
				};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 13: Retrieve total purchases by each customer
	public static List<String[]> getCustomerPurchaseCounts() {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT c.customer_name, COUNT(s.sale_id) as purchase_count " +
				"FROM Customers c LEFT JOIN Sale s ON c.customer_id = s.customer_id " +
				"GROUP BY c.customer_id ORDER BY purchase_count DESC";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String[] row = {
						rs.getString("customer_name"),
						rs.getString("purchase_count")
				};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 14: Retrieve total revenue by product category
	public static List<String[]> getRevenueByCategory() {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT i.item_type, SUM(s.sale_price) as total_revenue " +
				"FROM Items i JOIN Sale s ON i.item_id = s.item_id " +
				"GROUP BY i.item_type";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String[] row = {rs.getString("item_type"), rs.getString("total_revenue")};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 15: Retrieve payments made by credit card
	public static List<String[]> getCreditCardPayments() {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT p.payment_id, p.amount, p.payment_date " +
				"FROM Payments p WHERE p.payment_method = 'Credit Card' " +
				"ORDER BY p.payment_date";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String[] row = {
						rs.getString("payment_id"),
						rs.getString("amount"),
						rs.getString("payment_date")
				};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 16: Retrieve most frequently purchased product
	public static String[] getMostPopularProduct() {
		String[] result = new String[2];
		String query = "SELECT i.item_name, SUM(s.quantity) as total_quantity " +
				"FROM Items i JOIN Sale s ON i.item_id = s.item_id " +
				"GROUP BY i.item_id ORDER BY total_quantity DESC LIMIT 1";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			if (rs.next()) {
				result[0] = rs.getString("item_name");
				result[1] = rs.getString("total_quantity");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// Query 17: Retrieve all products sorted by price descending
	public static List<Item> getProductsByPriceDesc() {
		List<Item> items = new ArrayList<>();
		String query = "SELECT item_name, item_type, item_price FROM Items ORDER BY item_price DESC";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Item item = new Item(0, rs.getString("item_name"), rs.getDouble("item_price"),
						rs.getString("item_type"), 0, "", "");
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return items;
	}

	// Query 18: Retrieve transactions in specific month grouped by payment method
	public static List<String[]> getTransactionsByMonth(int month) {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT p.payment_method, COUNT(p.payment_id) as transaction_count, " +
				"SUM(p.amount) as total_amount " +
				"FROM Payments p WHERE MONTH(p.payment_date) = ? " +
				"GROUP BY p.payment_method";

		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, month);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String[] row = {
						rs.getString("payment_method"),
						rs.getString("transaction_count"),
						rs.getString("total_amount")
				};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 19: Retrieve top sales employees
	public static List<String[]> getTopSalesEmployees() {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT e.employee_name, SUM(s.sale_price) as total_sales " +
				"FROM Employee e JOIN Sale s ON e.employee_id = s.employee_id " +
				"GROUP BY e.employee_id ORDER BY total_sales DESC";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String[] row = {
						rs.getString("employee_name"),
						rs.getString("total_sales")
				};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	// Query 20: Retrieve total quantity of each product sold by category
	public static List<String[]> getSalesByCategory() {
		List<String[]> results = new ArrayList<>();
		String query = "SELECT i.item_type, SUM(s.quantity) as total_sold " +
				"FROM Items i JOIN Sale s ON i.item_id = s.item_id " +
				"GROUP BY i.item_type";

		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				String[] row = {rs.getString("item_type"), rs.getString("total_sold")};
				results.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}

	public static int addOrderAndGetId(int customerId, double totalPrice) {
		String sql = "INSERT INTO Sale (customer_id, sale_price, quantity, store_id, employee_id, item_id) VALUES (?, ?, 1, 1, 1, 1)";
		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, customerId);
			pstmt.setDouble(2, totalPrice);
			pstmt.executeUpdate();

			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1); // return sale_id
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// Add these methods to your DBConnect class

	public static boolean addPayment(int orderId, double amount, String paymentMethod, String status) throws SQLException {
		String query = "INSERT INTO Payments (order_id, amount, payment_method, payment_date, status) " +
				"VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)";

		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, orderId);
			pstmt.setDouble(2, amount);
			pstmt.setString(3, paymentMethod);
			pstmt.setString(4, status);

			return pstmt.executeUpdate() > 0;
		}
	}

	public static boolean addOrderItem(int orderId, int itemId, int quantity, double price) throws SQLException {
		String query = "INSERT INTO Order_Items (order_id, item_id, quantity, price) " +
				"VALUES (?, ?, ?, ?)";

		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, orderId);
			pstmt.setInt(2, itemId);
			pstmt.setInt(3, quantity);
			pstmt.setDouble(4, price);

			return pstmt.executeUpdate() > 0;
		}
	}

	public static int getLastSaleId() {
		String query = "SELECT MAX(sale_id) AS last_id FROM Sale";
		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			if (rs.next()) return rs.getInt("last_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean addPayment(int saleId, int customerId, double amount, String paymentMethod) {
		String sql = "INSERT INTO Payments (sale_id, customer_id, amount, payment_date, payment_method) VALUES (?, ?, ?, CURDATE(), ?)";
		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, saleId);
			pstmt.setInt(2, customerId);
			pstmt.setDouble(3, amount);
			pstmt.setString(4, paymentMethod);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


}
