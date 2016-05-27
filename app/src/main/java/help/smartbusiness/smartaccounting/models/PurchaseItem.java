package help.smartbusiness.smartaccounting.models;

/**
 * Created by gamerboy on 26/5/16.
 */
public class PurchaseItem {
    private String name;
    private float quantity;
    private float rate;
    private float amount;

    public PurchaseItem(String name, float quantity, float rate, float amount) {
        this.name = name;
        this.quantity = quantity;
        this.rate = rate;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
