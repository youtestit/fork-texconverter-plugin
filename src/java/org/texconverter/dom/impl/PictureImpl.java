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

import org.texconverter.dom.Picture;

/**
 * @author tfrana
 * @see org.texconverter.dom.Picture
 */
public class PictureImpl extends AbstractNode implements Picture {

    private String path = null;

    private Float height = null, width = null, angle = null;

    private boolean isHeightRelative = false, isWidthRelative = false;

    /**
     * {@inheritDoc}
     */
    public String getPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    public Float getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     */
    public void setHeight(final Float height) {
        this.height = height;
    }

    /**
     * {@inheritDoc}
     */
    public Float getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     */
    public void setWidth(final Float width) {
        this.width = width;
    }

    /**
     * {@inheritDoc}
     */
    public Float getAngle() {
        return angle;
    }

    /**
     * {@inheritDoc}
     */
    public void setAngle(final Float angle) {
        this.angle = angle;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHeightRelative() {
        return isHeightRelative;
    }

    /**
     * {@inheritDoc}
     */
    public void setIsHeightRelative(final boolean isHeightRelative) {
        this.isHeightRelative = isHeightRelative;

    }

    /**
     * {@inheritDoc}
     */
    public boolean isWidthRelative() {
        return isWidthRelative;
    }

    /**
     * {@inheritDoc}
     */
    public void setIsWidthRelative(final boolean isWidthRelative) {
        this.isWidthRelative = isWidthRelative;
    }
}
