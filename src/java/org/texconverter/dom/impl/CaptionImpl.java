/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:20 $
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

import java.util.List;

import org.texconverter.dom.Caption;
import org.texconverter.dom.Node;

/**
 * @author tfrana
 * @see org.texconverter.dom.Caption
 */
public class CaptionImpl extends NodeWithChildsImpl implements Caption {

    private List<Node> tocTitle = null;

    /**
     * {@inheritDoc}
     */
    public List<Node> getTocTitle() {
        return tocTitle;
    }

    /**
     * {@inheritDoc}
     */
    public void setTocTitle(final List<Node> tocTitle) {
        this.tocTitle = tocTitle;
    }

}
