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
package org.texconverter.reader.tex.parser;

import org.texconverter.reader.tex.handlers.CommandHandler;

/**
 * The definition of a TeX command.
 * 
 * @author tfrana
 */
public class CmdDef {

    protected boolean plainValue, altMode;

    protected boolean[] interpreteMandArg, interpreteOptArg;

    protected CommandHandler cmdHandler;

    /**
     * @param interpreteMandArg
     *            which mandatory args should be interpreted and which should be
     *            parsed verbatim
     * @param interpreteOptArg
     *            which optional args should be interpreted and which should be
     *            parsed verbatim
     * @param plainValue
     *            if this command uses a plain value assignment (via '=')
     * @param altMode
     *            if this command has an alternative mode (optional '*' after
     *            command name)
     * @param cmdHandler
     *            the handler for this command
     */
    public CmdDef(final boolean[] interpreteMandArg,
            final boolean[] interpreteOptArg, final boolean plainValue,
            final boolean altMode, final CommandHandler cmdHandler) {
        this.interpreteMandArg = interpreteMandArg;
        this.interpreteOptArg = interpreteOptArg;
        this.cmdHandler = cmdHandler;
        this.plainValue = plainValue;
        this.altMode = altMode;
    }
}