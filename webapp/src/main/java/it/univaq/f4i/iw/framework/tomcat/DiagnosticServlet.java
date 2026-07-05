package it.univaq.f4i.iw.framework.tomcat;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class DiagnosticServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Diagnostic Information</title>");
            out.println("<style>");
            out.println("body { padding:0; margin:0; font-family: Verdana, Arial, Helvetica; font-size: 11pt; background: #73A2BD; }");
            out.println("h1 { font-size: 20pt;    font-weight: bold;    text-align: center;     color: white; }");
            out.println("h2 { color: white; margin-top: 30px; background-color: #53829D;   padding: 10px; }");
            out.println("table { border-collapse: collapse; width: 100%; margin: 10px 0; background: white; }");
            out.println("th, td { border: 1px solid #73A2BD; padding: 8px; text-align: left; }");
            out.println("th { background-color: ##73A2BD; color: white; }");
            out.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            out.println(".value { color: #0066cc; font-weight: bold; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1>Servlet Diagnostic Information</h1>");
            out.println("<p style='text-align:center'><strong><em>Remove this Servlet in Production!</em></strong></p>");

            printRequestInfo(out, request);
            printHeaders(out, request);
            printParameters(out, request);
            printRequestAttributes(out, request);
            printCookies(out, request);
            printSessionInfo(out, request);
            printServletContextInfo(out);
            printServerInfo(out, request);
            printSystemProperties(out);
            printEnvironmentVariables(out);

            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /////////////////////

    private void printRequestInfo(PrintWriter out, HttpServletRequest request) {
        printTable(out, "Request Information", "Property Name", "Property Value",
                Map.ofEntries(
                        makeMapEntry("Request Method", request.getMethod()),
                        makeMapEntry("Request URI", request.getRequestURI()),
                        makeMapEntry("Request URL", request.getRequestURL().toString()),
                        makeMapEntry("Context Path", request.getContextPath()),
                        makeMapEntry("Servlet Path", request.getServletPath()),
                        makeMapEntry("Path Info", request.getPathInfo()),
                        makeMapEntry("Path Translated", request.getPathTranslated()),
                        makeMapEntry("Query String", request.getQueryString()),
                        makeMapEntry("Protocol", request.getProtocol()),
                        makeMapEntry("Scheme", request.getScheme()),
                        makeMapEntry("Server Name", request.getServerName()),
                        makeMapEntry("Server Port", String.valueOf(request.getServerPort())),
                        makeMapEntry("Remote Address", request.getRemoteAddr()),
                        makeMapEntry("Remote Host", request.getRemoteHost()),
                        makeMapEntry("Remote Port", String.valueOf(request.getRemotePort())),
                        makeMapEntry("Remote User", request.getRemoteUser()),
                        makeMapEntry("Local Address", request.getLocalAddr()),
                        makeMapEntry("Local Name", request.getLocalName()),
                        makeMapEntry("Local Port", String.valueOf(request.getLocalPort())),
                        makeMapEntry("Character Encoding", request.getCharacterEncoding()),
                        makeMapEntry("Content Length", String.valueOf(request.getContentLength())),
                        makeMapEntry("Content Type", request.getContentType()),
                        makeMapEntry("Auth Type", request.getAuthType()),
                        makeMapEntry("Is Secure", String.valueOf(request.isSecure()))));

    }

    private void printHeaders(PrintWriter out, HttpServletRequest request) {
        if (request.getHeaderNames().hasMoreElements()) {
            Map<String, Object> headersMap
                    = Collections.list(request.getHeaderNames()).stream()
                            .collect(Collectors.toMap(
                                    name -> name,
                                    name -> Collections.list(request.getHeaders(name)).stream().collect(Collectors.joining(","))
                            ));
            printTable(out, "Request Headers", "Header Name", "Header Value", headersMap);
        }
    }

    private void printParameters(PrintWriter out, HttpServletRequest request) {
        if (request.getParameterNames().hasMoreElements()) {
            Map<String, Object> parametersMap
                    = Collections.list(request.getParameterNames()).stream()
                            .collect(Collectors.toMap(
                                    name -> name,
                                    name -> Arrays.stream(request.getParameterValues(name)).collect(Collectors.joining(","))
                            ));
            printTable(out, "Request Parameters", "Parameter Name", "Parameter Value", parametersMap);
        }
    }

    private void printRequestAttributes(PrintWriter out, HttpServletRequest request) {
        if (request.getAttributeNames().hasMoreElements()) {
            Map<String, Object> attributesMap
                    = Collections.list(request.getAttributeNames()).stream()
                            .collect(Collectors.toMap(
                                    name -> name,
                                    name -> request.getAttribute(name)
                            ));
            printTable(out, "Request Attributes", "Attribute Name", "Attribute Value", attributesMap);
        }
    }

    private void printCookies(PrintWriter out, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Map<String, String[]> cookiesMap
                    = Arrays.stream(cookies).collect(
                            Collectors.toMap(
                                    c -> c.getName(),
                                    c -> (new String[]{c.getValue(), c.getDomain(), c.getPath(), String.valueOf(c.getMaxAge()), c.getSecure() ? "true" : "false"})
                            ));
            printTable(out, "Cookies", "Cookie Name", new String[]{"Cookie Value", "Domain", "Path", "Max Age", "Secure"}, cookiesMap);
        }
    }

    private void printSessionInfo(PrintWriter out, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            printTable(out, "Session Information", "Property Name", "Property Value",
                    Map.ofEntries(
                            makeMapEntry("Session ID", session.getId()),
                            makeMapEntry("Creation Time", (new Date(session.getCreationTime()).toString())),
                            makeMapEntry("Last Accessed Time", (new Date(session.getLastAccessedTime()).toString())),
                            makeMapEntry("Max Inactive Interval", session.getMaxInactiveInterval() + " seconds"),
                            makeMapEntry("Is New", String.valueOf(session.isNew()))
                    ));
            Map<String, Object> attributesMap
                    = Collections.list(session.getAttributeNames()).stream()
                            .collect(Collectors.toMap(
                                    name -> name,
                                    name -> request.getAttribute(name)
                            ));

            printTable(out, "Session Attributes", "Attribute Name", "Attribute Value", attributesMap);
        }
    }

    private void printServletContextInfo(PrintWriter out) {
        ServletContext context = getServletContext();
        printTable(out, "Servlet Context Information", "Context Property Name", "Context Property Value",
                Map.ofEntries(
                        makeMapEntry("Context Path", context.getContextPath()),
                        makeMapEntry("Server Info", context.getServerInfo()),
                        makeMapEntry("Servlet Context Name", context.getServletContextName()),
                        makeMapEntry("Major Version", String.valueOf(context.getMajorVersion())),
                        makeMapEntry("Minor Version", String.valueOf(context.getMinorVersion())),
                        makeMapEntry("Effective Major Version", String.valueOf(context.getEffectiveMajorVersion())),
                        makeMapEntry("Effective Minor Version", String.valueOf(context.getEffectiveMinorVersion())),
                        makeMapEntry("Real Path (/)", context.getRealPath("/"))
                ));

        if (context.getInitParameterNames().hasMoreElements()) {
            Map<String, Object> initParamsMap
                    = Collections.list(context.getInitParameterNames()).stream()
                            .collect(Collectors.toMap(
                                    name -> name,
                                    name -> context.getInitParameter(name)
                            ));

            printTable(out, "Context Init Parameters", "Parameter Name", "Parameter Value", initParamsMap);
        }

        if (context.getAttributeNames().hasMoreElements()) {
            Map<String, Object> attributesMap
                    = Collections.list(context.getAttributeNames()).stream()
                            .collect(Collectors.toMap(
                                    name -> name,
                                    name -> context.getAttribute(name)
                            ));
            printTable(out, "Context Attributes", "Attributes Name", "Attributes Value", attributesMap);
        }
    }

    private void printServerInfo(PrintWriter out, HttpServletRequest request) {
        printTable(out, "Server Environment", "Property Name", "Property Value",
                Map.of("Servlet Name", getServletName(), "Servlet Info", getServletInfo()));
    }

    private void printSystemProperties(PrintWriter out) {
        printTable(out, "System Variables", "Property Name", "Property Value", System.getProperties());
    }

    private void printEnvironmentVariables(PrintWriter out) {
        printTable(out, "Environment Variables", "Variable Name", "Variable Value", System.getenv());
    }

    ////////////////
    
    private void printTable(PrintWriter out, String title, String name_header, String value_header, Map data) {
        printTable(out, title, name_header, new String[]{value_header}, data);
    }

    private void printTable(PrintWriter out, String title, String name_header, String[] value_headers, Map data) {
        out.println("<h2>" + title + "</h2>");
        out.println("<table>");
        printHeaderRow(out, name_header, value_headers);
        data.forEach((key, value) -> {
            printRow(out, key.toString(), value);
        });
        out.println("</table>");
    }

    private void printHeaderRow(PrintWriter out, String name_header, String... value_headers) {
        out.println("<tr>");
        out.println("<th>" + escapeHtml(name_header) + "</th>");
        for (String value_header : value_headers) {
            out.println("<th>" + escapeHtml(value_header) + "</th>");
        }
        out.println("</tr>");
    }

    private void printRow(PrintWriter out, String header, Object value) {
        out.println("<tr>");
        out.println("<td><strong>" + escapeHtml(header) + "</strong></td>");

        if (value instanceof Object[] values) {
            for (Object val : values) {
                out.println("<td class='value'>" + escapeHtml(val != null ? val.toString() : "null") + "</td>");
            }
        } else {
            out.println("<td class='value'>" + escapeHtml(value != null ? value.toString() : "null") + "</td>");
        }
        out.println("</tr>");
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "<i>null</i>";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private Map.Entry<String, Object> makeMapEntry(String key, Object value) {
        return Map.entry(key, value != null ? value : "null");
    }

    @Override
    public String getServletInfo() {
        return "Diagnostic Servlet";
    }
}
