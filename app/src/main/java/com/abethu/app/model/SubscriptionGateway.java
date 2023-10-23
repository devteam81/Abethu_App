package com.abethu.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by codegama on 14/10/17.
 */

public class SubscriptionGateway implements Parcelable {

    private String country;
    private String payment_type;
    private int status;

    protected SubscriptionGateway(Parcel in) {
        country = in.readString();
        payment_type = in.readString();
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(payment_type);
        dest.writeInt(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubscriptionGateway> CREATOR = new Creator<SubscriptionGateway>() {
        @Override
        public SubscriptionGateway createFromParcel(Parcel in) {
            return new SubscriptionGateway(in);
        }

        @Override
        public SubscriptionGateway[] newArray(int size) {
            return new SubscriptionGateway[size];
        }
    };

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
