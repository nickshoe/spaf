package it.unibo.disi.spaf.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import it.unibo.disi.spaf.common.exceptions.StreamProcessingException;

/**
 * Heavily inspired by org.apache.kafka.common.config.AbstractConfig and org.apache.kafka.common.utils.Utils classes 
 */
public class ReflectionUtils {
	
	/**
     * Get an instance of the given class.
     *
     * @param classRef the class
     * @param baseClassRef The interface the class should implement
     * @return an instance of the class
     */
	public static <T> T getInstance(String className, Class<T> baseClassRef) {
        if (className == null) {
            return null;
        }

        Object instance;
        try {
            instance = ReflectionUtils.newInstance(className, baseClassRef);
        } catch (ClassNotFoundException e) {
            throw new StreamProcessingException("Class " + className + " cannot be found", e);
        }
        
        if (!baseClassRef.isInstance(instance)) {
            throw new StreamProcessingException(className + " is not an instance of " + baseClassRef.getName());
        }

        return baseClassRef.cast(instance);
    }
	
	/**
     * Look up the class by name and instantiate it.
     * 
     * @param className class name
     * @param baseClass base class of the class to be instantiated
     * @param <T> the type of the base class
     * @return the new instance
     */
    public static <T> T newInstance(String className, Class<T> baseClass) throws ClassNotFoundException {
        return ReflectionUtils.newInstance(loadClass(className, baseClass));
    }
	
	/**
     * Instantiate the class
     */
    public static <T> T newInstance(Class<T> classRef) {
        if (classRef == null) {
            throw new StreamProcessingException("class cannot be null");
        }
        
        Constructor<T> constructor;
		try {
			constructor = classRef.getDeclaredConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new StreamProcessingException("Could not get the public no-argument constructor for " + classRef.getName(), e);
		}
		
		T instance;
		try {
			instance = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {			
			throw new StreamProcessingException("Could not instantiate class " + classRef.getName(), e);
		}
		
		return instance;
    }
	
	/**
     * Look up a class by name.
     * 
     * @param className class name
     * @param baseClass base class of the class for verification
     * @param <T> the type of the base class
     * @return the class
     */
	public static <T> Class<? extends T> loadClass(String className, Class<T> baseClass) throws ClassNotFoundException {
        return Class.forName(className, true, ReflectionUtils.getContextOrSPAFClassLoader()).asSubclass(baseClass);
    }
	
	/**
     * Get the Context ClassLoader on this thread or, if not present, the ClassLoader that
     * loaded SPAF.
     *
     * This should be used whenever passing a ClassLoader to Class.forName
     */
	public static ClassLoader getContextOrSPAFClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            return ReflectionUtils.getSPAFClassLoader();
        } else {
            return cl;
        }
    }
	
	/**
     * Get the ClassLoader which loaded SPAF.
     */
    public static ClassLoader getSPAFClassLoader() {
        return ReflectionUtils.class.getClassLoader();
    }
    
}
