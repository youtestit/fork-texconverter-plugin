/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:36 $
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tfrana
 */
public class FileResourceLoader implements ResourceLoader {

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory
            .getLog(FileResourceLoader.class);

    /**
     * {@inheritDoc}
     */
    public InputStream getResource(final String path) {
        InputStream istream = null;
        log.debug("trying to retrieve resource " + path);
        try {
            istream = new FileInputStream(path);
        } catch (final FileNotFoundException e) {
            log.warn("File " + path + " not found", e);
        }
        return istream;
    }

}
