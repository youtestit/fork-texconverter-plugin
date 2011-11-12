/**
 * $Revision: 1.4 $
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
 * created: 09.08.2006 tfrana
 */
package org.texconverter.reader.tex.builder.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.texconverter.dom.NodeWithChilds;
import org.texconverter.dom.Referencable;
import org.texconverter.dom.Reference;
import org.texconverter.dom.Section;
import org.texconverter.dom.impl.BibliographyImpl;
import org.texconverter.dom.impl.BibliographyRefImpl;
import org.texconverter.dom.impl.CaptionImpl;
import org.texconverter.dom.impl.GlossaryEntryImpl;
import org.texconverter.dom.impl.GlossaryImpl;
import org.texconverter.dom.impl.IndexImpl;
import org.texconverter.dom.impl.IndexMarkImpl;
import org.texconverter.dom.impl.SectionImpl;
import org.texconverter.reader.tex.Helper;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;

/**
 * @author tfrana
 */
public class RootBuilderModule {

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory
            .getLog(RootBuilderModule.class);

    /**
     * Any section lower in hierarchy than this will not have numbers in their
     * title anymore.
     */
    protected static final Section.Lvl SECTIONNUMBERINGTHRESHOLD = Section.Lvl.SUBSUBSECTION;

    private static final int DEFAULT_SECTIONNUMBERINGDEPTH = 3;

    private AbstractBuilder rootBuilder;

    private Section.Lvl toplevelSection = null;

    private final int[] sectionCounters = new int[Section.Lvl.values().length];

    private boolean useAppendixNumbers = false;

    private List<GlossaryEntryImpl> glossarList = null;

    private GlossaryImpl glossaryNode = null;

    private BibliographyImpl bibliographyNode = null;

    private IndexImpl indexNode = null;

    private boolean doBibTexPostprocessing = false;

    private String bibTexLibraryFile = null;

    /** Default style is "plain". */
    private String bibTexStyle = "plain";

    private Map<String, Referencable> labels = null;

    private List<CaptionImpl> captions = null;

    private List<Reference> refNodes = null;

    private List<BibliographyRefImpl> bibRefNodes = null;

    private Set<String> bibUsedCitationKeys = null;

    private File texFileBaseDir = null;

    private String texProjectName = null;

    private Section.Lvl highestNumberedSectionLvl = Section.Lvl.CHAPTER;

    private int sectionNumberingDepth = DEFAULT_SECTIONNUMBERINGDEPTH;

    private Locale locale;

    private Set<String> indexKeys = null;

    private List<IndexMarkImpl> indexMarkers = new ArrayList<IndexMarkImpl>();

    /**
     * @param rootBuilder
     *            the root builder
     * @param locale
     *            the locale which should be used for localized resources
     */
    public RootBuilderModule(final AbstractBuilder rootBuilder,
            final Locale locale) {
        this.rootBuilder = rootBuilder;
        this.locale = locale;
    }

    /**
     * This constructor uses the default locale for localized resources.
     * 
     * @param rootBuilder
     *            the root builder
     */
    public RootBuilderModule(final AbstractBuilder rootBuilder) {
        this(rootBuilder, Locale.getDefault());
    }

    /**
     * Counts & sets number of section and checks which is the topmost section
     * level.
     * 
     * @param section
     *            the section
     */
    public void registerSection(final SectionImpl section) {
        final Section.Lvl lvl = section.getLevel();

        if (lvl.compareTo(highestNumberedSectionLvl) >= 0) {
            // check for toplevel section
            if (toplevelSection == null) {
                toplevelSection = lvl;
            } else if (lvl.compareTo(toplevelSection) < 0) {
                toplevelSection = lvl;
            }

            // set number
            if (lvl.compareTo(SECTIONNUMBERINGTHRESHOLD) <= 0) {
                sectionCounters[lvl.ordinal()]++;

                String number;
                if (useAppendixNumbers && lvl == toplevelSection) {
                    number = Helper
                            .translateNumberToLetters(sectionCounters[lvl
                                    .ordinal()]);
                } else {
                    number = Integer.toString(sectionCounters[lvl.ordinal()]);
                }

                section.setCounter(number);

                // reset numbers of lower levels
                for (int i = lvl.ordinal() + 1; i < sectionCounters.length; i++) {
                    sectionCounters[i] = 0;
                }
            }
        }
    }

