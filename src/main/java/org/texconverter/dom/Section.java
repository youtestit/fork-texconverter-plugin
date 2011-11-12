/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:21 $
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
package org.texconverter.dom;

import java.util.List;

/**
 * Interface for sections.
 * 
 * @author tfrana
 */
public interface Section extends NodeWithChilds, Referencable, TitledNode {

    /**
     * The different levels that exist for sections.
     * 
     * @author tfrana
     */
    public static enum Lvl {
        /**
         * The topmost level available, used mainly for books. This level has no
         * influence on the numbering of any child sections
         */
        PART,
        /**  */
        CHAPTER,
        /** Section level, commonly the topmost level for e.g. articles */
        SECTION,
        /**  */
        SUBSECTION,
        /**  */
        SUBSUBSECTION,
        /**  */
        PARAGRAPH,
        /**  */
        SUBPARAGRAPH;
    }

    /**
     * @return the level of this section
     */
    Section.Lvl getLevel();

    /**
     * @param level
     *            the level of this section
     */
    void setLevel(Section.Lvl level);

    /**
     * tocTitle is an optional shorter title for usage e.g. in the table of
     * contents.
     * 
     * @return the tocTitle or <code>null</code> if there is none
     */
    List<Node> getTocTitle();

    /**
     * @param tocTitle
     *            the tocTitle
     * @see #getTocTitle()
     */
    void setTocTitle(List<Node> tocTitle);
}