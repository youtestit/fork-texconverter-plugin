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
 * created: 02.08.2006 tfrana
 */
package org.texconverter.reader.tex.handlers.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.Referencable;
import org.texconverter.dom.Reference.RefStyle;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.ReferenceImpl;
import org.texconverter.reader.tex.Helper;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * Handles Reference related commands.
 * 
 * @author tfrana
 */
public class ReferenceHandler implements CommandHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReferenceHandler.class);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void handle(final Command cmd, final AbstractBuilder builder) {

        final String label = Helper.getPlainTextFromNodeList((List<Node>) cmd
                .getMandatoryArgs().get(0));

        if ("label".equals(cmd.getName())) {
            Node ref = builder.getLastNode(Referencable.class);
            if (ref != null) {
                Referencable lastReferencable = (Referencable) ref;
                if (lastReferencable.getLabel() == null) {
                    lastReferencable.setLabel(label);
                    lastReferencable.setLabel(label);
                    builder.addLabel(label, lastReferencable);
                }
            } else {
                LOGGER.error("handle -> Could not determine target of label "
                        + label);
            }

        } else if ("ref".equals(cmd.getName())
                || "pageref".equals(cmd.getName())) {
            // We cannot determine target here as it might be a forward
            // reference. Instead this is done by the builder in a later step.
            RefStyle style;
            if ("ref".equals(cmd.getName())) {
                style = RefStyle.BY_REFTEXT;
            } else {
                style = RefStyle.BY_LINK;
            }
            ReferenceImpl ref = new ReferenceImpl(label, style);
            builder.consumeNode(ref, false);
        }
    }
}
