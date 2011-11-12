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
 * @author tfrana
 */
public interface Listing extends NodeWithChilds, Referencable {

    /**
     * Listing styles.
     * 
     * @author tfrana
     */
    public static enum ListingStyles {
        /** Use simple bullets for each listing item. */
        BULLETS,
        /** Use incremented numbers for each listing item. */
        NUMBERS,
        /**
         * Use the description of an {@link ListingItem} instead of
         * bullets/numbers.
         */
        DESCRIPTIONS;
    }

    /**
     * @return one of the Listing#STYLE_.. constants
     */
    ListingStyles getStyle();

    /**
     * @param style
     *            one of Listing#STYLE_.. constants
     */
    void setStyle(ListingStyles style);
}
