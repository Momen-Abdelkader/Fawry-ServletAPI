package org.fawry.servlet.product.repository;

import org.fawry.servlet.product.model.Product;

import java.util.Collection;

public interface ProductRepository {
    public Collection<Product> getProducts();
    public Product getProduct(int id);
    public void addProduct(Product product);
    public void removeProduct(int id);
    public void updateProduct(Product product);
}
