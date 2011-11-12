/**
 * $Revision: 1.2 $
 * $Date: 2006/09/26 09:31:36 $
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
package org.texconverter.reader.tex.builder;

import java.io.File;
import java.io.IOException;

import org.texconverter.dom.TexDocument;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;

/**
 * Interface for a TeX-document builder. Use
 * {@link org.texconverter.reader.tex.builder.impl.BuilderFactory} to create an
 * instance.
 * 
 * @author tfrana
 */
public interface TeXDocumentBuilder {

    /**
     * Parses a TeX file and converts it into a {@link TexDocument}.
     * 
     * @param texFile
     *            the tex file
     * @return the TexDocument
     * @throws TexParserException
     *             on general parser problems
     * @throws IOException
     *             on IO problems
     * @throws ConfigurationException
     *             on configuration problems
     */
    TexDocument build(File texFile) throws TexParserException, IOException,
            ConfigurationException;
}
