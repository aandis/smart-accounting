package help.smartbusiness.smartaccounting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gamerboy on 26/5/16.
 */
public class Purchase {
    private Customer customer;
    private String date;
    private String remarks;
    private float amount;
    private List<PurchaseItem> purchaseItems;

    public Purchase(Customer customer, String date, String remarks, float amount) {
        this.customer = customer;
        this.date = date;
        this.remarks = remarks;
        this.amount = amount;
        purchaseItems = new ArrayList<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseItem> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }

    public boolean isValid() {
        // Not empty validations.
        List<String> notEmpty = new ArrayList<>(Arrays.asList(
                customer.getName(), customer.getAddress(), date));

        for (int i = 0; i < purchaseItems.size(); i++) {
            notEmpty.add(purchaseItems.get(i).getName());
        }

        for (String text : notEmpty) {
            if (text.isEmpty()) {
                return false;
            }
        }

        // Lazy float value validations.
        // Only validate totals. If they are correct, rate and quantity *should* be correct.
        for (int i = 0; i < purchaseItems.size(); i++) {
            if (purchaseItems.get(i).getAmount() < 0) {
                return false;
            }
        }
        return true;
    }

}
