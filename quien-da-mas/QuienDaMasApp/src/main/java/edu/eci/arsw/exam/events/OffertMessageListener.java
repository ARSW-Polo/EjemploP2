package edu.eci.arsw.exam.events;

import edu.eci.arsw.exam.IdentityGenerator;
import edu.eci.arsw.exam.Product;
import edu.eci.arsw.exam.WinnerNotification;
import edu.eci.arsw.exam.remote.ManejadorOfertasStub;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class OffertMessageListener implements MessageListener {

    private final Random rand = new Random(System.currentTimeMillis());
    private ManejadorOfertasStub manejadorOfertasStub;

    public OffertMessageListener() {
        super();
        System.out.println("Comprador #" + IdentityGenerator.actualIdentity + " esperando eventos...");
    }

    public void setManejadorOfertasStub(ManejadorOfertasStub manejadorOfertasStub) {
        this.manejadorOfertasStub = manejadorOfertasStub;
    }

    @Override
    public void onMessage(Message message) {
        try {
            Object receivedObject = deserializeObject(message.getBody());
            if (receivedObject instanceof Product) {
                Product receivedProduct = (Product) receivedObject;
                System.out.println("Comprador #" + IdentityGenerator.actualIdentity + " recibió: " + receivedProduct.getCode());

                int montoOferta = receivedProduct.setStartPrice() + rand.nextInt(1000000);
                manejadorOfertasStub.agregarOferta(IdentityGenerator.actualIdentity, receivedProduct.getCode(), montoOferta);
                System.out.println("Comprador #" + IdentityGenerator.actualIdentity + " ofertó: " + montoOferta + " por " + receivedProduct.getCode());
            } else if (receivedObject instanceof WinnerNotification) {
                WinnerNotification winnerNotification = (WinnerNotification) receivedObject;
                if (IdentityGenerator.actualIdentity.equals(winnerNotification.getBuyerId())) {
                    System.out.println("El comprador " + winnerNotification.getBuyerId() + " compro el producto " + winnerNotification.getProductCode());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("An exception occured while trying to get a AMQP object:" + e.getMessage(), e);
        }

    }

    private Object deserializeObject(byte[] body) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(body);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        bis.close();
        return obj;
    }

}
