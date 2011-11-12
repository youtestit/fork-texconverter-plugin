/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:19 $
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
package org.texconverter.dom.impl;

import org.texconverter.dom.Node;
import org.texconverter.dom.TypeFace;

/**
 * @author tfrana
 * @see org.texconverter.dom.TypeFace
 */
public class TypeFaceImpl extends NodeWithChildsImpl implements TypeFace {

    private FontFamily fontFamily = null;

    private FontSeries fontSeries = null;

    private FontShape fontShape = null;

    private FontSize fontSize = null;

    private boolean basic = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canEndNodes() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEndedBy(final Node node) {
        // if (node instanceof TypeFaceImpl) {
        // // if every attribute of this is overridden by the new one, the new
        // // one can safely end us
        // if (isOverriddenBy((TypeFaceImpl) node)) { return true; }
        // }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public FontFamily getFontFamily() {
        return fontFamily;
    }

    /**
     * @param fontFamily
     *            the font family
     */
    public void setFontFamily(final FontFamily fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * {@inheritDoc}
     */
    public FontSeries getFontSeries() {
        return fontSeries;
    }

    /**
     * @param fontSeries
     *            the font series
     */
    public void setFontSeries(final FontSeries fontSeries) {
        this.fontSeries = fontSeries;
    }

    /**
     * {@inheritDoc}
     */
    public FontShape getFontShape() {
        return fontShape;
    }

    /**
     * @param fontShape
     *            the font shape
     */
    public void setFontShape(final FontShape fontShape) {
        this.fontShape = fontShape;
    }

    /**
     * {@inheritDoc}
     */
    public FontSize getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize
     *            the font size
     */
    public void setFontSize(final FontSize fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * A typeface is considered being overridden by another typeface if nothing
     * from the first typeface has any effect beyond the other typeface node.
     * 
     * @param typeFace
     *            the other typeface
     * @return if this is overridden by <code>typeFace</code>
     */
    public boolean isOverriddenBy(final TypeFaceImpl typeFace) {
        return (fontFamily == null || typeFace.getFontFamily() != null)
                && (fontSeries == null || typeFace.getFontSeries() != null)
                && (fontShape == null || typeFace.getFontShape() != null)
                && (fontSize == null || typeFace.getFontSize() != null);
    }

    /**
     * Two typefaces are mergable if their font attributes do not overlap.
     * 
     * @param typeFace
     *            the other typeface
     * @return if they are mergable
     */
    public boolean isMergableWith(final TypeFaceImpl typeFace) {
        return (fontFamily == typeFace.getFontFamily() || fontFamily == null || typeFace
                .getFontFamily() == null)
                && (fontSeries == typeFace.getFontSeries()
                        || fontSeries == null || typeFace.getFontSeries() == null)
                && (fontShape == typeFace.getFontShape() || fontShape == null || typeFace
                        .getFontShape() == null)
                && (fontSize == typeFace.getFontSize() || fontSize == null || typeFace
                        .getFontSize() == null);
    }

    /**
     * Merge two typefaces.
     * 
     * @param typeFace
     *            the other typeface
     * @return true if the merging was successfull, false otherwise
     * @see #isMergableWith(TypeFaceImpl)
     */
    public boolean mergeWith(final TypeFaceImpl typeFace) {
        if (isMergableWith(typeFace)) {
            overwritingMergeWith(typeFace);
            return true;
        }
        return false;
    }

    /**
     * Force the merge of the typefaces attributes into this typeface. If one of
     * the other typefaces attributes is null, it will not overwrite the
     * attribute in this typeface.
     * 
     * @param typeFace
     *            the other typeface
     */
    public void overwritingMergeWith(final TypeFaceImpl typeFace) {
        if (typeFace.getFontFamily() != null) {
            fontFamily = typeFace.getFontFamily();
        }
        if (typeFace.getFontSeries() != null) {
            fontSeries = typeFace.getFontSeries();
        }
        if (typeFace.getFontShape() != null) {
            fontShape = typeFace.getFontShape();
        }
        if (typeFace.getFontSize() != null) {
            fontSize = typeFace.getFontSize();
        }
    }

    /**
     * A TypeFace being basic means all following content added to the DOMTree
     * is affected by this typeface, unless it overwritten by a rivaling basic
     * typeFace.
     * 
     * @return if this typeface is basic
     */
    public boolean isBasic() {
        return basic;
    }

    /**
     * @param isBasic
     *            if this typeface is basic
     * @see #isBasic()
     */
    public void setBasic(final boolean isBasic) {
        this.basic = isBasic;
    }
}
