package org.fawry.servlet.product.repository;

import org.fawry.servlet.product.model.Product;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleProductRepository implements ProductRepository {
    private static volatile ProductRepository instance;
    private final Map<Integer, Product> products = new HashMap<>();
    private static int newProductId = 1;

    private SimpleProductRepository() {
    }

    public static ProductRepository getInstance() {
        if (instance == null) {
            synchronized (SimpleProductRepository.class) {
                if (instance == null) {
                    instance = new SimpleProductRepository();
                }
            }
        }

        return instance;
    }

    @Override
    public Collection<Product> getProducts() {
        return products.values();
    }

    @Override
    public Product getProduct(int id) {
        return products.get(id);
    }

    @Override
    public void addProduct(Product product) {
        product.setId(newProductId++);
        products.put(product.getId(), product);
    }

    @Override
    public void removeProduct(int id) {
        products.remove(id);
    }

    @Override
    public void updateProduct(Product product) {
        products.put(product.getId(), product);
    }
}
