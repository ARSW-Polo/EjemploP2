/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.test;

import edu.eci.arsw.exam.FachadaPersistenciaOfertas;
import edu.eci.arsw.exam.remote.ManejadorOfertasSkeleton;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hcadavid
 */
public class FachadaPersistenciaTest {
    
    public FachadaPersistenciaTest() {
    }
    
    @Test
    public void manejoOfertasTest(){
        ManejadorOfertasSkeleton mof=new ManejadorOfertasSkeleton();
        FachadaPersistenciaOfertas fop=new FachadaPersistenciaOfertas();
        mof.setFachadaPersistenciaOfertas(fop);
                
        mof.agregarOferta("2", "10", 4000);
        mof.agregarOferta("2", "20", 4000);
        mof.agregarOferta("3", "20", 4000);
        mof.agregarOferta("2", "30", 4000);
        mof.agregarOferta("3", "30", 4000);
        mof.agregarOferta("3", "30", 4000);

        mof.agregarOferta("X", "40", 5000);
        mof.agregarOferta("Y", "40", 4000);
        mof.agregarOferta("Z", "40", 3000);
        
        mof.agregarOferta("A", "50", 4000);
        mof.agregarOferta("B", "50", 5000);
        mof.agregarOferta("C", "50", 6000);

        
        Assert.assertEquals("No es consistente al registrar tres ofertas",new Integer(3),fop.getMapaOfertasRecibidas().get("30"));
        Assert.assertEquals("No es consistente al registrar dos ofertas",new Integer(2),fop.getMapaOfertasRecibidas().get("20"));
        Assert.assertEquals("No es consistente al registrar una oferta",new Integer(1),fop.getMapaOfertasRecibidas().get("10"));

        Assert.assertEquals("No se asigna consistentemente la oferta de menor precio",new Integer(3000),fop.getMapaMontosAsignados().get("40"));        
        Assert.assertEquals("No se asigna consistentemente el oferente de la oferta de menor precio","Z",fop.getMapaOferentesAsignados().get("40"));                
        
        Assert.assertEquals("No se asigna consistentemente la oferta de menor precio",new Integer(4000),fop.getMapaMontosAsignados().get("50"));                
        Assert.assertEquals("No se asigna consistentemente el oferente de la oferta de menor precio","A",fop.getMapaOferentesAsignados().get("50"));                
        
    }
}
