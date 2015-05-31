package de.dawanda.dawandaclient.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by emanuele on 30.05.15.
 */
public class Product implements Parcelable {

    public static class DefaultImage implements Parcelable {

        public static final Parcelable.Creator<DefaultImage> CREATOR = new Parcelable.Creator<DefaultImage>() {
            public DefaultImage createFromParcel(Parcel source) {
                return new DefaultImage(source);
            }

            public DefaultImage[] newArray(int size) {
                return new DefaultImage[size];
            }
        };

        public String big;
        public String thumb;
        public String long_;
        public String product_l;
        public String full;
        public String listview;
        public String listview_xs;
        public String listview_s;
        public String listview_m;
        public String listview_l;

        public DefaultImage() {
        }

        private DefaultImage(Parcel in) {
            this.big = in.readString();
            this.thumb = in.readString();
            this.long_ = in.readString();
            this.product_l = in.readString();
            this.full = in.readString();
            this.listview = in.readString();
            this.listview_xs = in.readString();
            this.listview_s = in.readString();
            this.listview_m = in.readString();
            this.listview_l = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.big);
            dest.writeString(this.thumb);
            dest.writeString(this.long_);
            dest.writeString(this.product_l);
            dest.writeString(this.full);
            dest.writeString(this.listview);
            dest.writeString(this.listview_xs);
            dest.writeString(this.listview_s);
            dest.writeString(this.listview_m);
            dest.writeString(this.listview_l);
        }
    }

    public static class Shop implements Parcelable {

        public static final Parcelable.Creator<Shop> CREATOR = new Parcelable.Creator<Shop>() {
            public Shop createFromParcel(Parcel source) {
                return new Shop(source);
            }

            public Shop[] newArray(int size) {
                return new Shop[size];
            }
        };

        public int id;
        public String title;
        public String subdomain;
        public String holiday_from;
        public String holiday_to;

        public Shop() {
        }

        private Shop(Parcel in) {
            this.id = in.readInt();
            this.title = in.readString();
            this.subdomain = in.readString();
            this.holiday_from = in.readString();
            this.holiday_to = in.readString();
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.title);
            dest.writeString(this.subdomain);
            dest.writeString(this.holiday_from);
            dest.writeString(this.holiday_to);
        }
    }


    public static class Seller implements Parcelable {

        public static final Parcelable.Creator<Seller> CREATOR = new Parcelable.Creator<Seller>() {
            public Seller createFromParcel(Parcel source) {
                return new Seller(source);
            }

            public Seller[] newArray(int size) {
                return new Seller[size];
            }
        };


        public int id;
        public String username;
        public int rating;
        public String platform;
        public String country;

        public Seller() {
        }

        private Seller(Parcel in) {
            this.id = in.readInt();
            this.username = in.readString();
            this.rating = in.readInt();
            this.platform = in.readString();
            this.country = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.username);
            dest.writeInt(this.rating);
            dest.writeString(this.platform);
            dest.writeString(this.country);
        }
    }

    public static class Price implements Parcelable {

        public static final Parcelable.Creator<Price> CREATOR = new Parcelable.Creator<Price>() {
            public Price createFromParcel(Parcel source) {
                return new Price(source);
            }

            public Price[] newArray(int size) {
                return new Price[size];
            }
        };

        public String currency;
        public String symbol;
        public int cents;

        public Price() {
        }

        private Price(Parcel in) {
            this.currency = in.readString();
            this.symbol = in.readString();
            this.cents = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.currency);
            dest.writeString(this.symbol);
            dest.writeInt(this.cents);
        }
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String id;
    public String slug;
    public String unit;
    public String title;
    public Price price;
    public Price undiscounted_price;
    public Price base_price;
    public String badge;
    public String sale_percentage;
    public boolean discounted;
    public boolean sold_in_unit;
    public DefaultImage default_image;
    public Seller seller;
    public Shop shop;

    public Product() {
    }

    private Product(Parcel in) {
        this.id = in.readString();
        this.slug = in.readString();
        this.unit = in.readString();
        this.title = in.readString();
        this.price = in.readParcelable(Price.class.getClassLoader());
        this.undiscounted_price = in.readParcelable(Price.class.getClassLoader());
        this.base_price = in.readParcelable(Price.class.getClassLoader());
        this.badge = in.readString();
        this.sale_percentage = in.readString();
        this.discounted = in.readByte() != 0;
        this.sold_in_unit = in.readByte() != 0;
        this.default_image = in.readParcelable(DefaultImage.class.getClassLoader());
        this.seller = in.readParcelable(Seller.class.getClassLoader());
        this.shop = in.readParcelable(Shop.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.slug);
        dest.writeString(this.unit);
        dest.writeString(this.title);
        dest.writeParcelable(this.price, 0);
        dest.writeParcelable(this.undiscounted_price, 0);
        dest.writeParcelable(this.base_price, 0);
        dest.writeString(this.badge);
        dest.writeString(this.sale_percentage);
        dest.writeByte(discounted ? (byte) 1 : (byte) 0);
        dest.writeByte(sold_in_unit ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.default_image, 0);
        dest.writeParcelable(this.seller, 0);
        dest.writeParcelable(this.shop, 0);
    }
}
