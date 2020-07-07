/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabulous.FabulousManagementSystem.common;

/**
 *
 * @author O-O
 */
public class StringCommon {

    public static boolean IsNotNullandEmpty(String str) {
        if (str != null && !"".equals(str)) {
            return true;
        }
        return false;
    }

    public static boolean IsNullorEmpty(String str) {
        return !StringCommon.IsNotNullandEmpty(str);
    }

    public static String Encode(String str) throws Exception {
        EncryptionDecryption des = new EncryptionDecryption();
        return des.encrypt(str);
    }

    public static String Decode(String str) throws Exception {
        EncryptionDecryption des = new EncryptionDecryption();
        return des.decrypt(str);
    }
}
