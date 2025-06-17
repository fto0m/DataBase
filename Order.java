package application;

import java.util.Date;

public class Order {
	private int id;
	private Date date;
	private double price;
	private int quantity;
	private String storeName;
	private String employeeName;
	private String customerName;
	private String itemName;
	private int storeId;
	private int employeeId;
	private int customerId;
	private int itemId;

	public Order(int id, Date date, double price, int quantity, String storeName, String employeeName,
			String customerName, String itemName, int storeId, int employeeId, int customerId, int itemId) {
		this.id = id;
		this.date = date;
		this.price = price;
		this.quantity = quantity;
		this.storeName = storeName;
		this.employeeName = employeeName;
		this.customerName = customerName;
		this.itemName = itemName;
		this.storeId = storeId;
		this.employeeId = employeeId;
		this.customerId = customerId;
		this.itemId = itemId;
	}

	// Getters and setters for all fields
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
}