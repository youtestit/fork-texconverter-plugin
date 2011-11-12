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
 * A floating environment. This represents a group of content which eventually
 * desires a specific position in a page-based output format ({@link #getPositionHints()}).
 * 
 * @author tfrana
 */
public interface FloatingEnvironment extends Node, Referencable {

    /**
     * Position hints indicating the desired positioning of floating
     * environments.
     * 
     * @author tfrana
     */
    public static enum PositionHint {
        /**  */
        NORMAL,
        /** Desired position at top of a page. */
        TOPOFPAGE,
        /** Desired position at bottom of a page. */
        BOTTOMOFPAGE,
        /** An extra page is desired. */
        EXTRAPAGE;
    }

    /**
     * Hints at the start of the array are more desired than latter ones.
     * 
     * @return the position hints
     */
    FloatingEnvironment.PositionHint[] getPositionHints();

}