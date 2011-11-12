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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tfrana
 */
public class FileResourceLoader implements ResourceLoader {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileResourceLoader.class);

    /**
     * {@inheritDoc}
     */
    public InputStream getResource(final String path) {
        InputStream istream = null;
        LOGGER.debug("trying to retrieve resource " + path);
        try {
            istream = new FileInputStream(path);
        } catch (final FileNotFoundException e) {
            LOGGER.warn("File " + path + " not found", e);
        }
        return istream;
    }

}
