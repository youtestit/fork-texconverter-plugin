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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.AlignedNode;
import org.texconverter.dom.Node;
import org.texconverter.dom.impl.BreakImpl;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.dom.impl.TableFieldImpl;
import org.texconverter.dom.impl.TableImpl;
import org.texconverter.dom.impl.TableRowImpl;
import org.texconverter.dom.impl.TextImpl;
import org.texconverter.reader.tex.builder.EnvironmentBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.TableParser;
import org.texconverter.reader.tex.parser.Token;

/**
 * Builder for the tabular environment.
 * 
 * @author tfrana
 */
public final class TableBuilder extends AbstractBuilder implements
        EnvironmentBuilder {

    private final static Logger LOGGER = LoggerFactory.getLogger(RootBuilderModule.class);

    private List<AlignedNode.Alignment> columnAlignments;

    private List<List<Node>> staticColumnContents;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final AbstractBuilder parentBuilder,
            final EnvCommand envCmd) throws TexParserException {
        super.init(parentBuilder, new TableImpl(envCmd.getName()), Token.NONE);
        evalArgs(envCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableImpl build() throws TexSyntaxException, IOException,
            TexParserException, ConfigurationException {
        startNewRow();
        TableParser.parse(this);
        finish();
        return (TableImpl) root;
    }

    /**
     * Start a new column in the table.
     */
    public void startNewColumn() {
        final TableFieldImpl tableField = new TableFieldImpl();
        consumeNode(tableField, true);

        final int col = tableField.getParent().getNumChildren() - 1;

        tableField.setAlignment(columnAlignments.get(col));
        if (staticColumnContents.get(col) != null) {
            tableField.getChildren().addAll(staticColumnContents.get(col));
        }
    }

    /**
     * Start a new row in the table.
     */
    public void startNewRow() {
        consumeNode(new TableRowImpl(), true);
        startNewColumn();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws ConfigurationException
     * @throws TexParserException
     */
    @Override
    protected void finish() throws IOException, TexParserException,
            ConfigurationException {
        super.finish();
        if (root.getChildren().size() > 0) {
            final List rows = root.getChildren();
            final TableRowImpl lastRow = (TableRowImpl) rows
                    .get(rows.size() - 1);
            if (lastRow.getChildren().size() > 0) {
                final List lastRowFields = lastRow.getChildren();
                List fieldChilds;
                Object child;
                boolean hasChilds = false;
                for (int i = 0; i < lastRowFields.size(); i++) {
                    fieldChilds = ((TableFieldImpl) lastRowFields.get(i))
                            .getChildren();
                    for (int j = 0; j < fieldChilds.size(); j++) {
                        child = fieldChilds.get(j);
                        if (child instanceof TextImpl) {
                            if (((TextImpl) child).getContent().trim().length() > 0) {
                                hasChilds = true;
                                break;
                            }
                        } else if (!(child instanceof BreakImpl)) {
                            hasChilds = true;
                            break;
                        }
                    }
                }
                if (!hasChilds) {
                    rows.remove(rows.size() - 1);
                }
            }
        }
    }

    private void evalArgs(final EnvCommand envCmd) throws TexParserException {
        final String strArg = (String) envCmd.getMandatoryArgs().get(0);
        final char[] arg = strArg.toCharArray();

        final TableImpl table = (TableImpl) root;

        columnAlignments = new ArrayList<AlignedNode.Alignment>();
        staticColumnContents = new ArrayList<List<Node>>();

        for (int i = 0; i < arg.length; i++) {
            // if (arg[i] == '|') {
            // TODO eval borders
            // } else
            if (arg[i] == 'l') {
                columnAlignments.add(AlignedNode.Alignment.LEFT);
            } else if (arg[i] == 'r') {
                columnAlignments.add(AlignedNode.Alignment.RIGHT);
            } else if (arg[i] == 'c') {
                columnAlignments.add(AlignedNode.Alignment.CENTER);
            } else if (arg[i] == 'p') {
                i++;
                // p{width} -> similar to \parbox
                if (arg[i] != '{') {
                    throw new TexSyntaxException(
                            "missing block in tabular environment options");
                }
                final int iClose = strArg.indexOf('}', i);
                if (iClose < 0) {
                    throw new TexSyntaxException(
                            "unclosed block in tabular environment options");
                }
                i = iClose + 1;
                columnAlignments.add(AlignedNode.Alignment.CENTER);
                // TODO use this width hint
            } else if (arg[i] == '@') {
                // TODO behaviour of static texts not that straightforward..
                // behave like an extra column, but with no space to neighbor
                // columns
                // or like text that is simply aligned col-wise
                i++;
                if (arg[i] != '{') {
                    throw new TexSyntaxException(
                            "missing block in tabular environment options");
                }
                final int iClose = strArg.indexOf('}', i);
                if (iClose < 0) {
                    throw new TexSyntaxException(
                            "unclosed block in tabular environment options");
                }
                //
                // String expression = strArg.substring(i + 1, iClose + 1);
                // getTokenizer().insertInput(new StringPeekReader(expression));
                // NodeBuilder nodeBuilder = new NodeBuilder(this,
                // Tokenizer.TOKEN_BLOCKCLOSE);
                //
                // // TODO commands like extracolsep must be handled here
                // List nodes = ((NodeWithChilds) nodeBuilder.parse())
                // .getChildren();

                // while (alignments.size() > staticContents.size() + 1) {
                // staticContents.add(null);
                // }
                // staticContents.add(nodes);
                i = iClose + 1;
            } else {
                LOGGER.warn("evalOptArgs -> unknown option " + arg[i]
                        + " in tabular environment options");
            }
        }

        while (columnAlignments.size() > staticColumnContents.size()) {
            staticColumnContents.add(null);
        }

        // Integer[] colAlignments = (Integer[]) alignments.toArray();
        // List[] staticColContents = (List[]) staticContents.toArray();

        table.setNumColumns(columnAlignments.size());
    }
}
