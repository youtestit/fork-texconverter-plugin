/**
 * $Revision: 1.5 $
 * $Date: 2006/09/28 13:13:40 $
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import org.texconverter.dom.AlignedNode;
import org.texconverter.dom.Node;
import org.texconverter.dom.Referencable;
import org.texconverter.dom.Reference;
import org.texconverter.dom.Section;
import org.texconverter.dom.TextContent;
import org.texconverter.dom.impl.AbstractNode;
import org.texconverter.dom.impl.AlignedNodeImpl;
import org.texconverter.dom.impl.BibliographyImpl;
import org.texconverter.dom.impl.BibliographyRefImpl;
import org.texconverter.dom.impl.CaptionImpl;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.dom.impl.GlossaryEntryImpl;
import org.texconverter.dom.impl.GlossaryImpl;
import org.texconverter.dom.impl.IndexImpl;
import org.texconverter.dom.impl.IndexMarkImpl;
import org.texconverter.dom.impl.NodeWithChildsImpl;
import org.texconverter.dom.impl.ParagraphImpl;
import org.texconverter.dom.impl.ReferenceImpl;
import org.texconverter.dom.impl.SectionImpl;
import org.texconverter.dom.impl.TextImpl;
import org.texconverter.dom.impl.TypeFaceImpl;
import org.texconverter.reader.tex.builder.EnvironmentBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.BasicParser;
import org.texconverter.reader.tex.parser.CmdDef;
import org.texconverter.reader.tex.parser.EnvDef;
import org.texconverter.reader.tex.parser.Token;
import org.texconverter.reader.tex.parser.Tokenizer;

/**
 * Parent class of all builders.
 * 
 * @author tfrana
 */
public abstract class AbstractBuilder implements EnvironmentBuilder {

    protected RootBuilderModule rootBuilderModule = null;

    protected Map<String, String> properties = null;

    protected Map<String, CmdDef> cmdDefs = null;

    protected Map<String, EnvDef> envDefs = null;

    protected AbstractBuilder rootBuilder;

    protected AbstractBuilder parentBuilder;

    protected NodeWithChildsImpl root;

    protected Tokenizer tokenizer;

    protected NodeWithChildsImpl currentParentNode;

    protected AbstractNode lastNode = null;

    protected boolean needWhitespace = false;

    protected boolean isFinished = false;

    /**
     * Labelname -> Node.
     */
    protected Map<String, Node> labels;

    protected List<Reference> refNodes;

    protected Token endingTokenId = Token.NONE;

    protected Stack<TypeFaceImpl> treeTypeFaces = new Stack<TypeFaceImpl>();

    protected Stack<AlignedNodeImpl> alignments = null;

    protected TypeFaceImpl currentTypeFace = null;

    protected AlignedNode alignment = null;

    protected StringBuffer plainTextBuffer = new StringBuffer();

    /**
     * no-arg constructor needed for instantiating via reflection.
     * Initialization is done via {@link #init(AbstractBuilder, EnvCommand)} and
     * {@link #init(AbstractBuilder, NodeWithChildsImpl, Token)}
     */
    protected AbstractBuilder() {

    }

    /**
     * Must be called as the first method after instantiation. This should be
     * ensured by only instantiating classes via
     * {@link org.texconverter.reader.tex.builder.impl.BuilderFactory#getEnvironmentBuilder(AbstractBuilder, EnvCommand)}.
     * Implementing subclasses must call
     * {@link #init(AbstractBuilder, NodeWithChildsImpl, Token)} in here.
     * 
     * @param theParentBuilder
     *            the parent environment builder
     * @param cmd
     *            the environment command
     * @throws IOException
     * @throws TexParserException
     * @throws ConfigurationException
     */
    protected abstract void init(AbstractBuilder theParentBuilder,
            EnvCommand cmd) throws IOException, TexParserException,
            ConfigurationException;

