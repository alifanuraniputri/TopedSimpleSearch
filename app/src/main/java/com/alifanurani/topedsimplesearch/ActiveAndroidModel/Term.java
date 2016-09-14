package com.alifanurani.topedsimplesearch.ActiveAndroidModel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by USER on 9/14/2016.
 */
@Table(name = "Terms")
public class Term extends Model {

    @Column(name = "Name", unique = true, onUniqueConflict = Column.ConflictAction.ABORT)
    public String name;

    public static long getSize(String name) {
        return new Select()
                .from(Term.class)
                .where("Name = ?", name)
                .execute().size();
    }


}
