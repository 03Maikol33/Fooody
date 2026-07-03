package it.univaq.f4i.iw.framework.tomcat;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.LogManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author giuse
 */
public class Runner {

    private static Tomcat tomcat = null; // Directory temporanea per Tomcat
    private static File tempWebappDir = null; // Directory temporanea per la webapp
    private static String tomcatBaseDir = null;
    private static File persistentDataDir = null;

    public static void main(String[] args) throws Exception {

        System.out.println("================================================");
        System.out.println("Application configuration...");
        System.out.println("================================================");

        registerShutdownHook(); //per chiudere l'applicazione cancellando i file temporanei
        configureLogging(); //per abilitare il logging su console e nella directory ./logs
        initializeDataDirectory(); //per creare e configurare una directory di salvataggio persistente, se necessaria

        boolean openBrowser = Boolean.parseBoolean(System.getProperty("open.browser", "true"));
        String port = System.getProperty("server.port", "8081"); //la porta 8080 è occupata dal server per l'api restful
        tomcatBaseDir = System.getProperty("java.io.tmpdir") + File.separator + "tomcat-" + port;
        System.out.println("* Tomcat base folder: " + tomcatBaseDir);

        tomcat = new Tomcat();
        tomcat.setPort(Integer.parseInt(port));
        tomcat.setBaseDir(tomcatBaseDir);
        tomcat.getConnector();
        tomcat.enableNaming();  // Abilita JNDI naming per DataSource ecc.

        // Determina se siamo in IDE o Standalone
        boolean runningFromJar = isRunningFromJar();
        System.out.println("* Execution mode: " + (runningFromJar ? "JAR" : "IDE"));

        String webappPath;
        if (runningFromJar) {
            webappPath = extractWebappFromJar();
        } else {
            webappPath = new File("src/main/webapp").getAbsolutePath();
        }
        System.out.println("* Webapp path: " + webappPath);

        File contextFile = new File(webappPath, "META-INF/context.xml");
        String contextPath = parseContextPath(contextFile);
        System.out.println("* Context path from context.xml: '" + contextPath + "'");
        Context context = tomcat.addWebapp(contextPath, webappPath);
        if (contextFile.exists()) {
            System.out.println("* Loading context.xml from " + contextFile.getAbsolutePath());
            context.setConfigFile(contextFile.toURI().toURL());
        }

        if (persistentDataDir != null) {
            context.getServletContext().setAttribute("dataDirectory", persistentDataDir.getAbsolutePath());
        }

        // Configura le risorse per le classi compilate
        if (!runningFromJar) {
            File classesDir = new File("target/classes");
            if (classesDir.exists()) {
                WebResourceRoot resources = new StandardRoot(context);
                resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", classesDir.getAbsolutePath(), "/"));
                context.setResources(resources);
                System.out.println("* Compiled classes folder: " + classesDir.getAbsolutePath());
            }
        } else {
            // Da JAR: le classi sono già nel classpath
            System.out.println("* Compiled classes in classpath");
        }

        String url = "http://localhost:" + port + context.getPath();

        System.out.println("================================================");
        System.out.println("Tomcat Embedded starting...");
        System.out.println("================================================");
        tomcat.start();
        System.out.println("================================================");
        System.out.println("Tomcat Embedded started");
        System.out.println("================================================");

        printTomcatConfiguration(context);

        if (openBrowser) {
            openBrowserWindow(url);
        }

        tomcat.getServer().await();
    }

