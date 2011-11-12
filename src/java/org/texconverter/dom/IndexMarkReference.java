/**
 * $Revision: 1.1 $
 * $Date: 2006/09/28 12:08:29 $
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
package org.texconverter.dom;

/**
 * @author tfrana
 */
public interface IndexMarkReference extends Reference {

    /**
     * @author tfrana
     */
    enum IndexRefType {
        /** References a single IndexMark. */
        SINGLE,
        /**
         * References a range, denoted by 2 IndexMarks.
         * 
         * @see IndexMarkReference#getReferencedNodeStop()
         * @see IndexMarkReference#getReferencedLabelStop()
         */
        RANGE;
    }

    /**
     * @return the type of this reference
     */
    IndexRefType getType();

    /**
     * @return the end of the range if type is {@link IndexRefType#RANGE} or
     *         <code>null</code>
     */
    IndexMark getReferencedNodeStop();

    /**
     * @return the label of the end of the range if type is
     *         {@link IndexRefType#RANGE} or <code>null</code>
     */
    String getReferencedLabelStop();

    /**
     * Set the label of the Referencable which denotes the end of the range.
     * 
     * @param referencedLabelStop
     *            the label
     */
    void setReferencedLabelStop(String referencedLabelStop);
}
