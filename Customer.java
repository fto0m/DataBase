package application;

import javafx.beans.property.*;

public class Customer {
	private final IntegerProperty id = new SimpleIntegerProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty phone = new SimpleStringProperty();
	private final StringProperty city = new SimpleStringProperty();
	private final StringProperty street = new SimpleStringProperty();
	private final StringProperty streetNumber = new SimpleStringProperty();
	private final StringProperty streetName = new SimpleStringProperty();
	private final IntegerProperty points = new SimpleIntegerProperty();

	// Constructors
	public Customer() {
	}

	public Customer(String name, String phone, String city, String street, String streetNumber, String streetName,
			int points) {
		setName(name);
		setPhone(phone);
		setCity(city);
		setStreet(street);
		setStreetNumber(streetNumber);
		setStreetName(streetName);
		setPoints(points);
	}

	// Property getters
	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty phoneProperty() {
		return phone;
	}

	public StringProperty cityProperty() {
		return city;
	}

	public StringProperty streetProperty() {
		return street;
	}

	public StringProperty streetNumberProperty() {
		return streetNumber;
	}

	public StringProperty streetNameProperty() {
		return streetName;
	}

	public IntegerProperty pointsProperty() {
		return points;
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

	public String getPhone() {
		return phone.get();
	}

	public void setPhone(String phone) {
		this.phone.set(phone);
	}

	public String getCity() {
		return city.get();
	}

	public void setCity(String city) {
		this.city.set(city);
	}

	public String getStreet() {
		return street.get();
	}

	public void setStreet(String street) {
		this.street.set(street);
	}

	public String getStreetNumber() {
		return streetNumber.get();
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber.set(streetNumber);
	}

	public String getStreetName() {
		return streetName.get();
	}

	public void setStreetName(String streetName) {
		this.streetName.set(streetName);
	}

	public int getPoints() {
		return points.get();
	}

	public void setPoints(int points) {
		this.points.set(points);
	}

	@Override
	public String toString() {
		return getName();
	}
}