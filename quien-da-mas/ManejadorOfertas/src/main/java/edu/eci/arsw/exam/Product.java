/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.exam;

import java.io.*;
import java.io.Serializable;

/**
 *
 * @author hcadavid
 * @author fchaves
 */
public class Product implements Serializable {
 
    private String code;
    private String description;
    private int startPrice;

    public Product(String code, String description,int startPrice) {
        this.code = code;
        this.description = description;
        this.startPrice  = startPrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public int setStartPrice() {
        return startPrice;
    }

    public void setStartPrice(int startPrice) {
        this.startPrice = startPrice;
    }
 
    public byte[] toBytes() throws IOException {
      byte []bytes; 
      ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
      ObjectOutputStream oos = new ObjectOutputStream(baos); 
      oos.writeObject(this); 
      oos.flush();
      oos.reset();
      bytes = baos.toByteArray();
      oos.close();
      baos.close();
      return bytes; 
    }
    
    public Product(byte[] body) throws IOException, ClassNotFoundException {
        Product obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream (body);
        ObjectInputStream ois = new ObjectInputStream (bis);
        obj = (Product)ois.readObject();
        ois.close();
        bis.close();
        this.code = obj.code;
        this.description = obj.description;
        this.startPrice = obj.startPrice;
    }
    
}