    /**
     * Affects the section numbering if set to true (usage of letters for
     * toplevel sections).
     * 
     * @param useAppendixNumbers
     *            if to use appendix "numbers"
     */
    public void setUseAppendixNumbers(final boolean useAppendixNumbers) {
        for (int i = toplevelSection.ordinal(); i < sectionCounters.length; i++) {
            sectionCounters[i] = 0;
        }
        this.useAppendixNumbers = useAppendixNumbers;
    }

    /**
     * @param bibliographyNode
     *            the bibliography node of the document
     */
    public void setBibliographyNode(final BibliographyImpl bibliographyNode) {
        this.bibliographyNode = bibliographyNode;
    }

    /**
     * @param indexNode
     *            the index node of the document
     */
    public void setIndexNode(final IndexImpl indexNode) {
        this.indexNode = indexNode;
    }

    /**
     * @param glossaryNode
     *            the glossary node of the document
     */
    public void setGlossaryNode(final GlossaryImpl glossaryNode) {
        this.glossaryNode = glossaryNode;
    }

    /**
     * @param glossaryEntry
     *            the glossary entry to add
     */
    public void addGlossaryEntry(final GlossaryEntryImpl glossaryEntry) {
        if (glossarList == null) {
            glossarList = new ArrayList<GlossaryEntryImpl>();
        }

        glossarList.add(glossaryEntry);
    }

    /**
     * Register a labelled node.
     * 
     * @param labelname
     *            name of the label
     * @param referencedNode
     *            node referenced by the labelname
     */
    public void addLabel(final String labelname,
            final Referencable referencedNode) {
        if (labels == null) {
            labels = new HashMap<String, Referencable>();
        }
        labels.put(labelname, referencedNode);
    }

    /**
     * @param referenceNode
     *            the reference node
     */
    public void registerReference(final Reference referenceNode) {
        if (refNodes == null) {
            refNodes = new ArrayList<Reference>();
        }
        refNodes.add(referenceNode);
    }

    /**
     * Register a bibliography reference, to be handled in postprocessing. The
     * corresponding entry will automatically be added to the bibliography, a
     * separate call to {@link #addBibEntry(String)} is not necessary (but will
     * do no harm).
     * 
     * @param bibRefNode
     *            the bibliography reference node
     */
    public void registerBibReference(final BibliographyRefImpl bibRefNode) {
        if (bibRefNodes == null) {
            bibRefNodes = new ArrayList<BibliographyRefImpl>();
        }
        bibRefNodes.add(bibRefNode);
        addBibEntry(bibRefNode.getReferencedLabel());
    }

    /**
     * Notify that a certain bibliography entry has been used. The entry
     * belonging to the key will be added to the bibliography.
     * 
     * @param bibKey
     *            the bibliography key
     */
    public void addBibEntry(final String bibKey) {
        if (bibUsedCitationKeys == null) {
            bibUsedCitationKeys = new HashSet<String>();
        }
        bibUsedCitationKeys.add(bibKey);
    }

