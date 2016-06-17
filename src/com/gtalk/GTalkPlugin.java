package com.gtalk;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class GTalkPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.gtalk";

	// The shared instance
	private static GTalkPlugin plugin;
	
    private ResourceBundle resourceBundle;
	
	/**
	 * The constructor
	 */
	public GTalkPlugin() {
        super();
        plugin = this;

        try {
            resourceBundle = ResourceBundle.getBundle("com.gtalk.PluginResources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static GTalkPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public Image getImage(String key) {
		// First check the image registry
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null) { // check the image descriptor registry
			ImageDescriptor descriptor = getImageDescriptor(key);
			if (descriptor != null) {
				imageRegistry.put(key, descriptor);
				image = imageRegistry.get(key);
			} else {

			}
		}
		return image;
	}
	
    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key 
     * @return 
     */
    public static String getString(String key) {
        ResourceBundle bundle = getDefault().getResourceBundle();

        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     * @return 
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

}
