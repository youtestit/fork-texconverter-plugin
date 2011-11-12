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
 * A picture.
 * 
 * @author tfrana
 */
public interface Picture extends Node {

    /**
     * Get the filepath of the picture. This can be either a relative or an
     * absolute path.
     * 
     * @return the filepath
     */
    String getPath();

    /**
     * @param path
     *            the filepath of the picture
     * @see #getPath()
     */
    void setPath(String path);

    /**
     * The supposed height of the picture. If <code>null</code> is returned,
     * there is no height hint for the picture. If a value is returned, check
     * #isHeightRelative(). If it is relative, the returned number must be
     * interpreted as a factor (1.0 = 100%) of the height of a page to get an
     * absolute size hint. Otherwise the value is absolute in pixels. If one of
     * height or width hints has no value, the missing hint can of course be
     * calculated by the aspect ratio of the picture.
     * 
     * @return the height hint or <code>null</code>
     */
    Float getHeight();

    /**
     * @param height
     *            the height value
     * @see #getHeight()
     */
    void setHeight(Float height);

    /**
     * The supposed width of the picture. If <code>null</code> is returned,
     * there is no width hint for the picture. If a value is returned, check
     * #isWidthRelative(). If it is relative, the returned number must be
     * interpreted as a factor (1.0 = 100%) of the width of a page of text to
     * get an absolute size hint. Otherwise the value is absolute in pixels. If
     * one of height or width hints has no value, the missing hint can of course
     * be calculated by the aspect ratio of the picture.
     * 
     * @return the width hint or <code>null</code>
     */
    Float getWidth();

    /**
     * @param width
     *            the width value
     * @see #getWidth()
     */
    void setWidth(Float width);

    /**
     * If a non-<code>null</code> value is returned, the picture should be
     * displayed rotated.
     * 
     * @return the degrees by which the picture should be rotated or
     *         <code>null</code>
     */
    Float getAngle();

    /**
     * @param angle
     *            the angle
     * @see #getAngle()
     */
    void setAngle(Float angle);

    /**
     * @return if the height is relative
     * @see #getHeight()
     */
    boolean isHeightRelative();

    /**
     * @param isHeightRelative
     *            if the height is relative
     */
    void setIsHeightRelative(boolean isHeightRelative);

    /**
     * @return if the width is relative
     * @see #getWidth()
     */
    boolean isWidthRelative();

    /**
     * @param isWidthRelative
     *            if the width is relative
     */
    void setIsWidthRelative(boolean isWidthRelative);
}