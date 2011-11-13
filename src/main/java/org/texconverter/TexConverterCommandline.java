/**
 * $Revision: 1.1 $
 * $Date: 2006/08/30 09:35:31 $
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
package org.texconverter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.texconverter.resource.ClasspathResourceLoader;
import org.texconverter.resource.ResourceLoader;

/**
 * @author sgodau
 */
public class TexConverterCommandline extends TexConverter {

    private static final String OPTION_PROPFILE = "p";

    private static final String OPTION_INPUT_FILE = "i";

    private static final String OPTION_OUTPUT_FILE = "o";

    private static final String OPTION_OUTPUTFORMAT = "f";

    private static final String OPTION_VERBOSE = "v";

    private static final String OPTION_LOCALECODE = "l";
    
    private static final String OPTION_MAVEN_DEV= "m";

    /**
     * @param args
     *            main args
     */
    @SuppressWarnings("static-access")
    // commons-cli use is really awkward with qualified access
    public static void main(final String[] args) {

        final Option optionInputFile = OptionBuilder.withArgName("file")
                .hasArg().isRequired().withDescription(
                        "Required: The input file").create(OPTION_INPUT_FILE);
        final Option optionOutputFile = OptionBuilder.withArgName("file")
                .hasArg().isRequired().withDescription(
                        "Required: The output file").create(OPTION_OUTPUT_FILE);
        final Option optionOutputFormat = OptionBuilder.withArgName("type")
                .hasArg().isRequired().withDescription(
                        "Required: The desired output format").create(
                        OPTION_OUTPUTFORMAT);
        final Option optionVerbose = OptionBuilder.withDescription(
                "optional: verbose").create(OPTION_VERBOSE);
        final Option optionLocaleCode = OptionBuilder
                .withArgName("locale")
                .hasArg()
                .withDescription(
                        "Optional: The ISO 639-2 3-letter language code, this affects some titles"
                                + " and captions in the document. If not given, the"
                                + " system locale will be used. Refer to the documentation for available language codes.")
                .create(OPTION_LOCALECODE);

        final Option optionMavenDev = OptionBuilder.withArgName("maven")
                                                   .hasOptionalArg()
                                                   .withDescription("Allow to specify that running on devlopment maven mode")
                                                   .create(OPTION_MAVEN_DEV);
        
        final Options directOptions = new Options();
        directOptions.addOption(optionInputFile);
        directOptions.addOption(optionOutputFile);
        directOptions.addOption(optionOutputFormat);
        directOptions.addOption(optionVerbose);
        directOptions.addOption(optionLocaleCode);
        directOptions.addOption(optionMavenDev);
        

        final Option optionPropertiesFile = OptionBuilder.withArgName("file")
                .hasArg().isRequired().withDescription(
                        "optional: the property file").create(OPTION_PROPFILE);

        final Options propertyOptions = new Options();
        propertyOptions.addOption(optionPropertiesFile);
        propertyOptions.addOption(optionVerbose);

        final CommandLineParser cmdparser = new PosixParser();
        CommandLine commandLine = null;

        String inputFile = null, outputFile = null, outputFormat = null, propertiesFile = null, localeCode = null;
        String mavenDevMode = null; 
        try {
            commandLine = cmdparser.parse(propertyOptions, args);

            propertiesFile = commandLine.getOptionValue(OPTION_PROPFILE);

        } catch (final ParseException ex) {

            try {
                commandLine = cmdparser.parse(directOptions, args);

                inputFile = commandLine.getOptionValue(OPTION_INPUT_FILE);
                outputFile = commandLine.getOptionValue(OPTION_OUTPUT_FILE);
                outputFormat = commandLine.getOptionValue(OPTION_OUTPUTFORMAT);
                localeCode = commandLine.getOptionValue(OPTION_LOCALECODE);
                
                mavenDevMode = commandLine.getOptionValue(OPTION_MAVEN_DEV);
                if(mavenDevMode!=null){
                    TexConverter.RESOURCES_PATH= "src/main/resources/";
                }
                
                if(inputFile!=null){
                    inputFile = inputFile.trim();
                }
                if(outputFile!=null){
                    outputFile = outputFile.trim();
                }

            } catch (final ParseException e) {

                final HelpFormatter formatter = new HelpFormatter();
                formatter
                        .printHelp("java texconverter.jar", "", directOptions,
                                "Alternatively, a single argument specifying a property file can be used:");
                formatter
                        .printHelp(
                                "java texconverter.jar",
                                "",
                                propertyOptions,
                                "The option propertiesFile will override any other options except verbose."
                                        + " In this text file one or several conversion tasks can be defined."
                                        + " Please refer to the readme or the documentation for a description of the files format.");
                System.exit(1);
            }
        }

        final boolean verbose = commandLine.hasOption(OPTION_VERBOSE);

        final TexConverterCommandline converter = new TexConverterCommandline();
        converter.init(null, verbose);
        converter.startConversion(propertiesFile, inputFile, outputFile,
                outputFormat, localeCode);
    }

    @Override
    protected ResourceLoader getResourceLoader() {
        return new ClasspathResourceLoader();
    }
}
