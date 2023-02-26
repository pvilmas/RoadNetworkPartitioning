package bp.roadnetworkpartitioning;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Main window of app.
 */
public class MainView extends Application {

    /** Path to jar files */
    private static final String[] classPath = {"lib"};

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        MainController.setStage(stage);
        HashMap<String, IPartitioning> algorithms = getAlgorithms();
        stage.setTitle("Main Window - Road Network Partitioning");
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();

    }

    private HashMap<String, IPartitioning> getAlgorithms() {
        HashMap<String, IPartitioning> algorithms = new HashMap<>();
        URLClassLoader cl = new URLClassLoader(findJarURLsInClasspath(), Thread.currentThread().getContextClassLoader());
        List<Class> classes = MainView.getClassesFromPackage(cl);
        for (Class clazz: classes) {
            try {
                if(IPartitioning.class.isAssignableFrom(clazz)){
                    Constructor ctor = clazz.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    IPartitioning alg = (IPartitioning) ctor.newInstance();
                    algorithms.put(alg.getName(), alg);
                }
            }catch (Exception e){

            }
        }
        return algorithms;

    }

    /**
     * Method that finds all jar files available in given dedicated classpath
     * places. It serves for an URLClassloader initialization.
     *
     * @return List of jar files URLs
     */
    private static URL[] findJarURLsInClasspath() {
        URL url;

        ArrayList<URL> jarURLs = new ArrayList();

        for (String path : classPath) {

            File[] jars = new File(path).listFiles(new FileFilter() {
                public boolean accept(File pathname) {

                    return pathname.getName().toLowerCase().endsWith(".jar");
                }
            });

            if (jars != null) {
                for (int i = 0; i < jars.length; i++) {
                    try {
                        System.out.println("JAR Path: " + jars[i].getAbsolutePath());
                        url = jars[i].toURI().toURL();

                        jarURLs.add(url);

                    } catch (Exception e) {

                    }
                }
            }
        }

        URL[] urls = jarURLs.toArray(new URL[0]);
        return urls;
    }

    /**
     * Method that returns all jar files registered in the given URLClassloader
     * and which are present in dedicated classpath places.
     *
     * @return List of jar files URLs
     */
    private static URL[] getJarURLs(URLClassLoader cl) {
        URL[] result = cl.getURLs();
        ArrayList<URL> urls = new ArrayList();

        for (URL url : result) {

            try {
                Path jarPath = Paths.get(url.toURI());

                for (String classPathString : classPath) {

                    Path classPath = Paths.get(classPathString).toAbsolutePath();

                    if (jarPath.startsWith(classPath)) {
                        urls.add(url);
                    }
                }

            } catch (URISyntaxException ex) {
            }
        }

        result = new URL[urls.size()];
        result = urls.toArray(result);

        return result;
    }



    /**
     * Method that returns all classes available underneath a given package
     * name.
     *
     * @return Set of Classes
     */
    private static List<Class> getClassesFromPackage(URLClassLoader cl) {
        List<Class> result = new ArrayList<>();

        for (URL jarURL : getJarURLs(cl)) {
            getClassesInSamePackageFromJar(result, jarURL.getPath(), cl);
        }

        return result;
    }

    /**
     * Method that fills TreeMap with all classes available in a particular jar
     * file, underneath a given package name.
     *
     */
    private static void getClassesInSamePackageFromJar(List<Class> result, String jarPath, URLClassLoader cl) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);

            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry entry = en.nextElement();
                String entryName = entry.getName();

                if (entryName != null && entryName.endsWith(".class")) {

                    try {
                        Class<?> entryClass = cl.loadClass(entryName.substring(0, entryName.length() - 6).replace('/', '.'));
                        if (entryClass != null) {
                            result.add(entryClass);
                        }
                    } catch (Throwable e) {
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (jarFile != null) {
                    jarFile.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Main method launches the app.
     * @param args  (unused)
     */
    public static void main(String[] args) {
        launch();
    }
}
