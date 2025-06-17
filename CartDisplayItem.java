package application;

public class CartDisplayItem {
	private String name;
	private double price;
	private int quantity;

	public CartDisplayItem(String name, double price, int quantity) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getItemId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
