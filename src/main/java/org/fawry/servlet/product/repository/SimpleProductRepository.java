package org.fawry.servlet.product.repository;

import org.fawry.servlet.product.model.Product;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleProductRepository implements ProductRepository {
    private static volatile ProductRepository instance;
    private final Map<Integer, Product> products = new HashMap<>();
    private static int newProductId;

    private SimpleProductRepository() {
        products.put(1, new Product(1, "Product 1", 10.0));
        products.put(2, new Product(2, "Product 2", 20.0));
        products.put(3, new Product(3, "Product 3", 30.0));
        products.put(4, new Product(4, "Product 4", 40.0));
        products.put(5, new Product(5, "Product 5", 50.0));
        products.put(6, new Product(6, "Product 6", 60.0));
        products.put(7, new Product(7, "Product 7", 70.0));
        products.put(8, new Product(8, "Product 8", 80.0));
        products.put(9, new Product(9, "Product 9", 90.0));
        products.put(10, new Product(10, "Product 10", 100.0));
        newProductId = 11;
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
    public Collection<Product> getAllProducts() {
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
