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

import java.util.List;

import org.texconverter.dom.AlignedNode;
import org.texconverter.dom.Node;
import org.texconverter.dom.impl.AlignedNodeImpl;
import org.texconverter.dom.impl.Command;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * @author tfrana
 */
public class AlignmentHandler implements CommandHandler {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void handle(final Command cmd, final AbstractBuilder builder) {
        AlignedNodeImpl alignedNode = new AlignedNodeImpl();

        boolean becomesNewParent = true;
        if ("centering".equals(cmd.getName())) {
            alignedNode.setAlignment(AlignedNode.Alignment.CENTER);
        } else if ("raggedleft".equals(cmd.getName())) {
            alignedNode.setAlignment(AlignedNode.Alignment.LEFT);
        } else if ("raggedright".equals(cmd.getName())) {
            alignedNode.setAlignment(AlignedNode.Alignment.RIGHT);
        } else if ("centerline".equals(cmd.getName())) {
            alignedNode.setAlignment(AlignedNode.Alignment.CENTER);
            becomesNewParent = false;
            final List<Node> nodes = (List<Node>) cmd.getMandatoryArgs().get(0);
            alignedNode.getChildren().addAll(nodes);
        } else {
            alignedNode = null;
        }

        if (alignedNode != null) {
            builder.consumeNode(alignedNode, becomesNewParent);
        }
    }
}
