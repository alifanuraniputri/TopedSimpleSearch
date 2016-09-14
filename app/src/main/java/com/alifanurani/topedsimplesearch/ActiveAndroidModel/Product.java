package com.alifanurani.topedsimplesearch.ActiveAndroidModel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by USER on 9/14/2016.
 */
@Table(name = "Products")
public class Product extends Model {

    @Column(name = "Name")
    public String name;

    @Column(name = "Image_uri")
    public String image_uri;

    @Column(name = "Price")
    public String price;

    @Column(name = "Term")
    public String term;

    /*@Column(name = "IdLocal")
    public String idLocal;*/

    public static List<Product> get10(String term, int start) {
        return new Select()
                .from(Product.class)
                .where("Term = ?", term)
                .offset(start)
                .limit(10)
                .execute();
    }

    public static long getSize(String term) {
        return new Select()
                .from(Product.class)
                .where("Term = ?", term)
                .execute().size();
    }


}
