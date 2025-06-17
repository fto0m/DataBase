package application;

public class CartItem {
	private int customerId;
	private int itemId;
	private int quantity;

	public CartItem(int customerId, int itemId, int quantity) {
		this.customerId = customerId;
		this.itemId = itemId;
		this.quantity = quantity;
	}

	// Getters & Setters
	public int getCustomerId() {
		return customerId;
	}

	public int getItemId() {
		return itemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
