package org.fawry.servlet.product.web;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.fawry.servlet.product.model.Product;
import org.fawry.servlet.product.repository.ProductRepository;
import org.fawry.servlet.product.repository.SimpleProductRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {
    private final ProductRepository db = SimpleProductRepository.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try (PrintWriter out = resp.getWriter()) {
            Integer id = extractId(req);
            // /products/ ---> All products
            if (id == null) {
                out.write(gson.toJson(db.getAllProducts()));
                return;
            }

            // /products/{id} ---> Specific product
            Product product = db.getProduct(id);
            if (product == null) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "ERROR: PRODUCT NOT FOUND");
                return;
            }

            out.write(gson.toJson(product));
        }
        catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ERROR: INVALID ID FORMAT");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);
        try {
            Product product = parseProduct(req);
            if (!isValidProduct(product)) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ERROR: INVALID PRODUCT");
                return;
            }

            db.addProduct(product);
            resp.setHeader("Location", req.getContextPath() + req.getServletPath() + "/" + product.getId());
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
        catch (JsonSyntaxException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ERROR: INVALID JSON FORMAT");
        }
        catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR: INTERNAL SERVER ERROR");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);

        Product existingProduct = findExistingProduct(req, resp);
        if (existingProduct == null) {
            return;
        } // Error already sent

        try {
            Product updatedProduct = parseProduct(req);
            if (!isValidProduct(updatedProduct)) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ERROR: INVALID PRODUCT");
                return;
            }

            updatedProduct.setId(existingProduct.getId());
            db.updateProduct(updatedProduct);
        }
        catch (JsonSyntaxException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ERROR: INVALID JSON FORMAT");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        prepareJsonResponse(resp);

        Product existingProduct = findExistingProduct(req, resp);
        if (existingProduct == null) {
            return;
        } // Error already sent

        db.removeProduct(existingProduct.getId());
    }

    private void prepareJsonResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(message));
    }

    private Integer extractId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }

        return Integer.parseInt(pathInfo.substring(1));
    }

    private Product parseProduct(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = req.getReader()) {
            return gson.fromJson(reader, Product.class);
        }
    }

    private boolean isValidProduct(Product product) {
        return product != null && product.getName() != null && product.getPrice() > 0;
    }

    private Product findExistingProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id;
        try {
            id = extractId(req);
        }
        catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ERROR: INVALID ID FORMAT");
            return null;
        }

        if (id == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ERROR: MISSING PRODUCT ID");
            return null;
        }

        Product existingProduct = db.getProduct(id);
        if (existingProduct == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "ERROR: PRODUCT NOT FOUND");
            return null;
        }

        return existingProduct;
    }
}
