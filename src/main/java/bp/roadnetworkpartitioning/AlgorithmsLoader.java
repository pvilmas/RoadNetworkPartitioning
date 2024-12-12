package bp.roadnetworkpartitioning;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class that is able to load all available algorithms from folder lib.
 * @author Lucie Roy
 * @version 27-03-2023
 */
public class AlgorithmsLoader {

    /** Path to jar files. */
    private static final String CLASSPATH = "lib";
    /** HashMap with all available partitioning algorithms. */
    private static final Map<String, APartitionAlgorithm> ALGORITHMS = new HashMap<>();

    /**
     * Finds all available partitioning algorithms and saves them to static attribute ALGORITHMS.
     * @return available algorithms.
     */
    public static Map<String, APartitionAlgorithm> findAlgorithms() {
        if (ALGORITHMS.size() == 0) {
            URLClassLoader cl = new URLClassLoader(findJarURLsInClasspath(), Thread.currentThread().getContextClassLoader());
            List<Class<?>> classes = getClasses(cl);
            for (Class<?> clazz : classes) {
                try {
                    if (APartitionAlgorithm.class.isAssignableFrom(clazz)) {
                        Constructor<?> ctor = clazz.getDeclaredConstructor();
                        ctor.setAccessible(true);
                        APartitionAlgorithm alg = (APartitionAlgorithm) ctor.newInstance();
                        alg.setParameters(alg.getAllCustomParameters());
                        alg.setParametersDescriptions(alg.getAllCustomParametersDescriptions());
                        ALGORITHMS.put(alg.getName(), alg);
                    }
                } catch (AbstractMethodError | Exception e) {
                    System.err.println("Could not load algorithm: " + clazz.getSimpleName());
                }
            }
        }
        return ALGORITHMS;
    }

    /**
     * Finds all jar files available in given classpath.
     * @return list of jar files URLs.
     */
    private static URL[] findJarURLsInClasspath() {
        URL url;
        List<URL> jarURLs = new ArrayList<>();
        File[] jars = new File(CLASSPATH).listFiles(pathname -> pathname.getName().toLowerCase().endsWith(".jar"));

        if (jars != null) {
            for (File jar : jars) {
                try {
                    url = jar.toURI().toURL();
                    jarURLs.add(url);
                } catch (Exception e) {
                    System.out.println("There is a problem with reading JAR urls.");
                    e.printStackTrace();
                }
            }
        }
        return jarURLs.toArray(new URL[0]);
    }

    /**
     * Gets all jar files registered in the given URLClassloader
     * and which are present in given classpath places.
     * @return List of jar files URLs.
     */
    private static URL[] getJarURLs(URLClassLoader cl) {
        URL[] result = cl.getURLs();
        List<URL> urls = new ArrayList<>();

        for (URL url : result) {
            try {
                Path jarPath = Paths.get(url.toURI());
                Path classPath = Paths.get(CLASSPATH).toAbsolutePath();
                if (jarPath.startsWith(classPath)) {
                    urls.add(url);
                }
            } catch (URISyntaxException ex) {
                System.out.println("There is a problem with reading JAR urls.");
                ex.printStackTrace();
            }
        }
        result = new URL[urls.size()];
        result = urls.toArray(result);
        return result;
    }

    /**
     * Gets all classes available underneath a given package name.
     * @param cl instance of URLClassLoader.
     * @return list of Classes.
     */
    private static List<Class<?>> getClasses(URLClassLoader cl) {
        List<Class<?>> result = new ArrayList<>();

        for (URL jarURL : getJarURLs(cl)) {
            getClassesFromJar(result, jarURL.getPath(), cl);
        }
        return result;
    }

    /**
     * Gets all classes available in the jar file, underneath a given package name.
     * @param result    list to be filled with all available classes.
     * @param jarPath   path to jar file.
     * @param cl        instance of class loader.
     */
    private static void getClassesFromJar(List<Class<?>> result, String jarPath, URLClassLoader cl) {
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry entry = en.nextElement();
                String entryName = entry.getName();

                if (entryName.endsWith(".class") && !entryName.endsWith("module-info.class")) {
                    try {
                        Class<?> entryClass = cl.loadClass(entryName.substring(0, entryName.length() - 6).replace('/', '.'));
                        if (entryClass != null) {
                            result.add(entryClass);
                        }
                    } catch (Throwable e) {
                        System.out.println("Could not get class from JAR.");
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not get class from JAR.");
            e.printStackTrace();
        }
    }
}
