/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:30 $
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
 * created: 03.08.2006 tfrana
 */
package org.texconverter.reader.tex.handlers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.GlossaryEntryImpl;
import org.texconverter.dom.impl.GlossaryImpl;
import org.texconverter.dom.impl.TextImpl;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.builder.impl.TeXDocumentBuilderImpl;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.handlers.CommandHandler;
import org.texconverter.resource.ResourceManager;

/**
 * @author tfrana
 */
public class GlossaryHandler implements CommandHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(GlossaryHandler.class);

    private static final String RESKEY_GLOSSARYTITLE = "GLOSSARY";

    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws TexParserException, IOException, ConfigurationException {

        if ("glossary".equals(cmd.getName())) {
            evalGlossaryCmd(cmd, builder);
        } else if ("nomenclature".equals(cmd.getName())) {
            evalNomenclatureCmd(cmd, builder);
        } else if ("printglossary".equals(cmd.getName())
                || "printnomenclature".equals(cmd.getName())) {

            String glossaryTitle = ResourceManager.getInstance()
                    .getLocalizedResource(ResourceManager.RES_STRINGS,
                            builder.getLocale())
                    .getString(RESKEY_GLOSSARYTITLE);
            builder.consumeNode(new GlossaryImpl(glossaryTitle), false);
        }
    }

    @SuppressWarnings("unchecked")
    private void evalNomenclatureCmd(final Command cmd,
            final AbstractBuilder builder) {
        final TeXDocumentBuilderImpl rootBuilder = (TeXDocumentBuilderImpl) builder
                .getRootBuilder();

        String sortKey = null;
        if (cmd.getOptionalArgs() != null) {
            sortKey = (String) cmd.getOptionalArgs().get(0);
        }

        final GlossaryEntryImpl glossaryEntry = new GlossaryEntryImpl(
                (List<Node>) cmd.getMandatoryArgs().get(0), (List<Node>) cmd
                        .getMandatoryArgs().get(1), sortKey);

        rootBuilder.addGlossaryEntry(glossaryEntry);
    }

    /**
     * @param cmd
     */
    private void evalGlossaryCmd(final Command cmd,
            final AbstractBuilder builder) {
        final Map<String, String> keyvalMap = new HashMap<String, String>();

        final String[] keyvals = ((String) cmd.getMandatoryArgs().get(0))
                .split(",");
        for (int i = 0; i < keyvals.length; i++) {
            keyvals[i] = keyvals[i].replaceAll("\\n", "");
            final String[] keyval = keyvals[i].trim().split("=");

            if (keyval.length != 2) {
                LOGGER.error("evalKeyValueArgs -> encountered malformed key-value optional arg: "
                                + keyvals[i]);
            } else {
                keyvalMap.put(keyval[0], keyval[1]);
            }
        }

        if (!keyvalMap.containsKey("name")) {
            LOGGER.error("arg for glossary: required argument \"name\" not defined!");
        } else if (!keyvalMap.containsKey("description")) {
            LOGGER.error("arg for glossary: required argument \"description\" not defined!");
        } else {
            String sortKey = null;
            if (keyvalMap.containsKey("sort")) {
                sortKey = keyvalMap.get("sort");
            }

            // TODO temporily handling the args as plain text, until proper
            // parser for keyval-args is written
            final List<Node> lnName = new ArrayList<Node>();
            lnName.add(new TextImpl(keyvalMap.get("name")));

            final List<Node> lnDescr = new ArrayList<Node>();
            lnDescr.add(new TextImpl(keyvalMap.get("description")));

            final GlossaryEntryImpl glossaryEntry = new GlossaryEntryImpl(
                    lnName, lnDescr, sortKey);
            builder.addGlossaryEntry(glossaryEntry);
        }
    }
}
