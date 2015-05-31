package de.dawanda.dawandaclient.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emanuele on 29.05.15.
 */
public class Category implements Parcelable {

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int id;
    public String name;
    public String image_url;

    public Category() {
    }

    private Category(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.image_url = in.readString();
    }


    @Override
    public int hashCode() {
        int result = 17 + 31 * id;
        if (name != null) {
            result = result + 31 * name.hashCode();
        }
        if (image_url != null) {
            result = result + 31 * image_url.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Category)) {
            return false;
        }
        return ((Category) o).id == id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.image_url);
    }
}
