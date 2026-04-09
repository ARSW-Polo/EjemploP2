/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.exam.remote;

import edu.eci.arsw.exam.FachadaPersistenciaOfertas;
import edu.eci.arsw.exam.MainFrame;
import edu.eci.arsw.exam.Product;
import edu.eci.arsw.exam.WinnerNotification;
import edu.eci.arsw.exam.events.OffertMessageProducer;
import org.springframework.amqp.AmqpException;

/**
 *
 * @author hcadavid
 */
public class ManejadorOfertasSkeleton implements ManejadorOfertasStub{

    private FachadaPersistenciaOfertas fpers=null;
    private OffertMessageProducer messageProducer;
    private MainFrame mainFrame;

    public void setFachadaPersistenciaOfertas(FachadaPersistenciaOfertas fpers) {
        this.fpers = fpers;
    }

    public void setMessageProducer(OffertMessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
            
    @Override
    public void agregarOferta(String codOferente,String codprod,int monto) {
        Object lock = fpers.getLockForProduct(codprod);
        synchronized (lock) {
            if (fpers.isProductClosed(codprod)) {
                return;
            }

            Product product = fpers.getMapaProductosSolicitados().get(codprod);
            if (product == null) {
                return;
            }

            if (monto < product.getStartPrice()) {
                return;
            }

            if (!fpers.getMapaOfertasRecibidas().containsKey(codprod)){
                //se ha recibido la primera oferta 
                fpers.getMapaOfertasRecibidas().put(codprod, 1);
                //se asigna el monto propuesto como mejor oferta
                fpers.getMapaMontosAsignados().put(codprod, monto);
                //se asigna al oferente como ganador provisional
                fpers.getMapaOferentesAsignados().put(codprod, codOferente);
            }
            else{
                int ofertasActuales=fpers.getMapaOfertasRecibidas().get(codprod);
                fpers.getMapaOfertasRecibidas().put(codprod,ofertasActuales+1);
                if (fpers.getMapaMontosAsignados().get(codprod) < monto){
                    fpers.getMapaMontosAsignados().put(codprod, monto);
                    fpers.getMapaOferentesAsignados().put(codprod, codOferente);
                }
            }

            int ofertasRecibidas = fpers.getMapaOfertasRecibidas().get(codprod);
            if (ofertasRecibidas >= 3) {
                fpers.closeProduct(codprod);
                String winnerCode = fpers.getMapaOferentesAsignados().get(codprod);
                int winningAmount = fpers.getMapaMontosAsignados().get(codprod);

                if (mainFrame != null) {
                    mainFrame.showAcceptedOffer(codprod, winnerCode, winningAmount);
                }

                if (messageProducer != null) {
                    try {
                        messageProducer.sendWinnerNotification(new WinnerNotification(winnerCode, codprod));
                    } catch (AmqpException ex) {
                        throw new RuntimeException("No fue posible notificar ganador para " + codprod, ex);
                    }
                }
            }
        }
    }
    
    
    
}
