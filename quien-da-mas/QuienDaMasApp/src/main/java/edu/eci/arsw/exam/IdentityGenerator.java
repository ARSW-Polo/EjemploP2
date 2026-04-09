/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.exam;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author hcadavid
 */
public class IdentityGenerator {

    public static String actualIdentity = generateIdentity();
    
    private static String generateIdentity() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
