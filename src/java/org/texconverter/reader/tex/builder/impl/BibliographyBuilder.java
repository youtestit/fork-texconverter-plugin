/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:18 $
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
 * created: 14.08.2006 tfrana
 */
package org.texconverter.reader.tex.builder.impl;

import java.io.IOException;

import org.texconverter.dom.impl.BibliographyImpl;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.parser.Token;
import org.texconverter.resource.ResourceManager;

/**
 * @author tfrana
 */
public class BibliographyBuilder extends AbstractBuilder {

    private static final String RESKEY_BIBTITLE = "BIBLIOGRAPHY";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init(final AbstractBuilder theParentBuilder,
            final EnvCommand cmd) throws IOException, TexParserException,
            ConfigurationException {
        String bibliographyTitle = ResourceManager.getInstance()
                .getLocalizedResource(ResourceManager.RES_STRINGS,
                        theParentBuilder.getLocale())
                .getString(RESKEY_BIBTITLE);
        super.init(theParentBuilder, new BibliographyImpl(bibliographyTitle),
                Token.NONE);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewParagraph() {
        // no paragraphs in bibliography
    }
}
