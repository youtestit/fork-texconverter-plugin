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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.impl.Command;
import org.texconverter.io.impl.FilePeekReader;
import org.texconverter.io.impl.StringPeekReader;
import org.texconverter.reader.tex.Helper;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.handlers.CommandHandler;
import org.texconverter.reader.tex.parser.Tokenizer;

/**
 * @author tfrana
 */
public class LstListingHandler implements CommandHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(LstListingHandler.class);

    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder) {

        // TODO handle lstset (eval & store in current builder

        if ("lstinputlisting".equals(cmd.getName())) {
            evalLstInputListing(cmd, builder);
        }
    }

    @SuppressWarnings("unchecked")
    private void evalLstInputListing(final Command cmd,
            final AbstractBuilder builder) {
        final Tokenizer tokenizer = builder.getTokenizer();
        final String filePath = Helper
                .getPlainTextFromNodeList((List<Node>) cmd.getMandatoryArgs()
                        .get(0));

        try {
            final FilePeekReader inputFile = new FilePeekReader(new File(
                    builder.getTexFileBaseDir(), filePath));
            tokenizer.insertInput(new StringPeekReader("\n\\end{lstlisting}"));
            tokenizer.insertInput(inputFile);

            String beginCmd = "\\begin{lstlisting}";
            if (cmd.getOptionalArgs() != null) {
                beginCmd = beginCmd + "[" + cmd.getOptionalArgs().get(0)
                        + "]\\n";
            }
            tokenizer.insertInput(new StringPeekReader(beginCmd));
        } catch (final FileNotFoundException e) {
            LOGGER.error("File " + filePath + " could not be found", e);
        }
    }
}
