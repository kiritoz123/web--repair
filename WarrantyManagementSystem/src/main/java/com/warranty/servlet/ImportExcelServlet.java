package com.warranty.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

/**
 * Servlet for handling Excel import
 */
@WebServlet("/admin/import-excel")
@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,      // 10 MB
    maxRequestSize = 10 * 1024 * 1024,   // 10 MB
    fileSizeThreshold = 1024 * 1024      // 1 MB
)
public class ImportExcelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is logged in and has ADMIN role
        String role = (String) request.getSession().getAttribute("role");
        if (role == null || !role.equals("ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Forward to import Excel page
        request.getRequestDispatcher("/views/admin/import-excel.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check authorization
        String role = (String) request.getSession().getAttribute("role");
        if (role == null || !role.equals("ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Get uploaded file
            Part filePart = request.getPart("excelFile");
            
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("error", "Vui lòng chọn file Excel!");
                request.getRequestDispatcher("/views/admin/import-excel.jsp").forward(request, response);
                return;
            }

            // Check file type
            String fileName = getFileName(filePart);
            if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
                request.setAttribute("error", "Chỉ chấp nhận file Excel (.xlsx, .xls)!");
                request.getRequestDispatcher("/views/admin/import-excel.jsp").forward(request, response);
                return;
            }

            // TODO: Process Excel file
            // 1. Read Excel using Apache POI
            // 2. Parse rows and columns
            // 3. Validate data
            // 4. Insert into database (customers, products, product_serials)
            
            // For now, just show success message
            request.setAttribute("success", "Upload thành công! File: " + fileName + 
                               ". Chức năng xử lý Excel đang được phát triển.");
            request.getRequestDispatcher("/views/admin/import-excel.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/import-excel.jsp").forward(request, response);
        }
    }

    /**
     * Extract file name from Part header
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }
}
