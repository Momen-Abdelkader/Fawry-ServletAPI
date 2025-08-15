package org.fawry.servlet.product.web;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.fawry.servlet.product.model.Product;
import org.fawry.servlet.product.repository.ProductRepository;
import org.fawry.servlet.product.repository.SimpleProductRepository;

import javax.servlet.ServletException;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        PrintWriter out = resp.getWriter();
        try {
            // /products/ ---> All products
            if (pathInfo == null || pathInfo.equals("/")) {
                out.write(gson.toJson(db.getAllProducts()));
                return;
            }

            // /products/{id} ---> Specific product
            int id = Integer.parseInt(pathInfo.substring(1));
            Product product = db.getProduct(id);

            if (product == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("ERROR: PRODUCT NOT FOUND");
                return;
            }

            out.write(gson.toJson(product));
        }
        catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("ERROR: INVALID ID FORMAT");
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("ERROR: INTERNAL SERVER ERROR");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            BufferedReader reader = req.getReader();
            Product product = gson.fromJson(reader, Product.class);
            if (product == null || product.getName() == null || product.getPrice() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("ERROR: INVALID PRODUCT");
                return;
            }

            db.addProduct(product);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Location", req.getContextPath() + req.getServletPath() + "/" + product.getId());
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
        catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("ERROR: INVALID REQUEST FORMAT");
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("ERROR: INTERNAL SERVER ERROR");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        PrintWriter out = resp.getWriter();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("ERROR: MISSING ID");
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            Product existingProduct = db.getProduct(id);

            if (existingProduct == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("ERROR: PRODUCT NOT FOUND");
                return;
            }

            BufferedReader reader = req.getReader();
            Product updatedProduct = gson.fromJson(reader, Product.class);
            if (updatedProduct == null || updatedProduct.getName() == null || updatedProduct.getPrice() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("ERROR: INVALID PRODUCT");
                return;
            }

            db.updateProduct(updatedProduct);
        }
        catch (NumberFormatException | JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("ERROR: INVALID REQUEST FORMAT");
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("ERROR: INTERNAL SERVER ERROR");
        }
    }
}
