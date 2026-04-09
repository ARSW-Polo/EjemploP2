/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.test;

import edu.eci.arsw.exam.FachadaPersistenciaOfertas;
import edu.eci.arsw.exam.Product;
import edu.eci.arsw.exam.WinnerNotification;
import edu.eci.arsw.exam.events.OffertMessageProducer;
import edu.eci.arsw.exam.remote.ManejadorOfertasSkeleton;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author hcadavid
 */
public class FachadaPersistenciaTest {
    
    public FachadaPersistenciaTest() {
    }

    private static class RecordingProducer extends OffertMessageProducer {

        private WinnerNotification winnerNotification;
        private int winnerNotificationCount;

        @Override
        public void sendWinnerNotification(Object message) {
            winnerNotificationCount++;
            winnerNotification = (WinnerNotification) message;
        }
    }
    
    @Test
    public void manejoOfertasDebeEscogerMayorMontoEntreLasTresPrimeras(){
        ManejadorOfertasSkeleton mof=new ManejadorOfertasSkeleton();
        FachadaPersistenciaOfertas fop=new FachadaPersistenciaOfertas();
        RecordingProducer producer = new RecordingProducer();

        mof.setFachadaPersistenciaOfertas(fop);
        mof.setMessageProducer(producer);

        fop.getMapaProductosSolicitados().put("P1", new Product("P1", "Producto de prueba", 1000));

        mof.agregarOferta("A", "P1", 1200);
        mof.agregarOferta("B", "P1", 1800);
        mof.agregarOferta("C", "P1", 1500);

        // No debe contar porque la subasta se cierra al recibir la tercera oferta.
        mof.agregarOferta("D", "P1", 2500);

        Assert.assertEquals("No es consistente al registrar solo las tres primeras ofertas.", Integer.valueOf(3), fop.getMapaOfertasRecibidas().get("P1"));
        Assert.assertEquals("No se asigna consistentemente la mejor oferta (mayor monto).", Integer.valueOf(1800), fop.getMapaMontosAsignados().get("P1"));
        Assert.assertEquals("No se asigna consistentemente el oferente ganador.", "B", fop.getMapaOferentesAsignados().get("P1"));

        Assert.assertEquals("Se esperaba una sola notificación de ganador.", 1, producer.winnerNotificationCount);
        Assert.assertEquals("El comprador notificado no coincide con el ganador.", "B", producer.winnerNotification.getBuyerId());
        Assert.assertEquals("El producto notificado no coincide.", "P1", producer.winnerNotification.getProductCode());
    }

    @Test
    public void manejoOfertasDebeIgnorarProductosInexistentesYMontosBajoPrecioBase(){
        ManejadorOfertasSkeleton mof=new ManejadorOfertasSkeleton();
        FachadaPersistenciaOfertas fop=new FachadaPersistenciaOfertas();

        mof.setFachadaPersistenciaOfertas(fop);

        fop.getMapaProductosSolicitados().put("P2", new Product("P2", "Otro producto", 2000));

        // Producto inexistente.
        mof.agregarOferta("A", "NO_EXISTE", 5000);
        // Monto inferior al precio base.
        mof.agregarOferta("A", "P2", 1500);
        // Oferta valida.
        mof.agregarOferta("A", "P2", 2500);

        Assert.assertNull("No se debe crear subasta para productos inexistentes.", fop.getMapaOfertasRecibidas().get("NO_EXISTE"));
        Assert.assertEquals("Solo la oferta valida debe ser registrada.", Integer.valueOf(1), fop.getMapaOfertasRecibidas().get("P2"));
        Assert.assertEquals("El mejor monto debe ser el de la oferta valida.", Integer.valueOf(2500), fop.getMapaMontosAsignados().get("P2"));
        Assert.assertEquals("El oferente ganador provisional debe coincidir.", "A", fop.getMapaOferentesAsignados().get("P2"));
    }
}
