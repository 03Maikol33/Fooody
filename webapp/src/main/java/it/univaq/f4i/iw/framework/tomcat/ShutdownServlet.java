package it.univaq.f4i.iw.framework.tomcat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author giuse
 */
public class ShutdownServlet extends HttpServlet {

    private void action_ask(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Web Application Shutdown</title>");
            out.println("<style>");
            out.println("body { text-align:center; padding:0; margin:0; font-family: Verdana, Arial, Helvetica; font-size: 11pt; background: #73A2BD; }");
            out.println(".container { background: white; padding: 40px; border-radius: 10px; max-width: 500px; margin: 0 auto; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
            out.println(".warning { color: #53829D; font-size: 24px; margin: 20px; }");
            out.println("button { padding: 15px 30px; font-size: 16px; margin: 10px; cursor: pointer; border: none; border-radius: 5px; }");
            out.println(".shutdown { background: #53829D; color: white; }");
            out.println(".shutdown:hover { background: #73A2BD; }");
            out.println(".cancel { background: #666; color: white; }");
            out.println(".cancel:hover { background: #444; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>Web Application Shutdown</h1>");
            out.println("<p class='warning'>Are you sure?</p>");
            out.println("<p>This will shut down Tomcat and clear temporary files.</p>");
            out.println("<form method='GET'><input type='hidden' name='shutdown' value='true'/>");
            out.println("<button type='submit' class='shutdown'>Shutdown</button>");
            out.println("<button type='button' class='cancel' onclick='window.location=\"" + request.getContextPath() + "\"'>Cancel</button>");
            out.println("</form>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void action_shutdown(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Web Application Shutdown</title>");
            out.println("<meta http-equiv='refresh' content='3;url=about:blank'>");
            out.println("<style>");
            out.println("body { text-align:center; padding:0; margin:0; font-family: Verdana, Arial, Helvetica; font-size: 11pt; background: #73A2BD; }");
            out.println(".container { background: white; padding: 40px; border-radius: 10px; max-width: 500px; margin: 0 auto; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
            out.println(".spinner { border: 4px solid #f3f3f3; border-top: 4px solid #73A2BD; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite; margin: 20px auto; }");
            out.println("@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<h1>Shutting down...</h1>");
            out.println("<div class='spinner'></div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            out.flush();
        }

        new Thread(() -> {
            try {
                System.out.println("================================================");
                System.out.println("Shutdown request (from servlet)");
                System.out.println("================================================");                
                Thread.sleep(1000);
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "shutdown-thread").start();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("shutdown") != null && request.getParameter("shutdown").equals("true")) {
            action_shutdown(request, response);
        } else {
            action_ask(request, response);
        }
    }
}
