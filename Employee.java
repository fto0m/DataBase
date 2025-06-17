package application;

import javafx.beans.property.*;

public class Employee {
	private final IntegerProperty id = new SimpleIntegerProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final DoubleProperty salary = new SimpleDoubleProperty();
	private final StringProperty phone = new SimpleStringProperty();
	private final IntegerProperty storeId = new SimpleIntegerProperty();
	private final StringProperty storeName = new SimpleStringProperty();

	// Constructors
	public Employee() {
	}

	public Employee(int id, String name, double salary, String phone, int storeId, String storeName) {
		setId(id);
		setName(name);
		setSalary(salary);
		setPhone(phone);
		setStoreId(storeId);
		setStoreName(storeName);
	}

	// Property getters
	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public DoubleProperty salaryProperty() {
		return salary;
	}

	public StringProperty phoneProperty() {
		return phone;
	}

	public IntegerProperty storeIdProperty() {
		return storeId;
	}

	public StringProperty storeNameProperty() {
		return storeName;
	}

	// Regular getters and setters
	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public double getSalary() {
		return salary.get();
	}

	public void setSalary(double salary) {
		this.salary.set(salary);
	}

	public String getPhone() {
		return phone.get();
	}

	public void setPhone(String phone) {
		this.phone.set(phone);
	}

	public int getStoreId() {
		return storeId.get();
	}

	public void setStoreId(int storeId) {
		this.storeId.set(storeId);
	}

	public String getStoreName() {
		return storeName.get();
	}

	public void setStoreName(String storeName) {
		this.storeName.set(storeName);
	}

	@Override
	public String toString() {
		return getName();
	}
}