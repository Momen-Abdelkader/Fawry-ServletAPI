package org.fawry.servlet.product.web;

import com.google.gson.Gson;
import org.fawry.servlet.product.model.Product;
import org.fawry.servlet.product.repository.ProductRepository;
import org.fawry.servlet.product.repository.SimpleProductRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("ERROR: INVALID ID FORMAT");
        }
    }
}
