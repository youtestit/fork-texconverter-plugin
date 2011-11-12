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
 * created: 02.08.2006 tfrana
 */
package org.texconverter.reader.tex.builder.impl;

import org.texconverter.dom.Listing;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.dom.impl.ListingImpl;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.parser.Token;

/**
 * Builder for the enumeration environments (enumerate, description, itemize).
 * 
 * @author tfrana
 */
public class ListingBuilder extends AbstractBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final AbstractBuilder parentBuilder, final EnvCommand cmd)
            throws ConfigurationException {
        final String envName = cmd.getName();
        final ListingImpl list = new ListingImpl(envName);
        if ("itemize".equals(envName)) {
            list.setStyle(Listing.ListingStyles.BULLETS);
        } else if ("enumerate".equals(envName)) {
            list.setStyle(Listing.ListingStyles.NUMBERS);
        } else if ("description".equals(envName)) {
            list.setStyle(Listing.ListingStyles.DESCRIPTIONS);
        } else {
            throw new ConfigurationException("environment " + envName
                    + " unhandled by ListingBuilder");
        }

        super.init(parentBuilder, list, Token.NONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewParagraph() {
        // no paragraphs in listings
    }
}