    private static String parseContextPath(File contextFile) {
        if (!contextFile.exists()) {
            System.out.println("WARNING: context.xml not found, context path set yo '/'");
            return "";
        }
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(contextFile);
            Element root = doc.getDocumentElement();
            if (root.hasAttribute("path")) {
                String path = root.getAttribute("path").trim();
                if (path.equals("/") || path.isEmpty()) {
                    return "";
                }
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                return path;
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.err.println("ERROR; parsing context.xml: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private static void printTomcatConfiguration(Context context) {
        System.out.println("================================================");
        System.out.println("Tomcat Embedded Configuration");
        System.out.println("================================================");
        System.out.println("* Context Path: '" + context.getPath() + "'");
        System.out.println("* Context Name: " + context.getName());
        System.out.println("* Reloadable: " + context.getReloadable());
        System.out.println("* Doc Base: " + context.getDocBase());
        // Verifica che il contesto sia configurato correttamente
        if (!context.getState().isAvailable()) {
            System.err.println("** WARNING: Context is not available!");
            System.err.println("* State: " + context.getStateName());
            return;
        }

        // Stampa risorse JNDI
        org.apache.tomcat.util.descriptor.web.ContextResource[] resources = context.getNamingResources().findResources();
        if (resources.length > 0) {
            System.out.println("* Configured DataSources:");
            for (org.apache.tomcat.util.descriptor.web.ContextResource res : resources) {
                System.out.println("- " + res.getName() + " (" + res.getType() + ")");
                if (res.getProperty("url") != null) {
                    System.out.println("    URL: " + res.getProperty("url"));
                }
                if (res.getProperty("driverClassName") != null) {
                    System.out.println("    Driver: " + res.getProperty("driverClassName"));
                }
            }
        }

        // Stampa environment entries
        org.apache.tomcat.util.descriptor.web.ContextEnvironment[] envs = context.getNamingResources().findEnvironments();
        if (envs.length > 0) {
            System.out.println("* Environment entries:");
            for (org.apache.tomcat.util.descriptor.web.ContextEnvironment env : envs) {
                System.out.println("- " + env.getName() + " = " + env.getValue());
            }
        }
    }

    ///////////////////////

    private static boolean isRunningFromJar() {
        String className = Runner.class.getName().replace('.', '/');
        String classJar = Runner.class.getResource("/" + className + ".class").toString();
        return classJar.startsWith("jar:");
    }

    private static String extractWebappFromJar() throws Exception {
        Path tempDir = Files.createTempDirectory("webapp-");
        tempWebappDir = tempDir.toFile();
        //tempWebappDir.deleteOnExit();
        System.out.println("* Extracting webapp in: " + tempWebappDir.getAbsolutePath());
        extractResourcesFromJar("webapp/", tempWebappDir);
        return tempWebappDir.getAbsolutePath();
    }

    private static void extractResourcesFromJar(String resourcePath, File targetDir) throws Exception {
        URL resourceUrl = Runner.class.getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            System.err.println("ERROR: Resource not found: " + resourcePath);
            return;
        }
        if (resourceUrl.getProtocol().equals("jar")) {
            // Siamo in un JAR
            String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
            try (java.util.jar.JarFile jar = new java.util.jar.JarFile(java.net.URLDecoder.decode(jarPath, "UTF-8"))) {
                java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    java.util.jar.JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(resourcePath)) {
                        String relativePath = name.substring(resourcePath.length());
                        if (relativePath.isEmpty()) {
                            continue;
                        }
                        File targetFile = new File(targetDir, relativePath);
                        if (entry.isDirectory()) {
                            targetFile.mkdirs();
                        } else {
                            targetFile.getParentFile().mkdirs();
                            try (InputStream is = Runner.class.getClassLoader().getResourceAsStream(name)) {
                                if (is != null) {
                                    Files.copy(is, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    ////////////

    private static void openBrowserWindow(String url) {
        try {
            System.out.println("================================================");
            System.out.println("Opening browser...");
            System.out.println("================================================");
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    System.out.println("* Opening browser at address: " + url);
                    Thread.sleep(1500);
                    desktop.browse(new URI(url));
                } else {
                    printBrowserIndications(url);
                }
            } else {
                printBrowserIndications(url);
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.err.println("WARNING: error opening browser: " + e.getMessage());
            printBrowserIndications(url);
        }
    }

    private static void printBrowserIndications(String url) {
        System.out.println("* Please open manually in your browser the address: " + url);
    }

    ////////////////       
    
    private static void configureLogging() {
        //System.setProperty("java.util.logging.config.file", "src/main/resources/logging.properties");
        System.setErr(System.out);
        try {
            InputStream configFile = Runner.class.getClassLoader().getResourceAsStream("logging.properties");
            if (configFile != null) {
                LogManager.getLogManager().readConfiguration(configFile);
                System.out.println("* Logging configured from logging.properties");
            }
            new File("logs").mkdirs();
        } catch (IOException e) {
            System.err.println("ERROR configuring logging: " + e.getMessage());
            e.printStackTrace();
        }
    }

    ////////////////
    
    
     private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("================================================");
            System.out.println("Tomcat Embedded stopping...");
            System.out.println("================================================");
            if (tomcat != null) {
                try {
                    tomcat.stop();
                    tomcat.destroy();
                } catch (LifecycleException e) {
                    System.err.println("ERROR stopping Tomcat: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (tempWebappDir != null && tempWebappDir.exists()) {
                System.out.println("* Cleaning temporary webapp: " + tempWebappDir.getAbsolutePath());
                try {
                    deleteFilesystemItem(tempWebappDir);
                } catch (IOException e) {
                    System.err.println("ERROR: Unable to delete temporary webapp: " + e.getMessage());
                }
            }

            if (tomcatBaseDir != null) {
                File tomcatDir = new File(tomcatBaseDir);
                if (tomcatDir.exists()) {
                    System.out.println("* Cleaning Tomcat folder: " + tomcatBaseDir);
                    try {
                        deleteFilesystemItem(tomcatDir);
                    } catch (IOException e) {
                        System.err.println("ERROR: Unable to delete Tomcat folder: " + e.getMessage());
                    }
                }
            }
        }, "webapp-shutdown-hook"));

        System.out.println("* Shutdown hook registered");
    }

    /////////////
     
     private static void initializeDataDirectory() throws IOException {
        String dataDirPath = null;

        dataDirPath = System.getProperty("app.data.dir");
        if (dataDirPath == null) {
            dataDirPath = System.getenv("APP_DATA_DIR");
        }
        if (dataDirPath == null) {
            dataDirPath = "./data";
        }

        Path dataPath = Paths.get(dataDirPath).toAbsolutePath().normalize();
        persistentDataDir = dataPath.toFile();

        if (!persistentDataDir.exists()) {
            if (persistentDataDir.mkdirs()) {
                System.out.println("* Persistent data folder created: " + persistentDataDir.getAbsolutePath());
            } else {
                // Fallback alla home directory se non si riesce a creare quella richiesta
                String userHome = System.getProperty("user.home");
                persistentDataDir = new File(userHome, "webapp-data");
                if (!persistentDataDir.exists() && !persistentDataDir.mkdirs()) {
                    throw new IOException("ERROR: Unable to create data folder: " + persistentDataDir.getAbsolutePath());
                }

                System.out.println("WARNING: Fallback on home folder for data storage");
            }
        } else {
            System.out.println("* Persistent data folder: " + persistentDataDir.getAbsolutePath());
        }

        if (!persistentDataDir.canWrite()) {
            throw new IOException("ERROR: Unable to write in data folder: " + persistentDataDir.getAbsolutePath());
        }

    }

    /////////////

    private static void deleteFilesystemItem(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFilesystemItem(file);
                }
            }
        }
        if (!directory.delete()) {
            throw new IOException("ERROR: Unable to delete: " + directory.getAbsolutePath());
        }
    }

}
