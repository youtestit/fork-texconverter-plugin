/**
 * $Revision: 1.2 $
 * $Date: 2006/09/14 10:10:58 $
 *
 * ====================================================================
 * TexConverter
 * Copyright (C) 2006 - NEUSTA GmbH Bremen, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * created: 02.08.2006 tfrana
 */
package org.texconverter.resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tfrana
 */
public final class ResourceManager {

    /**
     * TODO move me.
     */
    public static final String CAPTIONNAMES_BUNDLENAME = "plugin-resources/localization/captionNames";

    /**
     * TODO me too.
     */
    public static final String RES_STRINGS = "plugin-resources/localization/localization";

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory.getLog(ResourceManager.class);

    private final List<ResourceLoader> resourceLoaders = new ArrayList<ResourceLoader>(
            1);

    private static final ResourceManager INSTANCE = new ResourceManager();

    private static final Locale LOCALE_FALLBACK = Locale.ENGLISH;

    private ResourceManager() {
    }

    /**
     * @return the singleton instance
     */
    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    /**
     * @param path
     *            the path of the resource
     * @return the InputStream for the resource or <code>null</code>
     */
    public InputStream getResource(final String path) {
        InputStream istream = null;

        for (int i = 0; i < resourceLoaders.size(); i++) {
            istream = resourceLoaders.get(i).getResource(path);
            if (istream != null) {
                break;
            }
        }

        return istream;
    }

    /**
     * Load a resource bundle.
     * 
     * @param resourceBundleName
     *            name of the resource bundle to load
     * @param locale
     *            the locale of the resource to get
     * @return the InputStream for the resource or <code>null</code>
     */
    public ResourceBundle getLocalizedResource(final String resourceBundleName,
            final Locale locale) {
        ResourceBundle resBundle = null;

        Locale local = locale;
        if (local == null) {
            local = Locale.getDefault();
        }

        try {
            // ResourceBundle.getBundle("", locale, null);

            resBundle = ResourceBundle.getBundle(resourceBundleName, local);

        } catch (final MissingResourceException e) {
            // no resource bundle for system locale, try application default
            if (!LOCALE_FALLBACK.equals(local) && locale != null) {
                log.warn("getResource -> could not find resource for locale "
                        + locale.getCountry() + ", using fallback locale "
                        + LOCALE_FALLBACK.getCountry() + " instead");
                resBundle = ResourceBundle.getBundle(resourceBundleName,
                        LOCALE_FALLBACK);
            } else {
                throw e;
            }
        }

        return resBundle;
    }

    /**
     * Add a new {@link ResourceLoader}.
     * 
     * @param resourceLoader
     *            the {@link ResourceLoader} to add.
     */
    public void addResourceLoader(final ResourceLoader resourceLoader) {
        if (resourceLoader == null) {
            throw new IllegalArgumentException("resourceLoader may not be null");
        }

        resourceLoaders.add(resourceLoader);
    }

    /**
     * Delete all resource loaders.
     */
    public void clearResourceLoaders() {
        resourceLoaders.clear();
    }

}
