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
import groovy.xml.*

/*
 * The removeUnusedStrings task removes unused localized strings
 */
class RemoveUnusedStringsTask extends DefaultTask  {
    @TaskAction
    def action() {
        println ':remove unused localized strings'

        def reportFile = project.file('build/unusedResourcedStringsReport.txt')

        if (!reportFile.exists()) {
            throw new GradleScriptException("You must generate the unused strings report before running this command.", null);
        }

        def unusedStrings = []
        reportFile.eachLine { line, count ->
            unusedStrings.add(line)
        }

        def xlf = new XmlParser().parse(project.unusedStrings.xlfFile)
        def removeCount = 0

        xlf.file.body.'trans-unit'.each { tu ->
            def foundIt = false;
            unusedStrings.each { element ->
                if (element.equals(tu.attribute('id'))) {
                    foundIt = true;
                }
            }

            if (foundIt) {
                removeCount++
                tu.parent().remove(tu)
            }
        }

        project.unusedStrings.xlfFile.write(XmlUtil.serialize(xlf))

        println("""

        Removed ${removeCount} unused strings from ${project.unusedStrings.xlfFile.path}

        """)
    }
}
