/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.exam;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        ApplicationContext ac=new ClassPathXmlApplicationContext("applicationContext.xml");
        final MainFrame mf=ac.getBean(MainFrame.class);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mf.setVisible(true);
            }
        });
        
        
    }
    
}
