/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:37 $
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
package org.texconverter.reader.tex.builder;

import java.io.IOException;

import org.texconverter.dom.Node;
import org.texconverter.dom.impl.AbstractNode;
import org.texconverter.dom.impl.NodeWithChildsImpl;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.CmdDef;
import org.texconverter.reader.tex.parser.EnvDef;
import org.texconverter.reader.tex.parser.Tokenizer;

/**
 * Interface for environment builders. Not intended for use by renderers. Use
 * {@link org.texconverter.reader.tex.builder.impl.BuilderFactory} to create an
 * instance.
 * 
 * @author tfrana
 */
public interface EnvironmentBuilder {

    /**
     * Start parsing of the environment.
     * 
     * @return the node constructed from the environment
     * @throws TexSyntaxException
     *             on TeX syntax problems
     * @throws IOException
     *             on IO problems
     * @throws TexParserException
     *             on general parser problems
     * @throws ConfigurationException
     *             on configuration problems
     */
    AbstractNode build() throws TexSyntaxException, IOException,
            TexParserException, ConfigurationException;

    /**
     * @return the root node of this builder
     */
    NodeWithChildsImpl getRootNode();

    /**
     * @return the root builder (should be TeXDocumentBuilder)
     */
    AbstractBuilder getRootBuilder();

    /**
     * Consume a new node into the current builder's tree.
     * 
     * @param newNode
     *            the new node
     * @param becomesNewParent
     *            if this node will become the new currentParent
     */
    void consumeNode(AbstractNode newNode, boolean becomesNewParent);

    /**
     * @return if this builder is finished (its environment has ended)
     */
    boolean isFinished();

    /**
     * @return the node which is the current parent
     */
    NodeWithChildsImpl getCurrentParent();

    /**
     * Get a labelled node.
     * 
     * @param labelName
     *            the labelname
     * @return the node labelled with this name, or <code>null</code> if this
     *         label isn't known
     */
    Node getLabelledNode(String labelName);

    /**
     * Get a command definition ({@link CmdDef}).
     * 
     * @param cmdName
     *            the name of the command
     * @return the CmdDef
     */
    CmdDef getCmdDef(String cmdName);

    /**
     * Get an environment definition ({@link EnvDef}).
     * 
     * @param envName
     *            the name of the environment
     * @return the EnvDef
     */
    EnvDef getEnvDef(String envName);

    /**
     * Get a property. If it is not found in this builder, the parent builders
     * up to the root builder will be asked.
     * 
     * @param key
     *            key of the property
     * @return value of the property or <code>null</code> if it doesn't exist
     */
    String getValue(String key);

    /**
     * Store a property in this builder. Use for properties which should not be
     * visible or have no use beyond the scope of this builder.
     * 
     * @param key
     *            key of the property
     * @param value
     *            value of the property
     */
    void setValue(String key, String value);

    /**
     * Get a property from the root builder.
     * 
     * @param key
     *            key of the property
     * @return value of the property or <code>null</code> if it doesn't exist
     */
    String getGlobalValue(String key);

    /**
     * Store a property in the root builder.
     * 
     * @param key
     *            key of the property
     * @param value
     *            value of the property
     */
    void setGlobalValue(String key, String value);

    /**
     * @return the tokenizer of this builder
     */
    Tokenizer getTokenizer();

    /**
     * Find the last node in the tree which is assignable to theClass.
     * 
     * @param theClass
     *            the class
     * @return the node
     */
    Node getLastNode(Class theClass);
}
