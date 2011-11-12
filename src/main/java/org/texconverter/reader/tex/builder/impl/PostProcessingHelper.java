/**
 * $Revision: 1.2 $
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
 * created: 27.09.2006 tfrana
 */
package org.texconverter.reader.tex.builder.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.IndexMarkReference;
import org.texconverter.dom.Node;
import org.texconverter.dom.Referencable;
import org.texconverter.dom.Reference;
import org.texconverter.dom.Section;
import org.texconverter.dom.impl.BibliographyImpl;
import org.texconverter.dom.impl.BibliographyItemImpl;
import org.texconverter.dom.impl.BibliographyRefImpl;
import org.texconverter.dom.impl.CaptionImpl;
import org.texconverter.dom.impl.FloatingEnvironmentImpl;
import org.texconverter.dom.impl.GlossaryEntryImpl;
import org.texconverter.dom.impl.GlossaryImpl;
import org.texconverter.dom.impl.IndexEntryImpl;
import org.texconverter.dom.impl.IndexImpl;
import org.texconverter.dom.impl.IndexMarkImpl;
import org.texconverter.dom.impl.IndexMarkReferenceImpl;
import org.texconverter.dom.impl.NodeWithChildsImpl;
import org.texconverter.dom.impl.TextImpl;
import org.texconverter.io.PeekReader;
import org.texconverter.io.impl.FilePeekReader;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.Token;
import org.texconverter.resource.ResourceKeys;
import org.texconverter.resource.ResourceManager;

/**
 * @author tfrana
 */
public final class PostProcessingHelper {
    /**
     * Apache commons logging reference.
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(PostProcessingHelper.class);

    private static final Pattern RE_INDEXCMD = Pattern.compile("[^\"]\\|");

    private PostProcessingHelper() {
    }

    /**
     * Builds captions (from a localized name for the object the caption belongs
     * to, plus some kind of number or identifier from the actual object).
     * 
     * @param captions
     *            the captions
     * @param locale
     *            the locale the captions should use
     */
    public static void buildCaptions(final List<CaptionImpl> captions,
            final Locale locale) {
        if (captions != null) {
            Map<String, Integer> counters = new HashMap<String, Integer>();
            ResourceBundle localizedStrings = ResourceManager.getInstance()
                    .getLocalizedResource(ResourceManager.RES_STRINGS, locale);

            Iterator<CaptionImpl> it = captions.iterator();
            CaptionImpl caption;
            while (it.hasNext()) {
                caption = it.next();

                NodeWithChildsImpl floatEnv = caption
                        .getParent(FloatingEnvironmentImpl.class);
                if (floatEnv == null) {
                    LOGGER.error("finish -> Found a caption outside of a floating environment,"
                                    + " setting title \"ERROR - no caption target\"");

                    // caption.setTitle("ERROR - no caption target");
                } else {
                    String envName = ((FloatingEnvironmentImpl) floatEnv)
                            .getEnvironmentName();
                    if (envName.endsWith("*")) {
                        envName = envName.substring(0, envName.length() - 2);
                    }

                    int count = 1;
                    if (counters.containsKey(envName)) {
                        count = counters.get(envName);
                    }

                    StringBuffer captionPrefix = new StringBuffer();
                    if ("figure".equals(envName)) {
                        captionPrefix.append(localizedStrings
                                .getString(ResourceKeys.CAPTION_FIGURE
                                        .toString()));
                    } else if ("table".equals(envName)) {
                        captionPrefix.append(localizedStrings
                                .getString(ResourceKeys.CAPTION_TABLE
                                        .toString()));
                    } else if ("lstlisting".equals(envName)) {
                        captionPrefix.append(localizedStrings
                                .getString(ResourceKeys.CAPTION_LSTLISTING
                                        .toString()));
                    }

                    captionPrefix.append(" ").append(count).append(": ");
                    caption.getChildren().add(0,
                            new TextImpl(captionPrefix.toString()));

                    counters.put(envName, count + 1);

                    ((FloatingEnvironmentImpl) floatEnv).setCounter(Integer
                            .toString(count));
                }
            }
        }
    }

