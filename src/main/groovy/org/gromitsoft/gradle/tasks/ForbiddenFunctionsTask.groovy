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
 * This task runs the forbidden functions task on the project.  This task 
 * ensures that we do not call functions which are forbidden in controllers.
 * 
 * https://github.com/zgrossbart/forbiddenfunctions
 */
class ForbiddenFunctionsTask extends DefaultTask {
    @TaskAction
    def action() {
        println ':checkForbiddenFunctions'

        def args = []
        args.add('-funcs')

        args.add(new File(project.projectDir, project.checkForbiddenFunctions.fileName))

        project.checkForbiddenFunctions.files.each { f ->
            args.add(f)
            //println f
        }


        def errors = com.grossbart.forbiddenfunction.FFRunner.run(args as String[])

        if (errors.size > 0) {
            errors.each { err ->
                println err
            }
            throw new GradleScriptException("There were forbidden functions.  Look above and fix the issues.", null);
        }
    }
}
