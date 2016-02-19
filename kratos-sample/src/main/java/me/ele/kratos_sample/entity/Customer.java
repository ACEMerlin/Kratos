package me.ele.kratos_sample.entity;

import android.os.Parcel;
import android.os.Parcelable;

import kratos.internal.KString;

/**
 * Created by merlin on 16/2/18.
 */
public class Customer implements Parcelable {

    public KString name = new KString();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.name, flags);
    }

    public Customer() {
    }

    protected Customer(Parcel in) {
        this.name = in.readParcelable(KString.class.getClassLoader());
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        public Customer createFromParcel(Parcel source) {
            return new Customer(source);
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}
