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

import org.texconverter.dom.Node;
import org.texconverter.dom.TableField;

/**
 * @author tfrana
 * @see org.texconverter.dom.TableField
 */
public class TableFieldImpl extends AlignedNodeImpl implements TableField {

    private int colSpan = 1;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canEndNodes() {
        return true;
    }

    /**
     * @return how many columns this field spans
     */
    public int getColSpan() {
        return colSpan;
    }

    /**
     * @param colSpan
     *            how many columns this field spans
     */
    public void setColSpan(final int colSpan) {
        this.colSpan = colSpan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEndedBy(final Node node) {
        return node instanceof TableFieldImpl;
    }

}
