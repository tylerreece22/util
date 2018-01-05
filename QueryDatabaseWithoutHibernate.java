package com.kobie.qaautomation.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoreNumbers {
    //Put some object in list
    public static List<MerchantLocation> stores = null;
    public static ResultSet rs = null;
    public static Connection dbConnection = null;
    public static PreparedStatement preparedStatement = null;

    static {

        dbConnection = DatabaseConnection.getDBConnection(Settings.get("SOME-DB-CONNECTION-HERE"));

    }

    public static List<MerchantLocation> getStores() throws SQLException {
        try {
            preparedStatement = dbConnection
                    .prepareStatement("SOME QUERY HERE");
            rs = preparedStatement.executeQuery();
            stores = getStores();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<MerchantLocation> storeNumbers = new ArrayList<>();
        while (rs.next()) {
            storeNumbers.add(new BaseMerchantLocation(rs.getString(1), rs.getString(2), rs.getString(3)));
        }
        return storeNumbers;
    }
}
