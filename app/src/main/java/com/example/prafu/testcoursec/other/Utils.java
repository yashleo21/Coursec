package com.example.prafu.testcoursec.other;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Area51 on 27-Feb-17.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();

            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}