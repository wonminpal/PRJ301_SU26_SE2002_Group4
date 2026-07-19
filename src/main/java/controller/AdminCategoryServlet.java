/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Category;

/**
 *
 * @author LENOVO
 */
public class AdminCategoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            request.getRequestDispatcher("/WEB-INF/views/admin/inventory-management.jsp").forward(request, response);
        }

        if (action.equals("add")) {

            CategoryDAO categoryDAO = new CategoryDAO();
            request.setAttribute("parentCategories", categoryDAO.getParentCategories());

            request.getRequestDispatcher("/WEB-INF/views/admin/category-add.jsp").forward(request, response);
        } else if (action.equals("edit")) {
            try {
                int id = 0;
                String idParam = request.getParameter("id");
                try {
                    if (idParam == null || idParam.trim().isEmpty()) {
                        throw new NumberFormatException("ID is empty");
                    }
                    id = Integer.parseInt(idParam.trim());
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=invalid_id");
                    return;
                }
                CategoryDAO categoryDAO = new CategoryDAO();
                Category category = categoryDAO.getCategoryById(id);

                if (category != null) {
                    request.setAttribute("category", category);
                    request.getRequestDispatcher("/WEB-INF/views/admin/category-edit.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=category_not_found");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=exception");
            }
        } else if (action.equals("delete")) {
            try {
                int id = 0;
                String idParam = request.getParameter("id");
                try {
                    if (idParam == null || idParam.trim().isEmpty()) {
                        throw new NumberFormatException("ID is empty");
                    }
                    id = Integer.parseInt(idParam.trim());
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=invalid_id");
                    return;
                }

                CategoryDAO categoryDAO = new CategoryDAO();

                if (categoryDAO.hasDependencies(id)) {
                    System.out.println("Có tham số");
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=category_in_use");
                    return;
                }

                boolean isDeleted = categoryDAO.softDeleteCategory(id);
                if (isDeleted) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&message=cat_delete_success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=cat_delete_fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=exception");
            }
        } else if (action.equals("restore")) {
            try {
                int id = 0;
                String idParam = request.getParameter("id");
                try {
                    if (idParam == null || idParam.trim().isEmpty()) {
                        throw new NumberFormatException("ID is empty");
                    }
                    id = Integer.parseInt(idParam.trim());
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=invalid_id");
                    return;
                }
                CategoryDAO categoryDAO = new CategoryDAO();

                boolean isRestored = categoryDAO.restoreCategory(id);
                if (isRestored) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&message=cat_restore_success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=cat_restore_fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=exception");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        CategoryDAO categoryDAO = new CategoryDAO();

        if (action.equals("add")) {
            try {
                String name = request.getParameter("name");
                CategoryDAO dao = new CategoryDAO();

                int parentId = 0;
                String parentIdStr = request.getParameter("parentId");
                if (parentIdStr != null && !parentIdStr.trim().isEmpty()) {
                    try {
                        parentId = Integer.parseInt(parentIdStr.trim());
                    } catch (NumberFormatException e) {
                        parentId = 0;
                    }
                }

                if (name == null || name.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/adminCategory?action=add&error=empty_name");
                    return;
                }

                if (dao.checkCategoryExist(name)) {
                    response.sendRedirect(request.getContextPath() + "/adminCategory?action=add&error=duplicate_name");
                    return;
                }

                String slug = name.toLowerCase().trim().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");

                boolean isSuccess = dao.addCategory(name, slug, parentId);

                if (isSuccess) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&message=category_add_success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/adminCategory?action=add&error=insert_fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/adminCategory?action=add&error=exception");
            }
        } else if (action.equals("edit")) {
            try {
                int id = 0;
                String idParam = request.getParameter("id");
                try {
                    if (idParam == null || idParam.trim().isEmpty()) {
                        throw new NumberFormatException("ID is empty");
                    }
                    id = Integer.parseInt(idParam.trim());
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&error=invalid_id");
                    return;
                }

                String name = request.getParameter("name");

                if (name == null || name.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/adminCategory?action=edit&id=" + id + "&error=empty_name");
                    return;
                }

                String slug = name.toLowerCase().trim().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");

                if (categoryDAO.checkCategoryExistForUpdate(id, name, slug)) {
                    response.sendRedirect(request.getContextPath() + "/adminCategory?action=edit&id=" + id + "&error=duplicate_name");
                    return;
                }

                categoryDAO = new CategoryDAO();

                boolean isSuccess = categoryDAO.updateCategory(id, name, slug);
                if (isSuccess) {
                    response.sendRedirect(request.getContextPath() + "/adminProduct?action=list&message=category_update_success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/adminCategory?action=edit&id=" + id + "&error=update_fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/adminCategory?action=edit&id=" + request.getParameter("id") + "&error=exception");
            }
        }
    }

}
