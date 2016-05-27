package help.smartbusiness.smartaccounting.models;

import android.content.Context;

/**
 * Created by gamerboy on 26/5/16.
 */
public class Customer {
    private String name;
    private String address;

    public Customer(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void insert(Context context) {

    }
}
