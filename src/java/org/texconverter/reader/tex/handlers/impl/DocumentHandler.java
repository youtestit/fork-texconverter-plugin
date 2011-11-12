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
 * created: 02.08.2006 tfrana
 */
package org.texconverter.reader.tex.handlers.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.NodeWithChilds;
import org.texconverter.dom.TexDocument;
import org.texconverter.dom.Text;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.TexDocumentImpl;
import org.texconverter.dom.impl.TextImpl;
import org.texconverter.io.PeekReader;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.builder.impl.TeXDocumentBuilderImpl;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * @author tfrana
 */
public class DocumentHandler implements CommandHandler {

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory.getLog(DocumentHandler.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void handle(final Command cmd, final AbstractBuilder builder) {

        // TODO check which commands really have global meaning and which are
        // context sensitive (likely "title")
        final NodeWithChilds root = builder.getRootNode();
        if (root instanceof TexDocumentImpl
                && builder instanceof TeXDocumentBuilderImpl) {

            final TexDocumentImpl texdoc = (TexDocumentImpl) root;

            if ("documentclass".equals(cmd.getName())) {
                final String docclass = (String) cmd.getMandatoryArgs().get(0);
                texdoc.setDocumentClass(docclass);

                // if (cmd.getOptionalArgs() != null) {
                // TODO evaluate optional args
                // }
            } else if ("pdfinfo".equals(cmd.getName())) {
                evaluatePdfInfo(cmd, texdoc);
            } else if ("title".equals(cmd.getName())) {
                // evaluateTitle(cmd, builder);
                final List<Node> nodes = (List<Node>) cmd.getMandatoryArgs()
                        .get(0);
                texdoc.setTitle(nodes);
            } else if ("author".equals(cmd.getName())) {
                evaluateAuthorInfo(cmd, texdoc);
            } else if ("date".equals(cmd.getName())) {
                final String value = (String) cmd.getMandatoryArgs().get(0);
                try {
                    texdoc.setDate(DateFormat.getDateInstance().parse(value));
                } catch (final ParseException e) {
                    log.error("handle -> \"" + value
                            + "\" could not be parsed as a valid date.");
                }
            }
        }
    }

    /**
     * Document info can be specified by the commands \author, \date etc AND by
     * \pdfinfo. This evaluates the pdfinfo info. This always takes second place
     * to the other commands info (so pdfinfo will not overwrite, but the others
     * will).
     * 
     * @param cmd
     * @param texdoc
     */
    private void evaluatePdfInfo(final Command cmd, final TexDocument texdoc) {
        // TODO write parser for pdfinfo arg

        final String args = (String) cmd.getMandatoryArgs().get(0);
        final String[] lines = args.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            line = line.trim();

            String cmdName = null, value = null;
            int j = line.indexOf('/'), i2 = line.indexOf(' ');
            if (j > -1 && i2 > j) {
                cmdName = line.substring(j + 1, i2);
            }
            j = line.indexOf('(');
            i2 = line.indexOf(')');
            if (j > -1 && i2 > j) {
                value = line.substring(j + 1, i2);
            }

            if (cmdName != null && value != null) {
                if ("Title".equals(cmdName) && texdoc.getTitle() == null) {
                    final List<Node> title = new ArrayList<Node>();
                    title.add(new TextImpl(value));
                    texdoc.setTitle(title);
                } else if ("Author".equals(cmdName)
                        && texdoc.getAuthors() == null) {
                    // TODO how are several authors handled in pdfinfo
                    texdoc.setAuthors(new String[] { value });
                } else if ("ModDate".equals(cmdName)
                        && texdoc.getDate() == null) {
                    try {
                        texdoc.setDate(DateFormat.getDateInstance()
                                .parse(value));
                    } catch (final ParseException e) {
                        log.error("evaluatePdfInfo -> \"" + value
                                + "\" could not be parsed as a valid date.");
                    }

                }
            }
        }
    }

    private void evaluateAuthorInfo(final Command cmd, final TexDocument texdoc) {
        // TODO write parser for this

        final List arg = (List) cmd.getMandatoryArgs().get(0);

        String lineBuffer = "";
        final List<String> authors = new ArrayList<String>();

        Node node;
        boolean continuedLine = false;
        final Iterator it = arg.iterator();
        while (it.hasNext()) {
            node = (Node) it.next();

            if (node instanceof Text) {
                String txt = ((Text) node).getContent().trim();
                if (continuedLine) {
                    txt = lineBuffer + PeekReader.LINEBREAK + txt;
                    continuedLine = false;
                }
                lineBuffer = txt;
            } else if (node instanceof Command) {
                if ("\\".equals(((Command) node).getName())) {
                    continuedLine = true;
                } else if ("and".equals(((Command) node).getName())) {
                    authors.add(lineBuffer);
                    lineBuffer = "";
                }
            }
        }
        if (lineBuffer.length() > 0) {
            authors.add(lineBuffer);
        }

        final Object[] x = authors.toArray();
        final String[] y = new String[x.length];
        for (int i = 0; i < x.length; i++) {
            y[i] = (String) x[i];
        }
        texdoc.setAuthors(y);
    }
}
