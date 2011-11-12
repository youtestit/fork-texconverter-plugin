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

/**
 * Indicates that all Text childs are formatted in some way. Which way is told
 * by getType, which will return one of the TYPE.. constants. E.g. a type of
 * TYPE_FAMILY and a value of FFAM_ROMAN tells that all Text childs should use a
 * roman font.
 * 
 * @author tfrana
 */
public interface TypeFace extends NodeWithChilds {

    /**
     * Font families.
     * 
     * @author tfrana
     */
    public static enum FontFamily {
        /** Standard roman font. */
        ROMAN,
        /** A font using no serifes. */
        SANSSERIF,
        /**
         * A nonproportional font (all symbols occupy the same width), aka
         * typewriter.
         */
        NONPROPORTIONAL;

        /**
         * @return default family
         */
        public static FontFamily getDefault() {
            return ROMAN;
        }
    }

    /**
     * Font series.
     * 
     * @author tfrana
     */
    public static enum FontSeries {
        /** Bold font version. */
        BOLD,
        /** Medium (non-bold) font version. */
        MEDIUM;

        /**
         * @return default series
         */
        public static FontSeries getDefault() {
            return MEDIUM;
        }
    }

    /**
     * Font shapes.
     * 
     * @author tfrana
     */
    public static enum FontShape {
        /** Upright shape (standard). */
        UPRIGHT,
        /** Italic shape. */
        ITALIC,
        /** Slanted shape (somewhere between italic and upright shape). */
        SLANTED,
        /** A font using all capital letters. */
        SMALLCAPS;

        /**
         * @return default shape
         */
        public static FontShape getDefault() {
            return UPRIGHT;
        }
    }

    /**
     * Font sizes (10).
     * 
     * @author tfrana
     */
    public static enum FontSize {
        // TODO do these have certain values added/subtracted from the default
        // font size?
        /**  */
        TINY,
        /**  */
        SCRIPTSIZE,
        /**  */
        FOOTNOTESIZE,
        /**  */
        SMALL,
        /**  */
        NORMALSIZE,
        /**  */
        LARGE,
        /**  */
        LARGE2,
        /** Equivalent to the section heading size. */
        LARGE3,
        /**  */
        HUGE,
        /**  */
        HUGE2;

        /**
         * @return default size
         */
        public static FontSize getDefault() {
            return NORMALSIZE;
        }
    }

    /**
     * @return the font family or <code>null</code> if this typeface does not
     *         indicate a change in the font family
     */
    FontFamily getFontFamily();

    /**
     * @return the font series or <code>null</code> if this typeface does not
     *         indicate a change in the font series
     */
    FontSeries getFontSeries();

    /**
     * @return the font shape or <code>null</code> if this typeface does not
     *         indicate a change in the font shape
     */
    FontShape getFontShape();

    /**
     * @return the font size or <code>null</code> if this typeface does not
     *         indicate a change in the font size
     */
    FontSize getFontSize();
}