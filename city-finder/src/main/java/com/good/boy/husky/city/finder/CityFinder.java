package com.good.boy.husky.city.finder;

import com.good.boy.husky.database.configuration.DatabaseConfiguration;

public class CityFinder {

    public static void main(String[] args) {
        System.out.println(DatabaseConfiguration.getUser());
        System.out.println(DatabaseConfiguration.getPassword());
        System.out.println(DatabaseConfiguration.getUrl());
    }

}
