package com.zeus.android.mydeputy.app.util;

import java.math.BigInteger;

/**
 * Created by admin on 2/10/15.
 */
public class HashGenerator {

    private static String hAr[] = {
            "24dba5fb0a",
            "2642992841",
            "7523954739",
            "d3ec2e6f20",
            "544305df1b",
            "b9cfaf5163",
            "8477aaef22",
            "85d343dfee",
            "c23f126a3b"
    };

    private static String toHex(String in)
    {
        byte[] array = in.getBytes();
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

    public static String hash(String pswd) {

        int i = Character.getNumericValue(pswd.charAt(0));
        i &= 0x07;

        return hAr[i] + toHex(pswd) + Integer.toHexString(pswd.hashCode());
    }
}
