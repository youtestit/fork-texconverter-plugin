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

import org.texconverter.dom.impl.Command;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.handlers.CommandHandler;
import org.texconverter.reader.tex.parser.CmdDef;

/**
 * @author tfrana
 */
public class DefineCmdHandler implements CommandHandler {

    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder)
            throws TexSyntaxException {

        if ("newcommand".equals(cmd.getName())
                || "renewcommand".equals(cmd.getName())) {

            String newCmdName, replacement, optionalArg = null;
            int numArgs = 0;

            String arg = (String) cmd.getMandatoryArgs().get(0);
            newCmdName = arg.trim();

            if (!newCmdName.startsWith("\\")) {
                throw new TexSyntaxException(
                        "malformed command in first argument of newcommand: "
                                + arg);
            }
            newCmdName = newCmdName.substring(1);
            replacement = (String) cmd.getMandatoryArgs().get(1);

            if (cmd.getOptionalArgs() != null) {
                arg = (String) cmd.getOptionalArgs().get(0);
                try {
                    numArgs = Integer.parseInt(arg);
                } catch (final NumberFormatException e) {
                    throw new TexSyntaxException(cmd.getName()
                            + ": first optional arg is not a number: " + arg, e);
                }

                if (cmd.getOptionalArgs().size() > 1) {
                    optionalArg = (String) cmd.getOptionalArgs().get(1);
                }
            }

            final CommandHandler replacementHandler = new ReplacementCmdHandler(
                    replacement, numArgs, optionalArg);

            int numOptArgs = 0;
            if (optionalArg != null) {
                numArgs -= 1;
                numOptArgs = 1;
            }
            final CmdDef cmdDef = new CmdDef(new boolean[numArgs],
                    new boolean[numOptArgs], false, false, replacementHandler);
            builder.addCmdDef(newCmdName, cmdDef);
        }
    }
}