    /**
     * Links all given references.
     * 
     * @param refNodes
     *            the references to link
     * @param labels
     *            the map which maps all used labels to their
     *            {@link Referencable}s
     * @throws TexSyntaxException
     *             on tex syntax problems
     */
    public static void buildReferences(final List<Reference> refNodes,
            final Map<String, Referencable> labels) throws TexSyntaxException {
        final Iterator<Reference> it = refNodes.iterator();
        Reference ref;
        Referencable target;
        while (it.hasNext()) {
            ref = it.next();
            target = labels.get(ref.getReferencedLabel());
            if (target == null) {
                throw new TexSyntaxException(
                        "Found a reference to a non-existant label: "
                                + ref.getReferencedLabel());
            }
            ref.setReferencedNode(target);
        }
    }

    /**
     * Builds a glossary.
     * 
     * @param glossaryNode
     *            the glossary to fill
     * @param glossarList
     *            the glossary entries
     */
    public static void buildGlossary(final GlossaryImpl glossaryNode,
            final List<GlossaryEntryImpl> glossarList) {

        Collections.sort(glossarList);
        final Iterator<GlossaryEntryImpl> it = glossarList.iterator();
        while (it.hasNext()) {
            final GlossaryEntryImpl entry = it.next();
            glossaryNode.addChild(entry);
        }

    }

    /**
     * Build a bibliography.
     * 
     * @param bibliographyNode
     *            the bibliography to fill
     * @param bibRefNodes
     *            the bibliography references
     */
    public static void buildBibliography(
            final BibliographyImpl bibliographyNode,
            final List<BibliographyRefImpl> bibRefNodes) {
        BibliographyRefImpl bibRef;
        BibliographyItemImpl entry;
        final Iterator<BibliographyRefImpl> it = bibRefNodes.iterator();
        while (it.hasNext()) {
            bibRef = it.next();

            entry = bibliographyNode.getEntry(bibRef.getReferencedLabel());
            if (entry == null) {
                LOGGER.error("buildBibliography -> a bibliography reference references a non-existant entry with the key "
                                + bibRef.getReferencedLabel());
            } else {
                bibRef.setReferencedNode(entry);
            }
        }
    }

