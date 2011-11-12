/**
 * $Revision: 1.2 $
 * $Date: 2006/09/28 13:13:40 $
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
 * created: 26.09.2006 tfrana
 */
package org.texconverter.reader.tex.handlers.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.IndexImpl;
import org.texconverter.dom.impl.IndexMarkImpl;
import org.texconverter.reader.tex.Helper;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.handlers.CommandHandler;
import org.texconverter.resource.ResourceManager;

/**
 * Handles index-related commands.
 * 
 * @author tfrana
 */
public class IndexHandler implements CommandHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexHandler.class);

    private static final String RESKEY_INDEXTITLE = "INDEX";

    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws TexParserException, IOException, ConfigurationException {

        if ("index".equals(cmd.getName())) {
            String label = Helper.getPlainTextFromNodeList((List<Node>) cmd
                    .getMandatoryArgs().get(0));
            IndexMarkImpl indexMark = new IndexMarkImpl();
            indexMark.setLabel(label);
            builder.consumeNode(indexMark, false);

        } else if ("printindex".equals(cmd.getName())) {
            String indexTitle = ResourceManager.getInstance()
                    .getLocalizedResource(ResourceManager.RES_STRINGS,
                            builder.getLocale()).getString(RESKEY_INDEXTITLE);
            IndexImpl index = new IndexImpl(indexTitle);
            builder.consumeNode(index, false);

        } else if ("theindex".equals(cmd.getName())) {
            LOGGER.warn("handle -> cmd \"theindex\" is not supported because page"
                    + " numbers are not static (or not at all) in the output."
                    + " Consider using MakeIndex instead.");
        }
    }
}
