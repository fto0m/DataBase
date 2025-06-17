package application;

public class Publisher {

    private int id;
    private String phone;
    private int storeId;
    private String storeName;

    public Publisher(int id, String phone, int storeId, String storeName) {
        this.id = id;
        this.phone = phone;
        this.storeId = storeId;
        this.storeName = storeName;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}
