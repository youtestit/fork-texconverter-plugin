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

import org.texconverter.dom.impl.AbstractNode;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.dom.impl.NodeWithChildsImpl;
import org.texconverter.dom.impl.SectionImpl;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.Token;

/**
 * Has no own rootNode, but passes all nodes to consume to its parent builder
 * instead. Used for free-standing blocks ({...}). Basically its only use is the
 * storing of variables, which shouldn't be visible outside of the free block.
 * Processes all nodes until endsOnToken (constructor arg) is encountered. The
 * build method always returns null, as this builder doesn't construct anything
 * on its own.
 * 
 * @author tfrana
 */
public class EmptyBuilder extends AbstractBuilder {

    /**
     * Empty constructor, the {@link #init(AbstractBuilder, EnvCommand)} method
     * has to be called before any further usage.
     */
    public EmptyBuilder() {
        super();
    }

    /**
     * Constructor with arguments for shorter non-reflection instantiation. /**
     * 
     * @param parentBuilder
     *            The parent builder.
     * @param endingTokenId
     *            The token ending this builder or {@link Token#NONE}.
     */
    public EmptyBuilder(final AbstractBuilder parentBuilder,
            final Token endingTokenId) {
        super();
        plainTextBuffer = parentBuilder.plainTextBuffer;
        super.init(parentBuilder, parentBuilder.getCurrentParent(),
                endingTokenId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init(final AbstractBuilder parentBuilder,
            final EnvCommand cmd) throws IOException, TexParserException,
            ConfigurationException {
        plainTextBuffer = parentBuilder.plainTextBuffer;
        super.init(parentBuilder, parentBuilder.getCurrentParent(),
                endingTokenId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNode build() throws TexSyntaxException, IOException,
            TexParserException, ConfigurationException {
        super.build();
        finish();
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void consumeNode(final AbstractNode newNode,
            final boolean becomesNewParent) {
        if (newNode instanceof SectionImpl) {
            // If this is a section, it can end the root node, which is the
            // currentParentNode of the parentBuilder, as section commands are
            // not bound to scopes. If this happens, it is our new root node,
            // and will be the new currentParentNode of the parentBuilder once
            // this builder is finished.
            SectionImpl oldParentSection = null;
            if (root instanceof SectionImpl) {
                oldParentSection = (SectionImpl) root;
            } else {
                final Object o = root.getParent(SectionImpl.class);
                if (o != null) {
                    oldParentSection = (SectionImpl) o;
                }
            }

            if (oldParentSection != null
                    && ((SectionImpl) newNode).getLevel().compareTo(
                            oldParentSection.getLevel()) <= 0) {
                root = oldParentSection.getParent();
            }
        }
        super.consumeNode(newNode, becomesNewParent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isNodeOutOfScope(final NodeWithChildsImpl parentNode,
            final AbstractNode newNode) {
        // as the root belongs to the parent builder, it can not be ended inside
        // this builder -> it is out of scope. But Section nodes can travel
        // beyond.

        return !(newNode instanceof SectionImpl)
                && super.isNodeOutOfScope(parentNode, newNode);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IOException
     * @throws ConfigurationException
     * @throws TexParserException
     */
    @Override
    protected void finish() throws IOException, TexParserException,
            ConfigurationException {
        super.finish();
        // if our root node has changed due to a section change,
        // notify the parent builder of the new current parent node
        parentBuilder.currentParentNode = root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeWithChildsImpl getRootNode() {
        return parentBuilder.getRootNode();
    }

}
