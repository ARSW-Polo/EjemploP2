package edu.eci.arsw.exam.events;

import edu.eci.arsw.exam.IdentityGenerator;
import edu.eci.arsw.exam.Product;
import edu.eci.arsw.exam.WinnerNotification;
import edu.eci.arsw.exam.remote.ManejadorOfertasStub;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class OffertMessageListenerTest {

    private static class RecordingStub implements ManejadorOfertasStub {
        private String codOferente;
        private String codProducto;
        private int monto;
        private int invocations;

        @Override
        public void agregarOferta(String codOferente, String codProducto, int monto) {
            this.codOferente = codOferente;
            this.codProducto = codProducto;
            this.monto = monto;
            this.invocations++;
        }
    }

    @Test
    public void shouldRegisterOfferWhenProductAnnouncementArrives() throws Exception {
        OffertMessageListener listener = new OffertMessageListener();
        RecordingStub stub = new RecordingStub();
        listener.setManejadorOfertasStub(stub);

        Product product = new Product("PTEST", "producto", 5000);
        listener.onMessage(new Message(toBytes(product), new MessageProperties()));

        Assert.assertEquals("Se esperaba una sola invocacion al registrar oferta.", 1, stub.invocations);
        Assert.assertEquals("La identidad del comprador no coincide.", IdentityGenerator.actualIdentity, stub.codOferente);
        Assert.assertEquals("El codigo del producto no coincide.", "PTEST", stub.codProducto);
        Assert.assertTrue("El monto ofertado no cumple el precio base.", stub.monto >= 5000);
    }

    @Test
    public void shouldHandleWinnerNotificationWithoutPlacingOffer() throws Exception {
        OffertMessageListener listener = new OffertMessageListener();
        RecordingStub stub = new RecordingStub();
        listener.setManejadorOfertasStub(stub);

        WinnerNotification winner = new WinnerNotification("otroComprador", "PTEST");
        listener.onMessage(new Message(toBytes(winner), new MessageProperties()));

        Assert.assertEquals("No debe registrar ofertas al recibir una notificacion de ganador.", 0, stub.invocations);
    }

    private byte[] toBytes(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        byte[] bytes = baos.toByteArray();
        oos.close();
        baos.close();
        return bytes;
    }
}