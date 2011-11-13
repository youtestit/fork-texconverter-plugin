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
package org.texconverter.reader.tex.builder.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.texconverter.TexConverter;
import org.texconverter.dom.Node;
import org.texconverter.dom.Reference;
import org.texconverter.dom.TexDocument;
import org.texconverter.dom.impl.EnvCommand;
import org.texconverter.dom.impl.TexDocumentImpl;
import org.texconverter.io.impl.FilePeekReader;
import org.texconverter.reader.tex.builder.TeXDocumentBuilder;
import org.texconverter.reader.tex.exceptions.ConfigurationException;
import org.texconverter.reader.tex.exceptions.TexParserException;
import org.texconverter.reader.tex.exceptions.TexSyntaxException;
import org.texconverter.reader.tex.parser.CommandDefinitionsParser;
import org.texconverter.reader.tex.parser.Token;
import org.texconverter.reader.tex.parser.Tokenizer;

/**
 * Root builder for the whole tex document.
 * 
 * @author tfrana
 */
public class TeXDocumentBuilderImpl extends AbstractBuilder implements
        TeXDocumentBuilder {

    /**
     * @param cmdDefsFilePath path of the command/environment definitions xml
     *            file
     * @param locale The locale which should be used for localized resources
     * @throws IOException on IO problems
     * @throws ConfigurationException on configuration problems
     */
    public TeXDocumentBuilderImpl(final String cmdDefsFile, final Locale locale)
            throws IOException, ConfigurationException {
        super();
        rootBuilder = this;

        String cmdDefsFilePath = null;

        if (TexConverter.COMMANDDEFS_FILE.equals(cmdDefsFile)) {
            cmdDefsFilePath = TexConverter.RESOURCES_PATH + cmdDefsFile;
        }else{
            cmdDefsFilePath = cmdDefsFile ;
        }

        CommandDefinitionsParser.readTexConverterDefsFile(cmdDefsFilePath, this);
        labels = new HashMap<String, Node>();
        refNodes = new ArrayList<Reference>();
        super.init(null, new TexDocumentImpl(), Token.NONE);
        rootBuilderModule.setLocale(locale);
    }

    /**
     * {@inheritDoc}
     */
    public TexDocument build(final File texFile) throws IOException,
            TexParserException, ConfigurationException {

        if(!texFile.exists()){
            throw new IOException("File not found :"+texFile.getAbsolutePath());
        }
        tokenizer = new Tokenizer();
        tokenizer.insertInput(new FilePeekReader(texFile));

        String projectName = texFile.getName();
        int i = projectName.lastIndexOf('.');
        if (i != -1) {
            projectName = projectName.substring(0, i);
        }
        setTexProjectName(projectName);

        File baseDir = texFile.getParentFile();
        if (baseDir == null) {
            baseDir = new File(".");
        }
        setTexFileBaseDir(baseDir);

        return (TexDocument) build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final AbstractBuilder parentBuilder, final EnvCommand cmd)
            throws TexParserException {
        if (parentBuilder != null) {
            throw new TexSyntaxException(
                    "TeXDocumentBuilder is not root builder");
        }
        super.init(parentBuilder, new TexDocumentImpl(cmd.getName()),
                Token.NONE);
    }
}
