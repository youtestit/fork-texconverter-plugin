/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:30 $
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

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.PictureImpl;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * @author tfrana
 */
public class Picturehandler implements CommandHandler {

    /**
     * Apache commons logging reference.
     */
    private static transient Log log = LogFactory.getLog(Picturehandler.class);

    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder) {

        if ("includegraphics".equals(cmd.getName())) {
            final PictureImpl pic = buildPicture(cmd, builder);
            builder.consumeNode(pic, false);
        } else if ("graphicspath".equals(cmd.getName())) {
            String path = ((List) cmd.getMandatoryArgs().get(0)).get(0)
                    .toString();
            File graphicsDir = new File(builder.getTexFileBaseDir(), path);
            builder.getRootBuilder().setValue("graphicspath",
                    graphicsDir.getAbsolutePath());
        }
    }

    private PictureImpl buildPicture(final Command cmd,
            final AbstractBuilder builder) {
        final PictureImpl pic = new PictureImpl();

        File path = new File((String) cmd.getMandatoryArgs().get(0));
        if (!path.isAbsolute()) {
            final String graphicspath = builder.getValue("graphicspath");
            if (graphicspath != null) {
                path = new File(new File(graphicspath), path.getPath());
            } else {
                log
                        .warn("Picture with relative path found, but graphicspath not set: "
                                + path);
            }
        }
        pic.setPath(path.getPath());

        if (cmd.getOptionalArgs() != null) {
            // TODO other arg parser
            final String optArg = (String) cmd.getOptionalArgs().get(0);

            final String[] args = optArg.split(",");
            for (int i = 0; i < args.length; i++) {
                boolean relativeToHeight = false, relativeToWidth = false;
                // [0] = property, [1] = value
                final String[] propVal = args[i].split("=");

                if (propVal[1].endsWith("\\textwidth")) {
                    relativeToHeight = true;
                    propVal[1] = propVal[1].replaceAll("\\\\textwidth", "");
                } else if (propVal[1].endsWith("\\textheight")) {
                    relativeToWidth = true;
                    propVal[1] = propVal[1].replaceAll("\\\\textheight", "");
                }
                try {
                    final Float value = new Float(propVal[1]);

                    if ("height".equals(propVal[0])) {
                        pic.setHeight(value);
                        pic.setIsHeightRelative(relativeToHeight
                                || relativeToWidth);
                    } else if ("width".equals(propVal[0])) {
                        pic.setWidth(value);
                        pic.setIsWidthRelative(relativeToHeight
                                || relativeToWidth);
                    } else if ("angle".equals(propVal[0])) {
                        pic.setAngle(value);
                    }
                } catch (final NumberFormatException ex) {
                    log
                            .error("buildPicture -> invalid number encountered in includegraphics argument: "
                                    + propVal[1]);
                }
            }
        }

        return pic;
    }

}
