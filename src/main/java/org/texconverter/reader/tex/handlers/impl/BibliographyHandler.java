/**
 * $Revision: 1.2 $
 * $Date: 2006/09/28 12:08:29 $
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
 * created: 08.08.2006 tfrana
 */
package org.texconverter.reader.tex.handlers.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.impl.BibliographyImpl;
import org.texconverter.dom.impl.BibliographyItemImpl;
import org.texconverter.dom.impl.BibliographyRefImpl;
import org.texconverter.dom.impl.Command;
import org.texconverter.reader.tex.Helper;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * @author tfrana
 */
public class BibliographyHandler implements CommandHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(BibliographyHandler.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws TexParserException, IOException, ConfigurationException {

        if ("bibliography".equals(cmd.getName())) {
            BibliographyImpl bibliography = new BibliographyImpl(null);
            builder.consumeNode(bibliography, false);
            // the bibliography must be constructed later, notify the builder

            String bibTexLibraryFile = Helper
                    .getPlainTextFromNodeList((List<Node>) cmd
                            .getMandatoryArgs().get(0));
            builder.setBibTexLibraryFile(bibTexLibraryFile);
            builder.setDoBibTexPostprocessing(true);
        } else if ("bibliographystyle".equals(cmd.getName())) {
            builder.setBibTexStyle(Helper
                    .getPlainTextFromNodeList((List<Node>) cmd
                            .getMandatoryArgs().get(0)));
        } else if ("cite".equals(cmd.getName())) {
            final String bibKey = Helper
                    .getPlainTextFromNodeList((List<Node>) cmd
                            .getMandatoryArgs().get(0));
            if (bibKey.length() > 0) {
                String note = null;
                if (cmd.getOptionalArgs() != null
                        && cmd.getOptionalArgs().size() > 0) {
                    note = Helper.getPlainTextFromNodeList((List<Node>) cmd
                            .getOptionalArgs().get(0));
                }

                final BibliographyRefImpl bibRef = new BibliographyRefImpl(
                        bibKey, note);
                builder.consumeNode(bibRef, false);
            } else {
                LOGGER.error("handle -> cite command with empty key");
            }

        } else if ("nocite".equals(cmd.getName())) {
            builder.addBibEntry(Helper
                    .getPlainTextFromNodeList((List<Node>) cmd
                            .getMandatoryArgs().get(0)));
        } else if ("bibitem".equals(cmd.getName())) {
            BibliographyItemImpl bibItem = new BibliographyItemImpl(Helper
                    .getPlainTextFromNodeList((List<Node>) cmd
                            .getMandatoryArgs().get(0)));
            if (cmd.getOptionalArgs() != null
                    && cmd.getOptionalArgs().size() > 0) {
                bibItem.setReferenceText((List<Node>) cmd.getOptionalArgs()
                        .get(0));
            }
            builder.consumeNode(bibItem, true);
        }
    }
}
