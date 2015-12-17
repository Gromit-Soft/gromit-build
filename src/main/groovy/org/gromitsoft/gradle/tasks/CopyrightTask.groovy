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

/*
 * The copyright task checks JavaScript source files to make sure that they have the 
 * copyright we require with the correct year.  This has to happen at the build time 
 * for JavaScript files instead of at commit time since some files with a .js extension
 * are third-party libraries which we can't add out copyright too.
 */
class CopyrightTask extends DefaultTask  {
    @TaskAction
    def action() {
        println ':checking copyrights'
        def path = project.projectDir;
        def copyright = []
        def yearIndex = -1;
        def year = '' + Calendar.getInstance().get(Calendar.YEAR)
        new File(path, project.checkCopyright.fileName).eachLine { line, count ->
            if (line.indexOf('{year}') > -1) {
                yearIndex = line.indexOf('{year}')
            }
            copyright << line
        }

        project.checkCopyright.files.each { element ->
            if (element.exists()) {
                //println element
                element.eachLine { line, count ->
                    if (count < copyright.size()) {
                        if (copyright[count - 1].indexOf('{year}') > -1) {
                            if (!line.startsWith(copyright[count - 1].substring(0, yearIndex))) {
                                throw new GradleScriptException('No copyright year ' + element.name, null);
                            } else if (line.substring(yearIndex, line.indexOf(' ', yearIndex)).indexOf(year) == -1) {
                                def yearLine = line.substring(yearIndex, line.indexOf(' ', yearIndex))
                                throw new GradleScriptException(element.getPath() + ': Incorrect copyright year.  The copyright year should be ' + year, null);
                            }
                        } else if (!line.equals(copyright[count - 1])) {
                            throw new GradleScriptException(element.getPath() + ': Missing copyright header. See the copyright header in src/build/copyright.txt', null);
                        }
                    }

                    /*
                     * We are piggy-backing off of the copyright check to check for tab characters.
                     */
                    if (line.indexOf('\t') > -1) {
                        throw new GradleScriptException(element.getPath() + ': has a tab character at line ' + count, null);
                    }
                }
            }
        }
    }
}
