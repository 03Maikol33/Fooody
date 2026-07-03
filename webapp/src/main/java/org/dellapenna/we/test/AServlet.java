package org.dellapenna.we.test;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author GDP
 */
public class AServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet " + getServletName() + "</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>This is servlet <em>" + getServletName() + "</em> (implemented in class <em>" + getClass().getSimpleName() + "</em>)" + "</h1>");
            if (getServletInfo() != null && !getServletInfo().isBlank()) {
                out.println("<p><em>" + getServletInfo() + "</em></p>");
            }

            out.println("<h2>Request</h2>");
            out.println("<ul>");
            out.println("<li><strong>Request URL</strong>: " + request.getRequestURL() + "</li>");
            out.println("<li><strong>Request Context</strong>: " + request.getContextPath() + "</li>");
            out.println("<li><strong>Request Method</strong>: " + request.getMethod() + "</li>");
            out.println("<li><strong>Request Protocol</strong>: " + request.getProtocol() + "</li>");
            out.println("<li><strong>Request Timestamp</strong>: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "</li>");
            out.println("<li><strong>Request Remote Address</strong>: " + request.getRemoteAddr() + "</li>");
            out.println("</ul>");

            out.println("<h2>Server</h2>");
            out.println("<ul>");
            out.println("<li><strong>Server Version</strong>: " + getServletContext().getServerInfo() + "</li>");
            out.println("<li><strong>Server Name</strong>: " + request.getServerName() + "</li>");
            out.println("<li><strong>Server Port</strong>: " + request.getServerPort() + "</li>");
            out.println("<li><strong>Context Filesystem Path</strong>: " + getServletContext().getRealPath("") + "</li>");
            out.println("<li><strong>Servlet Version</strong>: " + getServletContext().getMajorVersion() + "." + getServletContext().getMinorVersion() + "</li>");
            out.println("<li><strong>Web Application Version</strong>: " + getServletContext().getEffectiveMajorVersion() + "." + getServletContext().getEffectiveMinorVersion() + "</li>");

            out.println("</ul>");
            out.println("<hr/>");
            
            out.println("<p>To analyze the web application context <a href=\"diagnostic\">open the diagnostic servlet</a>.</p>");
            out.println("<p>To stop this application and clear the temporary files, either hit CTRL-C if started from the command line or use the <a href=\"shutdown\"> embedded shutdown servlet </a>.</p>");
            out.println("<p><strong>Remove these servlets in production.</strong></p>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "This is a short servlet description";
    }

}
