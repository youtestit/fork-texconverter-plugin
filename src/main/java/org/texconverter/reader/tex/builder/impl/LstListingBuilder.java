/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:19 $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.texconverter.dom.Node;
import org.texconverter.dom.impl.CaptionImpl;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.dom.impl.FloatingEnvironmentImpl;
import org.texconverter.dom.impl.LstListingImpl;
import org.texconverter.io.PeekReader;
import org.texconverter.io.impl.StringPeekReader;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.Token;
import org.texconverter.reader.tex.parser.Tokenizer;
import org.texconverter.reader.tex.parser.VerbatimParser;

/**
 * Builder for the lstlisting environment.
 * 
 * @author tfrana
 */
public final class LstListingBuilder extends EmptyBuilder {

    private static final Pattern RERIGHTWHITESPACE = Pattern.compile("\\s+\\z");

    private LstListingImpl lstListing;

    /**
     * {@inheritDoc}
     */
    @Override
    public FloatingEnvironmentImpl build() throws IOException {
        // any trailing whitespace/newlines is cut off by lstlisting
        final Matcher m = RERIGHTWHITESPACE.matcher(VerbatimParser
                .parseEnvironmentVerbatim(getTokenizer(), "lstlisting"));
        lstListing.setContent(m.replaceAll(""));

        return (FloatingEnvironmentImpl) root;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws ConfigurationException
     * @throws TexParserException
     * @throws IOException
     * @throws TexSyntaxException
     */
    @Override
    public void init(final AbstractBuilder parentBuilder,
            final EnvCommand envCmd) throws TexSyntaxException, IOException,
            TexParserException, ConfigurationException {

        super.init(parentBuilder,
                new FloatingEnvironmentImpl(envCmd.getName()), Token.NONE);
        evalArgs(envCmd);
    }

    private void evalArgs(final EnvCommand envCmd) throws TexSyntaxException,
            IOException, TexParserException, ConfigurationException {
        // AlignedNodeImpl alignment = new AlignedNodeImpl();
        // alignment.setAlignement(AlignedNode.Alignment.CENTER);
        // consumeNode(alignment, true);
        lstListing = new LstListingImpl();
        consumeNode(lstListing, false);

        if (envCmd.getOptionalArgs() != null) {
            final FloatingEnvironmentImpl floatEnv = (FloatingEnvironmentImpl) root;
            // String title = null;
            String label = null, caption = null;
            final Map localProps = parseOptions((String) envCmd
                    .getOptionalArgs().get(0));

            // title = (String) localProps.get("title");
            label = (String) localProps.get("label");
            caption = (String) localProps.get("caption");

            // TODO handle scope properties

            // TODO solve title - caption rivalry
            // if (title != null) {
            //
            // }

            if (label != null) {
                addLabel(label, floatEnv);
                floatEnv.setLabel(label);
            }

            if (caption != null) {
                NodeBuilder nodeBuilder = new NodeBuilder(this, Token.NONE);
                Tokenizer miniTokenizer = new Tokenizer();
                miniTokenizer.insertInput(new StringPeekReader(caption));
                nodeBuilder.setTokenizer(miniTokenizer);

                final List<Node> l = nodeBuilder.build().getChildren();

                final CaptionImpl cap = new CaptionImpl();
                cap.setChildren(l);

                consumeNode(cap, false);
            }
        }
    }

    private static Map parseOptions(final String opts) {
        // TODO syntax likely used by more than lstlisting, extract and
        // generalize
        final Map<String, String> keyvals = new HashMap<String, String>();
        String longline = "";
        String[] lines = opts.split(PeekReader.LINEBREAK);
        for (int i = 0; i < lines.length; i++) {
            longline += lines[i].trim();
        }

        lines = longline.split(",");
        for (int i = 0; i < lines.length; i++) {
            final String[] keyval = lines[i].trim().split("=");
            if (keyval.length > 1) {
                keyvals.put(keyval[0], keyval[1]);

                if ("title".equals(keyval[0])) {
                    keyvals.put("title", keyval[1]);
                } else if ("label".equals(keyval[0])) {
                    keyvals.put("label", keyval[1]);
                } else if ("caption".equals(keyval[0])) {
                    keyvals.put("caption", keyval[1]);
                }

            }
        }

        return keyvals;
    }
}