    /**
     * This method must be called by any implementing subclasses before calling
     * {@link #build()}.
     * 
     * @param theParentBuilder
     *            the parent environment builder
     * @param rootNode
     *            the root node
     * @param endTokenId
     *            the token indicating the end of the environment of this
     *            builder (if there is no such token, use {@link Token#NONE})
     */
    protected void init(final AbstractBuilder theParentBuilder,
            final NodeWithChildsImpl rootNode, final Token endTokenId) {
        this.parentBuilder = theParentBuilder;
        if (theParentBuilder != null) {
            rootBuilder = theParentBuilder.rootBuilder;
        } else {
            rootBuilder = this;
            rootBuilderModule = new RootBuilderModule(this);
        }
        setRootNode(rootNode);
        endingTokenId = endTokenId;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractNode build() throws TexSyntaxException, IOException,
            TexParserException, ConfigurationException {
        BasicParser.parse(this, endingTokenId);
        finish();
        return root;
    }

    /**
     * Is called after parsing is done, any finishing touches are to be made
     * here.
     * 
     * @throws IOException
     * @throws ConfigurationException
     * @throws TexParserException
     */
    protected void finish() throws IOException, TexParserException,
            ConfigurationException {
        if (plainTextBuffer.length() > 0) {
            final TextImpl txt = new TextImpl(plainTextBuffer.toString());
            plainTextBuffer.delete(0, plainTextBuffer.length());
            consumeNode(txt, false);
        }
        if (parentBuilder != null) {
            parentBuilder.lastNode = lastNode;
        } else if (rootBuilder == this) {
            rootBuilderModule.finish(root);
        }
    }

    /**
     * {@inheritDoc}
     */
    public NodeWithChildsImpl getRootNode() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractBuilder getRootBuilder() {
        return rootBuilder;
    }

    /**
     * Set the root node for the builder.
     * 
     * @param rootNode
     */
    protected void setRootNode(final NodeWithChildsImpl rootNode) {
        currentParentNode = rootNode;
        root = rootNode;
        lastNode = rootNode;
    }

    /**
     * {@inheritDoc}
     */
    public void consumeNode(final AbstractNode newNode,
            final boolean becomesNewParent) {

        if (!(plainTextBuffer.length() < 1)) {
            final TextImpl txt = new TextImpl(plainTextBuffer.toString());
            plainTextBuffer.delete(0, plainTextBuffer.length());
            consumeNode(txt, false);
        }

        if (newNode instanceof TypeFaceImpl
                && ((TypeFaceImpl) newNode).isBasic()) {
            changeBasicTypeFace((TypeFaceImpl) newNode);
        } else {
            if (newNode instanceof SectionImpl) {
                rootBuilder.rootBuilderModule
                        .registerSection((SectionImpl) newNode);
            } else if (newNode instanceof GlossaryImpl) {
                rootBuilder.rootBuilderModule
                        .setGlossaryNode((GlossaryImpl) newNode);
            } else if (newNode instanceof BibliographyImpl) {
                rootBuilder.rootBuilderModule
                        .setBibliographyNode((BibliographyImpl) newNode);
            } else if (newNode instanceof BibliographyRefImpl) {
                rootBuilder.rootBuilderModule
                        .registerBibReference((BibliographyRefImpl) newNode);
            } else if (newNode instanceof ReferenceImpl) {
                rootBuilder.rootBuilderModule
                        .registerReference((Reference) newNode);
            } else if (newNode instanceof CaptionImpl) {
                rootBuilder.rootBuilderModule
                        .registerCaption((CaptionImpl) newNode);
            } else if (newNode instanceof IndexMarkImpl) {
                rootBuilder.rootBuilderModule
                        .registerIndexMark((IndexMarkImpl) newNode);
            } else if (newNode instanceof IndexImpl) {
                rootBuilder.rootBuilderModule
                        .registerIndex((IndexImpl) newNode);
            }

            // becomes parent of the new node (if the new node is added)
            NodeWithChildsImpl parentOfNewNode = currentParentNode;
            NodeWithChildsImpl parentNode = currentParentNode;

            if (newNode.canEndNodes()) {
                while (parentNode != null
                        && !isNodeOutOfScope(parentNode, newNode)) {
                    if (parentNode.isEndedBy(newNode)) {
                        parentOfNewNode = parentNode.getParent();
                    }
                    parentNode = parentNode.getParent();
                }
            }

            if (parentOfNewNode != currentParentNode) {
                // if we moved up in the tree
                // check if there are typeFaces to drag
                parentNode = currentParentNode;
                final Stack<TypeFaceImpl> endedTypeFaces = new Stack<TypeFaceImpl>();
                while (parentNode != parentOfNewNode) {
                    if (parentNode instanceof TypeFaceImpl
                            && ((TypeFaceImpl) parentNode).isBasic()) {
                        endedTypeFaces.push((TypeFaceImpl) parentNode);
                    }
                    parentNode = parentNode.getParent();
                }

                if (!endedTypeFaces.empty()) {
                    final TypeFaceImpl draggedTypeFace = new TypeFaceImpl();
                    while (!endedTypeFaces.empty()) {
                        draggedTypeFace.overwritingMergeWith(endedTypeFaces
                                .pop());
                    }
                    parentOfNewNode.addChild(draggedTypeFace);
                    parentOfNewNode = draggedTypeFace;
                }
            }

            parentOfNewNode.addChild(newNode);
            lastNode = newNode;
            // TODO this is still a bit awkward, and likely incomplete
            needWhitespace = newNode instanceof TextContent
                    || (needWhitespace
                            && (newNode instanceof AlignedNodeImpl || newNode instanceof TypeFaceImpl) && ((NodeWithChildsImpl) newNode)
                            .getNumChildren() > 0);

            if (becomesNewParent) {
                currentParentNode = (NodeWithChildsImpl) newNode;
            } else {
                currentParentNode = parentOfNewNode;
            }
        }
    }

    protected boolean isNodeOutOfScope(final NodeWithChildsImpl parentNode,
            final AbstractNode newNode) {
        return parentNode.equals(root);
    }

    /**
     * Starts a new paragraph.
     */
    public void startNewParagraph() {
        if (!(lastNode instanceof ParagraphImpl
                && ((ParagraphImpl) lastNode).getNumChildren() == 0 && plainTextBuffer
                .length() == 0)) {
            final ParagraphImpl p = new ParagraphImpl();
            consumeNode(p, true);
        }
        needWhitespace = false;
    }

    /**
     * Appends whitespace.
     */
    public void addWhitespace() {
        if (needWhitespace) {
            plainTextBuffer.append(" ");
            needWhitespace = false;
        }
    }

    /**
     * Append some plain text to the document.
     * 
     * @param txt
     *            the text
     */
    public void appendText(final String txt) {
        plainTextBuffer.append(txt);
        needWhitespace = true;
    }

    /**
     * Set the builder to status finished.
     */
    public void setFinished() {
        // currentParentNode = null;
        isFinished = true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * {@inheritDoc}
     */
    public NodeWithChildsImpl getCurrentParent() {
        return currentParentNode;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Node getLastNode(final Class theClass) {
        Node lastNodeOfClass = null;

        NodeWithChildsImpl parent = currentParentNode;
        while (parent != null && lastNodeOfClass == null) {
            for (int i = parent.getNumChildren() - 1; i >= 0; i--) {
                if (theClass.isAssignableFrom(parent.getChild(i).getClass())) {
                    lastNodeOfClass = parent.getChild(i);
                    break;
                }
            }
            if (theClass.isAssignableFrom(parent.getClass())) {
                lastNodeOfClass = parent;
            }

            parent = parent.getParent();
        }

        return lastNodeOfClass;
    }

    /**
     * {@inheritDoc}
     */
    public Node getLabelledNode(final String labelName) {
        if (rootBuilder.labels.containsKey(labelName)) {
            return rootBuilder.labels.get(labelName);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public CmdDef getCmdDef(final String cmdName) {
        if (cmdDefs != null && cmdDefs.containsKey(cmdName)) {
            return cmdDefs.get(cmdName);
        } else if (parentBuilder != null) {
            return parentBuilder.getCmdDef(cmdName);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public EnvDef getEnvDef(final String envName) {
        if (envDefs != null && envDefs.containsKey(envName)) {
            return envDefs.get(envName);
        } else if (parentBuilder != null) {
            return parentBuilder.getEnvDef(envName);
        }
        return null;
    }

    /**
     * Add a command definition to this builder.
     * 
     * @param cmdName
     *            name of the command
     * @param cmdDef
     *            the command definition
     */
    public void addCmdDef(final String cmdName, final CmdDef cmdDef) {
        if (cmdDefs == null) {
            cmdDefs = new HashMap<String, CmdDef>();
        }
        cmdDefs.put(cmdName, cmdDef);
    }

    /**
     * Add an environment definition to this builder.
     * 
     * @param envName
     *            name of the environment
     * @param envDef
     *            the environment definition
     */
    public void addEnvDef(final String envName, final EnvDef envDef) {
        if (envDefs == null) {
            envDefs = new HashMap<String, EnvDef>();
        }
        envDefs.put(envName, envDef);
    }

    /**
     * Use this map of command definitions for this builder. Any prior
     * registered command definitions will be overwritten.
     * 
     * @param cmdDefs
     *            the command definitions
     */
    public void setCmdDefs(final Map<String, CmdDef> cmdDefs) {
        this.cmdDefs = cmdDefs;
    }

    /**
     * Use this map of environment definitions for this builder. Any prior
     * registered environment definitions will be overwritten.
     * 
     * @param envDefs
     *            the environment definitions
     */
    public void setEnvDefs(final Map<String, EnvDef> envDefs) {
        this.envDefs = envDefs;
    }

    /**
     * {@inheritDoc}
     */
    public String getValue(final String key) {
        if (properties != null && properties.containsKey(key)) {
            return properties.get(key);
        } else if (parentBuilder != null) {
            return parentBuilder.getValue(key);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final String key, final String value) {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
        properties.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public String getGlobalValue(final String key) {
        return rootBuilder.getValue(key);
    }

    /**
     * {@inheritDoc}
     */
    public void setGlobalValue(final String key, final String value) {
        rootBuilder.setValue(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public Tokenizer getTokenizer() {
        Tokenizer tok;
        if (tokenizer == null) {
            tok = parentBuilder.getTokenizer();
        } else {
            tok = tokenizer;
        }
        return tok;
    }

    /**
     * Change the <b>basic</b> typeface of the document. "Basic" in this
     * context means that it will apply to all future content in this builder's
     * scope, unless replaced by a concurrent basic TypeFace command.
     * 
     * @param typeFace
     *            the typeface
     */
    public void changeBasicTypeFace(final TypeFaceImpl typeFace) {
        NodeWithChildsImpl parentOfTypeFace;
        if (currentParentNode instanceof TypeFaceImpl) {
            final TypeFaceImpl parentTypeFace = (TypeFaceImpl) currentParentNode;
            if (currentParentNode.getNumChildren() == 0
                    && ((TypeFaceImpl) currentParentNode).mergeWith(typeFace)) {
                parentOfTypeFace = null;
            } else if (parentTypeFace.isOverriddenBy(typeFace)) {
                parentOfTypeFace = parentTypeFace.getParent();
            } else {
                parentOfTypeFace = parentTypeFace;
                // eliminate superfluous attributes
                // unnecessary!
                // if (typeFace.getFontFamily() ==
                // parentTypeFace.getFontFamily()) {
                // typeFace.setFontFamily(null);
                // }
                // if (typeFace.getFontSeries() ==
                // parentTypeFace.getFontSeries()) {
                // typeFace.setFontSeries(null);
                // }
                // if (typeFace.getFontShape() == parentTypeFace.getFontShape())
                // {
                // typeFace.setFontShape(null);
                // }
                // if (typeFace.getFontSize() == parentTypeFace.getFontSize()) {
                // typeFace.setFontSize(null);
                // }
            }
        } else {
            parentOfTypeFace = currentParentNode;
        }

        if (parentOfTypeFace != null) {
            parentOfTypeFace.addChild(typeFace);
            currentParentNode = typeFace;
        }
    }

    /**
     * @param labelname
     *            the name of the label
     * @param referencedNode
     *            the node to which the label is applied
     */
    public void addLabel(final String labelname,
            final Referencable referencedNode) {
        rootBuilder.rootBuilderModule.addLabel(labelname, referencedNode);
    }

    /**
     * Affects the section numbering if set to true (usage of letters for
     * toplevel sections).
     * 
     * @param useAppendixNumbers
     *            if appendix "numbers" should be used
     */
    public void setUseAppendixNumbers(final boolean useAppendixNumbers) {
        rootBuilder.rootBuilderModule.setUseAppendixNumbers(useAppendixNumbers);
    }

    /**
     * Adds an entry to the bibliography (if it is/was added at some point).
     * 
     * @param bibKey
     *            The key of the bibliography entry.
     */
    public void addBibEntry(final String bibKey) {
        rootBuilder.rootBuilderModule.addBibEntry(bibKey);
    }

    /**
     * @param glossaryEntry
     *            The entry to add to the glossary.
     */
    public void addGlossaryEntry(final GlossaryEntryImpl glossaryEntry) {
        rootBuilder.rootBuilderModule.addGlossaryEntry(glossaryEntry);
    }

    /**
     * @return the directory the basic TeX file is residing in
     */
    public File getTexFileBaseDir() {
        return rootBuilder.rootBuilderModule.getTexFileBaseDir();
    }

    /**
     * @param texFileBaseDir
     *            the directory the basic TeX file is residing in
     */
    public void setTexFileBaseDir(final File texFileBaseDir) {
        rootBuilder.rootBuilderModule.setTexFileBaseDir(texFileBaseDir);
    }

    /**
     * @return the name of the tex project (the name of the basic TeX file
     *         without file extension)
     */
    public String getTexProjectName() {
        return rootBuilder.rootBuilderModule.getTexProjectName();
    }

    /**
     * @param texProjectName
     *            the name of the tex project (the name of the basic TeX file
     *            without file extension)
     */
    public void setTexProjectName(final String texProjectName) {
        rootBuilder.rootBuilderModule.setTexProjectName(texProjectName);
    }

    /**
     * @param bibTexLibraryFile
     *            the library (".bib") file for BibTex
     */
    public void setBibTexLibraryFile(final String bibTexLibraryFile) {
        rootBuilder.rootBuilderModule.setBibTexLibraryFile(bibTexLibraryFile);
    }

    /**
     * @param bibTexStyle
     *            the bibtex style
     */
    public void setBibTexStyle(final String bibTexStyle) {
        rootBuilder.rootBuilderModule.setBibTexStyle(bibTexStyle);
    }

    /**
     * @param doBibTexPostprocessing
     *            if BibTex postprocessing is required for constructing the
     *            bibliography.
     */
    public void setDoBibTexPostprocessing(final boolean doBibTexPostprocessing) {
        rootBuilder.rootBuilderModule
                .setDoBibTexPostprocessing(doBibTexPostprocessing);
    }

    /**
     * @return the locale which should be used for localized resources
     */
    public Locale getLocale() {
        return rootBuilder.rootBuilderModule.getLocale();
    }

    /**
     * @return the highest section level which should still be numbered
     */
    public Section.Lvl getHighestNumberedSectionLvl() {
        return rootBuilder.rootBuilderModule.getHighestNumberedSectionLvl();
    }

    /**
     * @return how many sections below the highest numbered section level should
     *         also be numbered
     */
    public int getSectionNumberingDepth() {
        return rootBuilder.rootBuilderModule.getSectionNumberingDepth();
    }
}