    /**
     * Build the glossary and bibliography, final formatting, etc.
     * 
     * @param documentRootNode
     *            the root node of the document
     * @throws IOException
     *             on IO problems
     * @throws ConfigurationException
     *             on configuration problems
     * @throws TexParserException
     *             on general parser problems
     */
    public void finish(final NodeWithChilds documentRootNode)
            throws IOException, TexParserException, ConfigurationException {
        // final formating of the object tree (replacements, breaks)
        final TeXDocFormatter formatter = new TeXDocFormatter();
        formatter.formatTextDoc(documentRootNode, toplevelSection,
                highestNumberedSectionLvl, sectionNumberingDepth);

        PostProcessingHelper.buildCaptions(captions, locale);

        // set the target node for each Reference node (do when document is
        // finished because of possible forward references)

        if (refNodes != null) {
            PostProcessingHelper.buildReferences(refNodes, labels);
        }

        if (glossaryNode != null && glossarList != null) {
            PostProcessingHelper.buildGlossary(glossaryNode, glossarList);
        }

        if (bibliographyNode != null) {
            if (doBibTexPostprocessing) {
                PostProcessingHelper.buildBibliographyWithBibTex(
                        bibliographyNode, bibRefNodes, bibUsedCitationKeys,
                        bibTexStyle, bibTexLibraryFile, texFileBaseDir,
                        texProjectName, rootBuilder, toplevelSection,
                        highestNumberedSectionLvl, sectionNumberingDepth);
            } else {
                PostProcessingHelper.buildBibliography(bibliographyNode,
                        bibRefNodes);
            }
        }

        if (indexNode != null) {
            PostProcessingHelper.buildIndex(indexNode, indexMarkers);
        }
    }

    /**
     * @param index
     *            the index
     */
    public void registerIndex(final IndexImpl index) {
        indexNode = index;
    }

    /**
     * @param indexMark
     *            an index mark (denoting a place in the document which is
     *            referred by the index)
     */
    public void registerIndexMark(final IndexMarkImpl indexMark) {
        indexMarkers.add(indexMark);
    }

    /**
     * @return the directory the basic TeX file is residing in
     */
    public File getTexFileBaseDir() {
        return texFileBaseDir;
    }

    /**
     * @param texFileBaseDir
     *            the directory the basic TeX file is residing in
     */
    public void setTexFileBaseDir(final File texFileBaseDir) {
        this.texFileBaseDir = texFileBaseDir;
    }

    /**
     * @return the name of the tex project (the name of the basic TeX file
     *         without file extension)
     */
    public String getTexProjectName() {
        return texProjectName;
    }

    /**
     * @param texProjectName
     *            the name of the tex project (the name of the basic TeX file
     *            without file extension)
     */
    public void setTexProjectName(final String texProjectName) {
        this.texProjectName = texProjectName;
    }

    /**
     * @param bibTexLibraryFile
     *            the library (".bib") file for BibTex
     */
    public void setBibTexLibraryFile(final String bibTexLibraryFile) {
        this.bibTexLibraryFile = bibTexLibraryFile;
    }

    /**
     * @param bibTexStyle
     *            the bibtex style
     */
    public void setBibTexStyle(final String bibTexStyle) {
        this.bibTexStyle = bibTexStyle;
    }

    /**
     * @param doBibTexPostprocessing
     *            if BibTex postprocessing is required for constructing the
     *            bibliography.
     */
    public void setDoBibTexPostprocessing(final boolean doBibTexPostprocessing) {
        this.doBibTexPostprocessing = doBibTexPostprocessing;
    }

    /**
     * @return the locale any localized resources should use
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Set the locale. The locale is used for things like the title used for a
     * glossary or bibliography node, captions referencing tables, listings,
     * etc.
     * 
     * @param locale
     *            the locale
     */
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    /**
     * Register caption for postprocessing.
     * 
     * @param caption
     *            the caption
     */
    public void registerCaption(final CaptionImpl caption) {
        if (captions == null) {
            captions = new ArrayList<CaptionImpl>();
        }
        captions.add(caption);
    }

    /**
     * @return the highest section level which should still be numbered
     */
    public Section.Lvl getHighestNumberedSectionLvl() {
        return highestNumberedSectionLvl;
    }

    /**
     * @return how many sections below the highest numbered section level should
     *         also be numbered
     */
    public int getSectionNumberingDepth() {
        return sectionNumberingDepth;
    }
}
