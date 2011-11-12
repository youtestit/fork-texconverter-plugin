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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.texconverter.dom.Node;
import org.texconverter.dom.Section;
import org.texconverter.dom.impl.Command;
import org.texconverter.dom.impl.SectionImpl;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * @author tfrana
 */
public class SectionHandler implements CommandHandler {

    private static final Map<String, Section.Lvl> SECTIONLEVELS = new HashMap<String, Section.Lvl>();

    static {
        SECTIONLEVELS.put("part", Section.Lvl.PART);
        SECTIONLEVELS.put("chapter", Section.Lvl.CHAPTER);
        SECTIONLEVELS.put("section", Section.Lvl.SECTION);
        SECTIONLEVELS.put("subsection", Section.Lvl.SUBSECTION);
        SECTIONLEVELS.put("subsubsection", Section.Lvl.SUBSUBSECTION);
        SECTIONLEVELS.put("paragraph", Section.Lvl.PARAGRAPH);
        SECTIONLEVELS.put("subparagraph", Section.Lvl.SUBPARAGRAPH);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws TexSyntaxException {

        if (SECTIONLEVELS.containsKey(cmd.getName())) {
            final SectionImpl section = new SectionImpl();

            section.setTitle((List<Node>) cmd.getMandatoryArgs().get(0));
            if (cmd.getOptionalArgs() != null) {
                section.setTocTitle((List<Node>) cmd.getOptionalArgs().get(0));
            }
            final Section.Lvl lvl = SECTIONLEVELS.get(cmd.getName());
            section.setLevel(lvl);

            processSectionCounters(builder, section);

            builder.consumeNode(section, true);
        }
    }

    private void processSectionCounters(final AbstractBuilder builder,
            final SectionImpl section) {

        if (section.getLevel()
                .compareTo(builder.getHighestNumberedSectionLvl()) >= 0) {
            int sectionCounter = 1;

            String sectionCounterStr = builder
                    .getGlobalValue(getSectionCounterKey(section.getLevel()));
            if (sectionCounterStr != null) {
                sectionCounter = Integer.parseInt(sectionCounterStr);
            }

            section.setCounter(Integer.toString(sectionCounter));

            // store incremented counter
            builder.setGlobalValue(getSectionCounterKey(section.getLevel()),
                    Integer.toString(sectionCounter + 1));

            // reset section counters for all lower level sections
            Section.Lvl[] lvls = Section.Lvl.values();
            for (int i = section.getLevel().ordinal() + 1; i < lvls.length; i++) {
                builder.setGlobalValue(getSectionCounterKey(lvls[i]), Integer
                        .toString(1));
            }
        }
    }

    private String getSectionCounterKey(final Section.Lvl lvl) {
        return "sectioncounter" + lvl.toString();
    }
}
