/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.exam;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author hcadavid
 */
public class FachadaPersistenciaOfertas {

    //mapa <codigo,producto>
    final private Map<String, Product> mapaProductosSolicitados = new ConcurrentHashMap<>();

    //mapa <codigo,codigo del cliente con la mejor oferta>
    final private Map<String, String> mapaOferentesAsignados = new ConcurrentHashMap<>();

    //mapa <codigo, monto de la mejor oferta>
    final private Map<String, Integer> mapaMontosAsignados = new ConcurrentHashMap<>();

    //mapa <codigo, numero de ofertas recibidas>
    final private Map<String, Integer> mapaOfertasRecibidas = new ConcurrentHashMap<>();

    //bloqueo por producto para evitar lock global
    final private Map<String, Object> productLocks = new ConcurrentHashMap<>();

    //conjunto de productos cuya subasta ya fue cerrada
    final private Set<String> closedProducts = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    public Map<String, Product> getMapaProductosSolicitados() {
        return mapaProductosSolicitados;
    }

    public Map<String, Integer> getMapaOfertasRecibidas() {
        return mapaOfertasRecibidas;
    }

    public Map<String, String> getMapaOferentesAsignados() {
        return mapaOferentesAsignados;
    }

    public Map<String, Integer> getMapaMontosAsignados() {
        return mapaMontosAsignados;
    }

    public Object getLockForProduct(String productCode) {
        return productLocks.computeIfAbsent(productCode, code -> new Object());
    }

    public boolean isProductClosed(String productCode) {
        return closedProducts.contains(productCode);
    }

    public void closeProduct(String productCode) {
        closedProducts.add(productCode);
    }

}
