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

import java.util.Iterator;
import java.util.List;

import org.texconverter.dom.Node;
import org.texconverter.dom.impl.AbstractNode;
import org.texconverter.dom.impl.BreakImpl;
import org.texconverter.dom.impl.CaptionImpl;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.FrameImpl;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.builder.impl.TeXDocumentBuilderImpl;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * @author tfrana
 */
public class FormattingHandler implements CommandHandler {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void handle(final Command cmd, final AbstractBuilder builder) {

        if ("caption".equals(cmd.getName())) {
            final CaptionImpl caption = new CaptionImpl();
            final List<Node> children = (List<Node>) cmd.getMandatoryArgs()
                    .get(0);
            caption.setChildren(children);

            // Node ref = builder.getCurrentParent().getParent(
            // FloatingEnvironment.class);
            //
            // if (ref != null) {
            // caption.belongsTo(ref);
            builder.consumeNode(caption, false);
            // }
        } else if ("\\".equals(cmd.getName())
                || "newline".equals(cmd.getName())) {
            final BreakImpl br = new BreakImpl();
            builder.consumeNode(br, false);
        } else if ("par".equals(cmd.getName())) {
            builder.startNewParagraph();
        } else if ("appendix".equals(cmd.getName())) {
            ((TeXDocumentBuilderImpl) builder.getRootBuilder())
                    .setUseAppendixNumbers(true);
        } else if ("parbox".equals(cmd.getName())) {
            // TODO parbox is just a paragraph currently
            builder.startNewParagraph();
            Iterator<AbstractNode> it = ((List<AbstractNode>) cmd
                    .getMandatoryArgs().get(1)).iterator();
            while (it.hasNext()) {
                builder.consumeNode(it.next(), false);
            }
            builder.startNewParagraph();
        } else if ("fbox".equals(cmd.getName())) {
            FrameImpl frameBox = new FrameImpl();
            frameBox.addAllChildren((List<AbstractNode>) cmd.getMandatoryArgs()
                    .get(0));
            builder.consumeNode(frameBox, false);
        }
    }

}
