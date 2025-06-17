package application;

public class User {
	private int userId;
	private String username;
	private String password; // Note: In production, never store plain text passwords
	private String role; // "employee" or "customer"
	private int referenceId; // Links to employee_id or customer_id

	public User(int userId, String username, String password, String role, int referenceId) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.role = role;
		this.referenceId = referenceId;
	}

	// Getters and setters
	public int getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getRole() {
		return role;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}
}