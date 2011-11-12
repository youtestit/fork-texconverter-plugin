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

import java.io.IOException;

import org.texconverter.dom.impl.AbstractNode;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.Token;

/**
 * @author tfrana
 */
public class AppendixBuilder extends EmptyBuilder {

    /**
     * 
     */
    public AppendixBuilder() {
        super();
    }

    /**
     * @param parentBuilder
     *            The parent builder.
     * @param endingTokenId
     *            The token ending this builder or {@link Token#NONE}.
     */
    public AppendixBuilder(final AbstractBuilder parentBuilder,
            final Token endingTokenId) {
        super(parentBuilder, endingTokenId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNode build() throws TexSyntaxException, IOException,
            TexParserException, ConfigurationException {
        ((TeXDocumentBuilderImpl) rootBuilder).setUseAppendixNumbers(true);
        final AbstractNode rv = super.build();
        ((TeXDocumentBuilderImpl) rootBuilder).setUseAppendixNumbers(false);
        return rv;
    }

}