    /**
     * Builds the bibliography of a document which uses BibTex.
     * 
     * @param bibliographyNode
     *            the bibliography to fill
     * @param bibRefNodes
     *            the bibliography references
     * @param bibUsedCitationKeys
     *            the bibliography keys that were cited
     * @param bibTexStyle
     *            the bibliography style - the given style must be supported by
     *            the local BibTeX version
     * @param bibTexLibraryFile
     *            the library file which should be used to find citation keys
     * @param texFileBaseDir
     *            the base dir of the tex document - used to find the library
     *            file
     * @param texProjectName
     *            the name of the tex root file - this will be passed to BibTex
     * @param rootBuilder
     *            the root builder - used to generate the bibliography from the
     *            .bib file generated by BibTeX
     * @param toplevelSection
     *            the topmost section level occuring in the document
     * @param highestNumberedSectionLvl
     *            any section higher in hierarchy than this will not have
     *            numbers in their title anymore
     * @param sectionNumberingDepth
     *            starting from (including) highestNumberedSectionLvl, how many
     *            of the lower levels should still be numbered
     * @throws IOException
     *             on IO problems
     * @throws TexParserException
     *             on general tex parsing problems
     * @throws ConfigurationException
     *             on configuration problems
     */
    public static void buildBibliographyWithBibTex(
            BibliographyImpl bibliographyNode,
            final List<BibliographyRefImpl> bibRefNodes,
            final Set<String> bibUsedCitationKeys, final String bibTexStyle,
            final String bibTexLibraryFile, final File texFileBaseDir,
            final String texProjectName, final AbstractBuilder rootBuilder,
            final Section.Lvl toplevelSection,
            final Section.Lvl highestNumberedSectionLvl,
            final int sectionNumberingDepth) throws IOException,
            TexParserException, ConfigurationException {
        // create the ".aux" file needed by bibtex (only using the
        // commands necessary for bibtex)
        // \citation{j2meForum}
        // \bibstyle{alpha}
        // \bibdata{Literatur}

        StringBuffer auxBuffer = new StringBuffer("\\relax");
        Iterator<String> it = bibUsedCitationKeys.iterator();
        String citKey;
        while (it.hasNext()) {
            citKey = it.next();
            auxBuffer.append("\n\\citation{").append(citKey).append("}");
        }

        auxBuffer.append("\n\\bibstyle{").append(bibTexStyle).append("}");

        if (bibTexLibraryFile == null) {
            LOGGER.error("runBibTex -> no BibTeX libary file defined");
            throw new TexSyntaxException(
                    "There was no BibTex libary file defined");
        }
        auxBuffer.append("\n\\bibdata{").append(bibTexLibraryFile).append("}");

        File auxFile = new File(texFileBaseDir, texProjectName + ".aux");
        try {
            FileWriter fw = new FileWriter(auxFile);
            fw.write(auxBuffer.toString());
            fw.close();
        } catch (IOException e) {
            LOGGER.error("runBibTex -> Writing to file " + auxFile.getPath()
                    + " has failed, BibTex cannot be finished.", e);
            throw e;
        }

        // run bibtex, create bbl file
        Process bibtexProcess = Runtime.getRuntime().exec(
                "bibtex.exe " + texProjectName, null, texFileBaseDir);
        InputStream inputStream = bibtexProcess.getInputStream();
        int c = inputStream.read();
        StringBuffer sbBibtexOutput = new StringBuffer();
        while (c != -1) {
            sbBibtexOutput.append((char) c);
            c = inputStream.read();
        }
        inputStream.close();
        LOGGER.info("running BibTex to construct bibliography:\n"
                    + sbBibtexOutput.toString());

        // insert bbl file content
        PeekReader bblFileReader = new FilePeekReader(new File(texFileBaseDir,
                texProjectName + ".bbl"));

        NodeBuilder nodeBuilder = new NodeBuilder(rootBuilder, Token.NONE);
        rootBuilder.getTokenizer().insertInput(bblFileReader);
        // Ok, a bit tricky here. When we start the NodeBuilder, it will
        // (/should) read a "thebibliography" environment from the .bbl file,
        // constructing and adding a new bibliography. As this bibliography is
        // registered (and the root builder is the same), our attribute
        // bibliographyNode will be overwritten. We want to keep the old
        // reference however, because it points to the (currently empty)
        // bibliography node which is at the CORRECT position in the object
        // tree. In short we want the old, empty bibliography node for its
        // correct position, and the new for its correct content.

        bibliographyNode.getParent().getChildren();

        BibliographyImpl bibAtCorrectPos = bibliographyNode;
        nodeBuilder.build();
        bibAtCorrectPos.copyContentFrom(bibliographyNode);
        bibliographyNode = bibAtCorrectPos;

        // As this nodebuilder is not the root builder, it doesn't do any
        // postprocessing. However, the bibliography it should have constructed
        // was registered in the root builder module (this one) as it was added
        // to the tree. After this method, the normal bibliography
        // postprocessing takes place, and all is fine (hopefully).
        TeXDocFormatter formatter = new TeXDocFormatter();
        formatter.formatTextDoc(bibliographyNode, toplevelSection,
                highestNumberedSectionLvl, sectionNumberingDepth);

        buildBibliography(bibliographyNode, bibRefNodes);
    }

