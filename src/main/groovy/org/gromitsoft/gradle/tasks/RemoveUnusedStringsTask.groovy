/*******************************************************************************
 *
 * Copyright 2015 Gromit Soft Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
