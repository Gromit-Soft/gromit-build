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

/*
 * This task generates a JSON file containing information about the current build.
 * This JSON file ends up being part of the WAR which creates a very simple REST
 * endpoint that the client can use to show information about the current build in
 * the about dialog.
 */

class BuildInfoTask extends DefaultTask {
    @TaskAction
    def action() {
        println ':buildInfo'
        def path = project.projectDir;
        org.spiffyui.build.RevisionInfoBean revInfo = org.spiffyui.build.RevisionInfoUtil.getRevisionInfo(project.file(path))
        def now = new Date()
        def builder = new groovy.json.JsonBuilder()
        def root = builder {
            schema 1
            revision {
                number revInfo.getRevNumber()
                date revInfo.getRevDate()
            }
            dir path.toString()
            date now.getTime()
            user System.properties['user.name']
            version project.gromitPluginData.version
        }

        def f = new File(project.projectDir, project.generateBuildInfo.fileName)
        f.createNewFile()
        f.write(builder.toString())
    }
}