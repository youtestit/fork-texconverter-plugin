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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texconverter.dom.Node;
import org.texconverter.dom.impl.Command;
import org.texconverter.io.impl.FilePeekReader;
import org.texconverter.io.impl.StringPeekReader;
import org.texconverter.reader.tex.Helper;
import org.texconverter.reader.tex.builder.impl.AbstractBuilder;
import org.texconverter.reader.tex.handlers.CommandHandler;
import org.texconverter.reader.tex.parser.Tokenizer;

/**
 * @author tfrana
 */
public class LstListingHandler implements CommandHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(LstListingHandler.class);
    private final static Map<String,String> BRUSH_MAPPING = new HashMap<String,String>();
    

    {
        BRUSH_MAPPING.put("text","text");

        BRUSH_MAPPING.put("as3","as3");
        BRUSH_MAPPING.put("sh","shell");
        BRUSH_MAPPING.put("bash","shell");
        BRUSH_MAPPING.put("cf","cf");
        BRUSH_MAPPING.put("csharp","csharp");
        
        BRUSH_MAPPING.put("c++","cpp");
        BRUSH_MAPPING.put("h","cpp");
        BRUSH_MAPPING.put("c","cpp");
        
        BRUSH_MAPPING.put("css","css");
        
        BRUSH_MAPPING.put("delphi","delphi");
        BRUSH_MAPPING.put("pas","pascal");
        BRUSH_MAPPING.put("pascal","pascal");
        
        BRUSH_MAPPING.put("diff","diff");
        BRUSH_MAPPING.put("patch","diff");
        
        BRUSH_MAPPING.put("erl","erl");
        BRUSH_MAPPING.put("erlang","erl");
        
        BRUSH_MAPPING.put("java","java");
        BRUSH_MAPPING.put("jfx","jfx");
        BRUSH_MAPPING.put("groovy","groovy");
        BRUSH_MAPPING.put("scala","scala");
        BRUSH_MAPPING.put("sql","sql");
        
        BRUSH_MAPPING.put("js","js");
        BRUSH_MAPPING.put("perl","perl");
        BRUSH_MAPPING.put("pl","perl");
        BRUSH_MAPPING.put("php","php");
        BRUSH_MAPPING.put("ps","ps");
        
        BRUSH_MAPPING.put("py","py");
        BRUSH_MAPPING.put("rails","rails");
        BRUSH_MAPPING.put("ror","rails");
        BRUSH_MAPPING.put("ruby","rails");
        
        BRUSH_MAPPING.put("vb","vb");
        BRUSH_MAPPING.put("vb","vbnet");
        
        BRUSH_MAPPING.put("xml","xml");
        BRUSH_MAPPING.put("xslt","xml");
        BRUSH_MAPPING.put("xhtml","xml");
        BRUSH_MAPPING.put("html","xml");
        BRUSH_MAPPING.put("jsp","xml");
        BRUSH_MAPPING.put("vm","xml");
        BRUSH_MAPPING.put("ftl","xml");
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void handle(final Command cmd, final AbstractBuilder builder) {

        // TODO handle lstset (eval & store in current builder

        if ("lstinputlisting".equals(cmd.getName())) {
            evalLstInputListing(cmd, builder);
        }
    }

    @SuppressWarnings("unchecked")
    private void evalLstInputListing(final Command cmd,
            final AbstractBuilder builder) {
        final Tokenizer tokenizer = builder.getTokenizer();
        final String filePath = Helper
                .getPlainTextFromNodeList((List<Node>) cmd.getMandatoryArgs()
                        .get(0));

        try {
            File file = new File(builder.getTexFileBaseDir(), filePath);
            final FilePeekReader inputFile = new FilePeekReader(file);

            String fileType= null;
            if(file.getName().contains(".")){
                int dotIndex = file.getName().lastIndexOf(".");
                fileType = file.getName().substring(dotIndex+1).toLowerCase();
            }
            if(fileType==null){
                fileType = "text";
            }
            
            String brush = null;
            if(BRUSH_MAPPING.containsKey(fileType)){
                brush = BRUSH_MAPPING.get(fileType);
            }else{
                brush = "text";
            }
            
            tokenizer.insertInput(new StringPeekReader("\n\\end{lstlisting}"));
            tokenizer.insertInput(new StringPeekReader("]]></script>"));  
            
            
            tokenizer.insertInput(inputFile);

            String beginCmd = "\\begin{lstlisting}";
            if (cmd.getOptionalArgs() != null) {
                beginCmd = beginCmd + "[" + cmd.getOptionalArgs().get(0)
                        + "]\\n";
            }
            
            
            tokenizer.insertInput(new StringPeekReader(
                                        String.format("<script type=\"syntaxhighlighter\" class=\"brush: %s\"><![CDATA[",brush)));
            tokenizer.insertInput(new StringPeekReader(beginCmd));            
            
            
            
        } catch (final FileNotFoundException e) {
            LOGGER.error("File " + filePath + " could not be found", e);
        }
    }
}

