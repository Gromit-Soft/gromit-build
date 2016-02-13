/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/*
 * CreateLegendFiles task creates legend.css and legend.html template files
 */
class CreateLegendFilesTask extends DefaultTask  {
    @TaskAction
    def action() {
        println ':createLegendFiles'
        def jsonSlurper = new groovy.json.JsonSlurper();
        def reader = new BufferedReader(new FileReader(project.createLegendFiles.legendFile))
        def parsedData = jsonSlurper.parse(reader)
        def imagesList = new ArrayList<LegendImage>()
        parsedData.legendImages.each {
            lImage ->
                def image = new LegendImage(lImage.path, lImage.description, lImage.separator, lImage.locstring)
                imagesList.add(image)
        }
        //imagesList.each {lImage -> println lImage}

        // css and views folders
        def cssDir = new File(project.projectDir.path, project.createLegendFiles.cssPath)
        def viewDir = new File(project.projectDir.path, project.createLegendFiles.viewPath)

        // legend.css file
        def legendCss = new StringBuffer();
        def lcssFile = new File(cssDir, 'legend.css')
        def legendCssFile = project.file(lcssFile.path)

        //legend.html
        def legendHtml = new StringBuffer();
        def lhtmlFile = new File(viewDir, 'legend.html')
        def legendHtmlFile = project.file(lhtmlFile.path)
        // add title and close button
        legendHtml.append('<div class="legendHeader">\n' +
                '   {{i18n.legend}}\n' +
                '   <a class="close" href="#" title="{{i18n.CLOSE}}" ng-click="close()">x</a>\n' +
                '</div>\n')
        // add body
        legendHtml.append('<div class="legendBody">\n')
        legendHtml.append('<table><tbody>\n')

        // css template line
        def cssLine = '.{imageClass} {\n' +
                '    background: transparent url("{imagePath}") no-repeat scroll 0 0 /16px auto;\n' +
                '    height: 22px;\n' +
                '    display: inline-block;\n' +
                '    padding-left: 25px;\n' +
                '    color: #808080;\n' +
                '}'

        // html template line
        def htmlLine = '<tr><td class="{imageClass}">{imageText}</td></tr>'

        imagesList.each { image ->
            if (image.separator) {
                legendHtml.append('<tr><td>&nbsp;</td></tr>\n')
            } else {
                // check if the file exists
                def imageFile = new File(cssDir.path, image.path)
                if (imageFile.exists()) {
                    //println 'File ' + imageFile.path + ' exists'
                    // get image file name
                    def imageName = image.path.substring(image.path.indexOf('/') + 1, image.path.lastIndexOf('.'))
                    // generate lines for css and html files
                    def cssl = cssLine.replace('{imageClass}', 'l_' + imageName).replace('{imagePath}', image.path)
                    legendCss.append(cssl + '\n')
                    // use localized string or original description if the local does not set
                    def htmll = ''
                    if (image.locstring) {
                        htmll = htmlLine.replace('{imageClass}', 'l_' + imageName).replace('{imageText}', image.locstring)
                    } else {
                        println 'Image ' + imageName + ' does not have localized description'
                        htmll = htmlLine.replace('{imageClass}', 'l_' + imageName).replace('{imageText}', image.description)
                    }

                    legendHtml.append(htmll + '\n')
                } else {
                    println 'File ' + imageFile.path + ' does not exists. Please remove it from legend.json file'
                }
            }
        }

        // write to files
        legendCssFile.write(legendCss.toString())
        legendHtml.append('</tbody></table>\n')
        legendHtml.append('</div>')
        legendHtmlFile.write(legendHtml.toString())
//        println("""
//        A list of all the used images has been generated in:
//        ${legendCssFile.path}  and  ${legendHtmlFile.path}
//        """)
    }
}

class LegendImage {
    def path
    def description
    def locstring
    def separator

    LegendImage (path, description, separator, locstring) {
        this.separator = separator;
        this.path = path
        this.description = description
        this.locstring = locstring
    }

    def String toString() {
        return "Path: " + this.path +
                ", Description: " + this.description + ", Localized string: " + this.locstring +
                ", Separator: " + this.separator
    }
}