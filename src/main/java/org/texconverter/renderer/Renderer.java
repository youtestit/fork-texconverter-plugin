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
package org.texconverter.renderer;

import org.texconverter.TexConverter.OUTPUTFORMATS;
import org.texconverter.dom.TexDocument;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.RendererException;

/**
 * @author tfrana
 */
public interface Renderer {

    /**
     * Renders the TexDocument.
     * 
     * @param document
     *            the TexDocument
     * @param outputFormat
     *            the output format
     * @param resourceDir
     *            the base directory of the application (used for finding
     *            application resources)
     * @return the render output
     * @throws RendererException
     *             on problems rendering the document
     * @throws ConfigurationException
     *             if the renderer could not be properly configured
     */
    String render(OUTPUTFORMATS outputFormat, TexDocument document,
            String resourceDir) throws RendererException,
            ConfigurationException;

}
