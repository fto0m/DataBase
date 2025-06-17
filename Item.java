package application;

public class Item {
	private int id;
	private String name;
	private double price;
	private String type;
	private int publisherId;
	private String publisherPhone;
	private String storeName;

	// Constructors, getters, setters
	public Item(int id, String name, double price, String type, int publisherId, String publisherPhone,
			String storeName) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.type = type;
		this.publisherId = publisherId;
		this.publisherPhone = publisherPhone;
		this.storeName = storeName;
	}

	// Getters and setters for all fields
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(int publisherId) {
		this.publisherId = publisherId;
	}

	public String getPublisherPhone() {
		return publisherPhone;
	}

	public void setPublisherPhone(String publisherPhone) {
		this.publisherPhone = publisherPhone;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
}
