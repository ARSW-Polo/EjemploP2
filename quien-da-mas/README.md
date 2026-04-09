# Quien-Da-Mas - Implementacion Punto 3 (Parcial ARSW)

## 1. Paso a paso para correr la solucion

### 1.1. Levantar RabbitMQ con Docker

Ejecutar en una terminal:

```bash
docker pull rabbitmq:3-management
docker run -d --name rabbitmq-par2ARSW -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

Verificar que el contenedor este arriba:

```bash
docker ps
```

Entrar a la consola de RabbitMQ:

- URL: http://localhost:15672
- Usuario: guest
- Password: guest

### 1.2. Compilar y probar los dos modulos

En terminal 1:

```bash
cd quien-da-mas/ManejadorOfertas
mvn test
```

En terminal 2:

```bash
cd quien-da-mas/QuienDaMasApp
mvn test
```

### 1.3. Ejecutar servidor y compradores

En terminal 1 (servidor con UI):

```bash
cd quien-da-mas/ManejadorOfertas
mvn exec:java
```

En terminal 2 (comprador 1):

```bash
cd quien-da-mas/QuienDaMasApp
mvn exec:java
```

En terminal 3 (comprador 2):

```bash
cd quien-da-mas/QuienDaMasApp
mvn exec:java
```

En terminal 4 (comprador 3):

```bash
cd quien-da-mas/QuienDaMasApp
mvn exec:java
```

### 1.4. Probar el flujo completo

1. En la ventana de ManejadorOfertas, crear un producto:
   - Codigo del producto
   - Descripcion
   - Precio inicial
2. Presionar Enviar.
3. Cada comprador recibe el producto y registra una oferta automatica via RMI.
4. Cuando llegan las primeras 3 ofertas validas:
   - El servidor cierra la subasta del producto.
   - El servidor muestra la oferta ganadora en la ventana "Propuestas aceptadas".
   - Se envia una notificacion de ganador por RabbitMQ.
5. El comprador ganador imprime:
   - El comprador XXXXX compro el producto YYYY

---

## 2. Como se uso RabbitMQ en la implementacion

Se uso RabbitMQ como bus de eventos con un exchange tipo topic llamado QDM-EXCHANGE.

### Eventos publicados

- Evento de producto nuevo: routing key auction.product
- Evento de ganador: routing key auction.winner

### Publicador (servidor)

El servidor publica ambos eventos usando Spring AMQP en QDM-EXCHANGE.

### Suscriptores (compradores)

Cada instancia del comprador crea su propia cola anonima (AnonymousQueue) y la enlaza con pattern auction.#.

Eso permite comportamiento pub/sub real: cada comprador recibe los eventos publicados.

---

## 3. Implementacion exacta por punto solicitado (Punto 3 del enunciado)

## 3.a) Los compradores registran su oferta al ser notificados de un nuevo producto

### Lo que hice

1. En el listener del comprador, al recibir un Product:
   - Se toma el startPrice del producto.
   - Se genera una oferta aleatoria mayor o igual al precio base.
   - Se llama el servicio remoto agregarOferta del servidor.
2. Se mantuvo el manejo de WinnerNotification para el punto 3.c.

### Archivos modificados

- QuienDaMasApp/src/main/java/edu/eci/arsw/exam/events/OffertMessageListener.java
- QuienDaMasApp/src/main/java/edu/eci/arsw/exam/Product.java

### Cambios clave

- Uso de getStartPrice() para el monto base.
- Llamada a manejadorOfertasStub.agregarOferta(...).

---

## 3.b) El servidor muestra la oferta ganadora al recibir las 3 primeras propuestas

### Lo que hice

1. Ajuste la logica en agregarOferta del servidor para:
   - Ignorar ofertas de productos inexistentes.
   - Ignorar ofertas por debajo del precio base.
   - Mantener como mejor oferta el mayor monto.
2. Al llegar a 3 ofertas validas:
   - Se cierra la subasta para ese producto.
   - Se muestra en la UI del servidor el ganador y monto.
   - Se dispara evento de ganador.
3. Se mantuvo el bloqueo por producto para evitar lock global.

### Archivos modificados

- ManejadorOfertas/src/main/java/edu/eci/arsw/exam/remote/ManejadorOfertasSkeleton.java
- ManejadorOfertas/src/main/java/edu/eci/arsw/exam/MainFrame.java
- ManejadorOfertas/src/main/java/edu/eci/arsw/exam/Product.java
- ManejadorOfertas/src/main/java/edu/eci/arsw/exam/events/OffertMessageProducer.java

### Cambios clave

- Seleccion de mejor oferta por mayor monto.
- Cierre de subasta al contador de 3 ofertas.
- Publicacion separada de eventos de producto y ganador.

---

## 3.c) Informar al comprador ganador que se le vendio el producto

### Lo que hice

1. Defini dos rutas de publicacion en el productor:
   - auction.product
   - auction.winner
2. Cuando el servidor determina ganador, publica WinnerNotification.
3. El comprador al recibir WinnerNotification:
   - Si buyerId coincide con su identidad local, imprime:
     - El comprador XXXXX compro el producto YYYY

### Archivos modificados

- ManejadorOfertas/src/main/java/edu/eci/arsw/exam/events/OffertMessageProducer.java
- ManejadorOfertas/src/main/java/edu/eci/arsw/exam/remote/ManejadorOfertasSkeleton.java
- QuienDaMasApp/src/main/java/edu/eci/arsw/exam/events/OffertMessageListener.java

---

## 4. Configuracion de RabbitMQ aplicada en codigo

### Servidor (ManejadorOfertas)

- Se declaro topic exchange QDM-EXCHANGE.
- Se uso rabbit template contra ese exchange.
- Se publico producto nuevo y notificacion de ganador con routing keys distintas.

Archivo:

- ManejadorOfertas/src/main/resources/applicationContext.xml

### Comprador (QuienDaMasApp)

- Se cambio a cola anonima por instancia: org.springframework.amqp.core.AnonymousQueue
- Se hizo bind con pattern auction.# para recibir ambos tipos de evento.

Archivo:

- QuienDaMasApp/src/main/resources/applicationContext.xml

---

## 5. Pruebas realizadas

## 5.1. Pruebas del servidor

Comando ejecutado:

```bash
cd quien-da-mas/ManejadorOfertas
mvn test
```

Resultado:

- BUILD SUCCESS
- 2 tests exitosos, 0 fallos

Archivo de pruebas:

- ManejadorOfertas/src/test/java/edu/eci/arsw/test/FachadaPersistenciaTest.java

Casos verificados:

1. Se escoge mayor monto entre las 3 primeras ofertas y se envia notificacion de ganador.
2. Se ignoran productos inexistentes y ofertas por debajo del precio base.

## 5.2. Pruebas del comprador

Comando ejecutado:

```bash
cd quien-da-mas/QuienDaMasApp
mvn test
```

Resultado:

- BUILD SUCCESS
- 2 tests exitosos, 0 fallos

Archivo de pruebas:

- QuienDaMasApp/src/test/java/edu/eci/arsw/exam/events/OffertMessageListenerTest.java

Casos verificados:

1. Al llegar Product, el comprador registra oferta automaticamente.
2. Al llegar WinnerNotification, no registra oferta adicional.

---

## 6. Resumen de archivos cambiados

### ManejadorOfertas

- src/main/java/edu/eci/arsw/exam/MainFrame.java
- src/main/java/edu/eci/arsw/exam/Product.java
- src/main/java/edu/eci/arsw/exam/events/OffertMessageProducer.java
- src/main/java/edu/eci/arsw/exam/remote/ManejadorOfertasSkeleton.java
- src/main/resources/applicationContext.xml
- src/test/java/edu/eci/arsw/test/FachadaPersistenciaTest.java

### QuienDaMasApp

- pom.xml
- src/main/java/edu/eci/arsw/exam/Product.java
- src/main/java/edu/eci/arsw/exam/events/OffertMessageListener.java
- src/main/resources/applicationContext.xml
- src/test/java/edu/eci/arsw/exam/events/OffertMessageListenerTest.java

---

## 7. Comandos usados durante la validacion

```bash
cd quien-da-mas/ManejadorOfertas
mvn test

cd ../QuienDaMasApp
mvn test
```

Con esto queda implementado y validado el punto 3 (a, b y c) solicitado en el parcial.
