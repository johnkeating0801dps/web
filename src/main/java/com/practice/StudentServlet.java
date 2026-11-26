package com.practice;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/student")
public class StudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String name = request.getParameter("name");
        if (name == null || name.isBlank()) {
            name = "Student";
        }
        response.getWriter().println("<h2>Welcome, " + escape(name) + "!</h2>");
        response.getWriter().println("<p>Your Maven web application is running.</p>");
        response.getWriter().println("<p><a href=\"index.jsp\">Back</a></p>");
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                    .replace(">", "&gt;").replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
}
