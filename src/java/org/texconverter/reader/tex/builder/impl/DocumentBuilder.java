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

import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.Token;

/**
 * While {@link org.texconverter.reader.tex.builder.impl.TeXDocumentBuilderImpl}
 * is responsible for the whole TeX document, this builder is only for the
 * "document" environment.
 * 
 * @author tfrana
 */
public class DocumentBuilder extends EmptyBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final AbstractBuilder parentBuilder, final EnvCommand cmd)
            throws TexParserException {
        if (!(parentBuilder instanceof TeXDocumentBuilderImpl)) {
            throw new TexSyntaxException(
                    "document environment encountered inside another environment");
        }
        super.init(parentBuilder, parentBuilder.getCurrentParent(), Token.NONE);
    }
}