    /**
     * From a list of index marker strings, this will construct and add the
     * appropriate index entries to the index.
     * 
     * @param index
     *            the index to which the entries will be added
     * @param indexMarkers
     *            the index markers the index should refer to
     * @throws TexSyntaxException
     *             on tex syntax problems
     */
    public static void buildIndex(final IndexImpl index,
            final List<IndexMarkImpl> indexMarkers) throws TexSyntaxException {
        List<IndexEntryImpl> rootEntries = new ArrayList<IndexEntryImpl>();
        Map<String, IndexEntryImpl> entriesMap = new HashMap<String, IndexEntryImpl>();
        Map<String, IndexMarkReferenceImpl> incompleteRangeMarkRefs = new HashMap<String, IndexMarkReferenceImpl>();
        Map<String, Integer> refCounters = new HashMap<String, Integer>();

        IndexMarkReferenceImpl markRef;
        Iterator<IndexMarkImpl> it = indexMarkers.iterator();
        while (it.hasNext()) {
            IndexMarkImpl indexMark = it.next();
            String indexArg = indexMark.getLabel();

            // create reference to marker, handle cmd and cut it off
            Matcher m = RE_INDEXCMD.matcher(indexArg);
            if (m.find()) {
                String label = indexArg.substring(0, m.start() + 1);
                String cmd = indexArg.substring(m.end(), indexArg.length());

                if ("(".equals(cmd)) {
                    markRef = new IndexMarkReferenceImpl(indexMark, null);
                    incompleteRangeMarkRefs.put(label, markRef);
                    // keep cmd as indicator for range start in label (label
                    // needs to be different for range start and end)
                    indexMark.setLabel(indexArg);
                } else if (")".equals(cmd)) {
                    markRef = incompleteRangeMarkRefs.remove(label);
                    label = indexArg;
                    // need to set counter of stop mark
                    if (markRef != null) {
                        indexMark.setCounter(((IndexMarkImpl) markRef
                                .getReferencedNode()).getCounter());
                        indexMark.setLabel(indexArg);
                        indexMark.setCounter(((IndexMarkImpl) markRef
                                .getReferencedNode()).getCounter());
                        markRef.setReferencedNodeStop(indexMark);
                    } else {
                        LOGGER.error("buildIndex -> missing start detected for index range "
                                        + label + ", ignored");
                    }
                    continue;
                } else {
                    markRef = new IndexMarkReferenceImpl(indexMark);
                    // TODO other cmd stuff ignored atm

                    indexMark.setLabel(label);
                }

                // don't include any command in indexArg, as this will be used
                // to generate index entry names
                indexArg = label;
            } else {
                markRef = new IndexMarkReferenceImpl(indexMark);
            }

            IndexEntryImpl indexEntry = null, parentIndexEntry = null;
            String[] subKeys = indexArg.split("!");
            // get/create matching entry for indexMark & create missing entries
            // in the hierarchy
            StringBuffer key = new StringBuffer();
            for (int i = 0; i < subKeys.length; i++) {
                if (i > 0) {
                    key.append("!");
                }
                key.append(subKeys[i]);

                indexEntry = entriesMap.get(key.toString());
                if (indexEntry != null) {
                    // already exists, nothing to do but add
                    parentIndexEntry = indexEntry;
                } else {
                    // create new one
                    indexEntry = new IndexEntryImpl();
                    indexEntry.setLevel(i);
                    indexEntry.setTerm(subKeys[i]);

                    if (subKeys[i].contains("@")) {
                        String[] sortKeyAndLabel = subKeys[i].split("@");
                        indexEntry.setSortKey(sortKeyAndLabel[0]);
                    }

                    // put into object tree
                    if (parentIndexEntry != null) {
                        parentIndexEntry.addChild(indexEntry);
                    } else {
                        rootEntries.add(indexEntry);
                    }
                    parentIndexEntry = indexEntry;
                    entriesMap.put(key.toString(), indexEntry);
                }
            }

            Integer count = refCounters.get(key.toString());
            if (count == null) {
                count = 1;
            }
            indexMark.setCounter(count);
            refCounters.put(key.toString(), count + 1);
            markRef.setReferencedNode(indexMark);
            indexEntry.addReference(markRef);
        }

        // handle unclosed range index marks
        Iterator<String> itIncomplRefLabels = incompleteRangeMarkRefs.keySet()
                .iterator();
        while (itIncomplRefLabels.hasNext()) {
            String label = itIncomplRefLabels.next();
            IndexMarkReferenceImpl ref = incompleteRangeMarkRefs.get(label);
            LOGGER.error("buildIndex -> missing end index detected for index range "
                            + label + ", handling as non-range");
            ref.setType(IndexMarkReference.IndexRefType.SINGLE);
        }

        // sorting the entries
        List<Node> rootEntriesTmp = new ArrayList<Node>(rootEntries);
        IndexEntryImpl.sortEntries(rootEntriesTmp);
        index.getChildren().addAll(rootEntriesTmp);
    }
}
