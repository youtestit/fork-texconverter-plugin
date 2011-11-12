/**
 * $Revision: 1.2 $
 * $Date: 2006/09/26 09:31:36 $
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
package org.texconverter.reader.tex.handlers.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.texconverter.dom.Node;
import org.texconverter.dom.TypeFace;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.TypeFaceImpl;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * @author tfrana
 */
public class TypeFaceHandler implements CommandHandler {

    private static final Map<String, TypeFace.FontFamily> FONTFAMILYCOMMANDS = new HashMap<String, TypeFace.FontFamily>();

    private static final Map<String, TypeFace.FontSeries> FONTSERIESCOMMANDS = new HashMap<String, TypeFace.FontSeries>();

    private static final Map<String, TypeFace.FontShape> FONTSHAPECOMMANDS = new HashMap<String, TypeFace.FontShape>();

    private static final Map<String, TypeFace.FontSize> FONTSIZECOMMANDS = new HashMap<String, TypeFace.FontSize>();

    private static final Set<String> CONTENTINARGUMENT = new HashSet<String>();

    static {
        // font sizes
        FONTSIZECOMMANDS.put("tiny", TypeFace.FontSize.TINY);
        FONTSIZECOMMANDS.put("scriptsize", TypeFace.FontSize.SCRIPTSIZE);
        FONTSIZECOMMANDS.put("footnotesize", TypeFace.FontSize.FOOTNOTESIZE);
        FONTSIZECOMMANDS.put("small", TypeFace.FontSize.SMALL);
        FONTSIZECOMMANDS.put("normalsize", TypeFace.FontSize.NORMALSIZE);
        FONTSIZECOMMANDS.put("large", TypeFace.FontSize.LARGE);
        FONTSIZECOMMANDS.put("Large", TypeFace.FontSize.LARGE2);
        FONTSIZECOMMANDS.put("LARGE", TypeFace.FontSize.LARGE3);
        FONTSIZECOMMANDS.put("huge", TypeFace.FontSize.HUGE);
        FONTSIZECOMMANDS.put("Huge", TypeFace.FontSize.HUGE2);

        // font families
        FONTFAMILYCOMMANDS.put("textrm", TypeFace.FontFamily.ROMAN);
        FONTFAMILYCOMMANDS.put("rmfamily", TypeFace.FontFamily.ROMAN);
        FONTFAMILYCOMMANDS.put("rm", TypeFace.FontFamily.ROMAN);
        FONTFAMILYCOMMANDS.put("textsf", TypeFace.FontFamily.SANSSERIF);
        FONTFAMILYCOMMANDS.put("sffamily", TypeFace.FontFamily.SANSSERIF);
        FONTFAMILYCOMMANDS.put("sf", TypeFace.FontFamily.SANSSERIF);
        FONTFAMILYCOMMANDS.put("texttt", TypeFace.FontFamily.NONPROPORTIONAL);
        FONTFAMILYCOMMANDS.put("ttfamily", TypeFace.FontFamily.NONPROPORTIONAL);
        FONTFAMILYCOMMANDS.put("tt", TypeFace.FontFamily.NONPROPORTIONAL);

        // font series
        FONTSERIESCOMMANDS.put("textbf", TypeFace.FontSeries.BOLD);
        FONTSERIESCOMMANDS.put("bfseries", TypeFace.FontSeries.BOLD);
        FONTSERIESCOMMANDS.put("bf", TypeFace.FontSeries.BOLD);
        FONTSERIESCOMMANDS.put("textmd", TypeFace.FontSeries.MEDIUM);
        FONTSERIESCOMMANDS.put("mdseries", TypeFace.FontSeries.MEDIUM);

        // font shapes
        FONTSHAPECOMMANDS.put("textup", TypeFace.FontShape.UPRIGHT);
        FONTSHAPECOMMANDS.put("upshape", TypeFace.FontShape.UPRIGHT);
        FONTSHAPECOMMANDS.put("textit", TypeFace.FontShape.ITALIC);
        FONTSHAPECOMMANDS.put("itshape", TypeFace.FontShape.ITALIC);
        FONTSHAPECOMMANDS.put("it", TypeFace.FontShape.ITALIC);
        FONTSHAPECOMMANDS.put("textsl", TypeFace.FontShape.SLANTED);
        FONTSHAPECOMMANDS.put("slshape", TypeFace.FontShape.SLANTED);
        FONTSHAPECOMMANDS.put("sl", TypeFace.FontShape.SLANTED);
        FONTSHAPECOMMANDS.put("textsc", TypeFace.FontShape.SMALLCAPS);
        FONTSHAPECOMMANDS.put("scshape", TypeFace.FontShape.SMALLCAPS);
        FONTSHAPECOMMANDS.put("sc", TypeFace.FontShape.SMALLCAPS);

        CONTENTINARGUMENT.add("textrm");
        CONTENTINARGUMENT.add("textsf");
        CONTENTINARGUMENT.add("texttt");
        CONTENTINARGUMENT.add("textbf");
        CONTENTINARGUMENT.add("textmd");
        CONTENTINARGUMENT.add("textup");
        CONTENTINARGUMENT.add("textit");
        CONTENTINARGUMENT.add("textsl");
        CONTENTINARGUMENT.add("textsc");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws ConfigurationException {

        final TypeFaceImpl typeFace = new TypeFaceImpl();

        // FIXME temporary solution until better TypeFace architecture
        // (parallel?)
        if ("em".equals(cmd.getName())) {
            cmd.setName("it");
        } else if ("emph".equals(cmd.getName())) {
            cmd.setName("textit");
        }

        if (FONTFAMILYCOMMANDS.containsKey(cmd.getName())) {
            typeFace.setFontFamily(FONTFAMILYCOMMANDS.get(cmd.getName()));
        } else if (FONTSERIESCOMMANDS.containsKey(cmd.getName())) {
            typeFace.setFontSeries(FONTSERIESCOMMANDS.get(cmd.getName()));
        } else if (FONTSHAPECOMMANDS.containsKey(cmd.getName())) {
            typeFace.setFontShape(FONTSHAPECOMMANDS.get(cmd.getName()));
        } else if (FONTSIZECOMMANDS.containsKey(cmd.getName())) {
            typeFace.setFontSize(FONTSIZECOMMANDS.get(cmd.getName()));
        } else {
            throw new ConfigurationException("command " + cmd.getName()
                    + " is mapped to TypeFaceHandler but not handled");
        }

        boolean contentInArgument = CONTENTINARGUMENT.contains(cmd.getName());
        typeFace.setBasic(!contentInArgument);
        if (contentInArgument) {
            typeFace.getChildren().addAll(
                    (List<Node>) cmd.getMandatoryArgs().get(0));
        }

        builder.consumeNode(typeFace, !contentInArgument);
    }

}
