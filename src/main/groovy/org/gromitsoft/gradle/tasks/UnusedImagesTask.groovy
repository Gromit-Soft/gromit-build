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
 * The findUnusedImages task find unused images in .js files
 */
class UnusedImagesTask extends DefaultTask  {
    @TaskAction
    def action() {
        println ':finding unused images'

        def images = []
        def basePathLength = (project.projectDir.path + new File(project.findUnusedImages.cssPath).path).length() + 1

        project.findUnusedImages.imageFiles.each { element ->
            images.add(element.getPath().substring(basePathLength).replace('\\', '/'))
        }

        project.findUnusedImages.cssFiles.each { element ->
            if (element.exists()) {
                element.eachLine { line, count ->

                    if (line.indexOf('url(') > -1) {

                        def index = line.indexOf('url(') + 4
                        def image = line.substring(index, line.indexOf(')', index))

                        if (image.startsWith('"') || image.startsWith('\'')) {
                            image = image.substring(1);
                        }

                        if (image.endsWith('"') || image.endsWith('\'')) {
                            image = image.substring(0, image.length() - 1);
                        }

                        def iter = images.iterator()
                        while (iter.hasNext()) {
                            def cssImage = iter.next()
                            if (image.equals(cssImage)) {
                                iter.remove()
                                break
                            }
                        }
                    }
                }
            }
        }

        def out = new StringBuffer();

        images.each { element ->
            out.append(project.findUnusedImages.cssPath + element + '\n')
        }

        def reportFile = project.file('unusedImagesReport.txt')

        reportFile.write(out.toString())

        println("""

        You have ${images.size()} unused images.

        A list of all the unused images has been generated in:
        ${reportFile.path}

        """)
    }
}
