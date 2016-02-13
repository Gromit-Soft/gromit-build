/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleScriptException

/*
 * The findUnusedStrings task finds unused localized strings
 */
class FindUnusedStringsTask extends DefaultTask  {
    @TaskAction
    def action() {
        println ':finding unused localized strings'

        def resourcedStrings = []

        if (!project.unusedStrings.bundleFile.exists()) {
            throw new GradleScriptException("You must build the localized strings before running this command.", null);
        }

        project.unusedStrings.bundleFile.eachLine { line, count ->
            if (line.startsWith('i18n.')) {
                resourcedStrings.add([
                        string: line.substring(5, line.indexOf('=')),
                        used: false
                ]);
            }
        }

        project.unusedStrings.sourceFiles.each { element ->
            if (element.exists()) {
                println('Searching file: ' + element)
                element.eachLine { line, count ->

                    resourcedStrings.each { resourcedString ->
                        if (line.indexOf('i18n.' + resourcedString.string) > -1 ||
                                line.indexOf('localizationService.' + resourcedString.string) > -1 ||
                                line.indexOf('title: \'' + resourcedString.string) > -1) {
                            resourcedString.used = true;
                        }
                    }
                }
            }
        }

        def out = new StringBuffer();

        def unusedCount = 0;

        resourcedStrings.each { element ->
            if (!element.used) {
                unusedCount++
                if (element.string.startsWith('getI18n_')) {
                    out.append(element.string.substring(8) + '\n')
                } else {
                    out.append(element.string + '\n')
                }
            }
        }

        def reportFile = project.file('build/unusedResourcedStringsReport.txt')

        reportFile.write(out.toString())

        println("""

        You have ${resourcedStrings.size()} resourced strings and ${unusedCount} are unused.

        A list of all the unused strings has been generated in:
        ${reportFile.path}

        """)
    }
}
