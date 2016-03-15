package com.example.wei.possessionmanager.database;

/**
 * Created by wei on 2016/2/28 0028.
 */
public class PossessionDatabase {

    public static final String NAME = "Possession.db";

    public static class ItemTable {

        public static final String NAME = "Items";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String DATE = "date";
            public static final String OWNER = "owner";
        }
    }
}
