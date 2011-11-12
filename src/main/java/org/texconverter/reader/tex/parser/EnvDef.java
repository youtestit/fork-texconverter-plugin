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

import java.util.Map;

/**
 * The definition of a TeX environment.
 * 
 * @author tfrana
 */
public class EnvDef {

    protected boolean altMode;

    protected boolean[] interpreteMandArg, interpreteOptArg;

    protected Class builderClass;

    protected Map<String, CmdDef> cmdDefs;

    /**
     * @param interpreteMandArg
     *            which mandatory args should be interpreted and which should be
     *            parsed verbatim
     * @param interpreteOptArg
     *            which optional args should be interpreted and which should be
     *            parsed verbatim
     * @param altMode
     *            if this environment has an alternative mode (optional '*'
     *            after environment name)
     * @param builderClass
     *            the class of the builder reponsible for building this
     *            environment type
     * @param cmdDefs
     *            any extra/different command definitions for this environment,
     *            if it has any (most likely commands mapped to different
     *            handlers)
     */
    public EnvDef(final boolean[] interpreteMandArg,
            final boolean[] interpreteOptArg, final boolean altMode,
            final Class builderClass, final Map<String, CmdDef> cmdDefs) {
        this.interpreteMandArg = interpreteMandArg;
        this.interpreteOptArg = interpreteOptArg;
        this.builderClass = builderClass;
        this.cmdDefs = cmdDefs;
        this.altMode = altMode;
    }

    /**
     * @return the class of the builder responsible for building this
     *         environment
     */
    public Class getBuilderClass() {
        return builderClass;
    }

    /**
     * @param builderClass
     *            the class of the builder responsible for building this
     *            environment
     */
    public void setBuilderClass(final Class builderClass) {
        this.builderClass = builderClass;
    }

    /**
     * @return command definitions that differ from the basic definitions inside
     *         this environment
     */
    public Map<String, CmdDef> getCmdDefs() {
        return cmdDefs;
    }
}
