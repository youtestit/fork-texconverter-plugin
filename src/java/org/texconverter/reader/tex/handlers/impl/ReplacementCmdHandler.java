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
import org.texconverter.io.impl.StringPeekReader;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.handlers.CommandHandler;


/**
 * @author tfrana
 */
public class ReplacementCmdHandler implements CommandHandler {

    private String replacement;
    private int numMandatoryArgs = 0;
    private String optArgDefault = null;

    /**
     * Creates a handler for a new command which takes no arguments.
     * 
     * @param replacement
     *            the replacement that should be inserted when this handler is
     *            called
     */
    public ReplacementCmdHandler(final String replacement) {
        super();
        this.replacement = new StringBuffer("{").append(replacement)
            .append("}").toString();
    }

    /**
     * Creates a handler for a new command which can take arguments.
     * 
     * @param replacement
     *            the replacement string, which must contain the placeholders #1
     *            to #n where n = number of arguments
     * @param args
     *            the number of arguments
     * @param optArgDefault
     *            if this is not <code>null</code>, the commands first
     *            argument (which will replace placeholder #1) will be an
     *            optional argument, with the default value being the value of
     *            this parameter
     */
    public ReplacementCmdHandler(final String replacement, final int args,
        final String optArgDefault) {
        this.replacement = new StringBuffer("{").append(replacement)
            .append("}").toString();
        this.numMandatoryArgs = args;
        if (optArgDefault != null) {
            this.optArgDefault = optArgDefault;
            this.numMandatoryArgs--;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder) {

        String repl = this.replacement;

        if (numMandatoryArgs > 0 || optArgDefault != null) {
            String arg = optArgDefault;
            int replaceNum = 1;
            if (optArgDefault != null) {
                if (cmd.getOptionalArgs() != null) {
                    arg = (String) cmd.getOptionalArgs().get(0);
                }
                repl = repl.replace("#1", arg);
                replaceNum++;
            }
            for (int i = 0; i < numMandatoryArgs; replaceNum++, i++) {
                arg = (String) cmd.getMandatoryArgs().get(i);
                repl = repl.replace("#" + replaceNum, arg);
            }
        }
        builder.getTokenizer().insertInput(new StringPeekReader(repl));
    }

}
