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
 * Generate the properties files for field localization into the build/l10n directory.
 */
class GeneratePropertiesFromXLFTask extends DefaultTask  {
    @TaskAction
    def action() {
        println ':generatePropertiesFromXLF'
        def path = project.projectDir;

        project.copy {
            from project.xlf.xlfFile
            into project.xlf.i18nPath
        }

        def words = []
        new File(path, project.xlf.jsKeywords).eachLine { line ->
            words << line
        }

        def enKeys = [:]

        new File(path, project.xlf.l10nPath).mkdirs();

        /*
         * When we generate the localization service we need to have a key in each language
         * for every value referenced in the code.  It often happens that we change the English
         * language files and then wait a while before doing the translation into other languages.
         * For the time that we have strings in the English XLF file while they aren't in the other
         * files we need to use the English strings.  We do that by creating a map of the English
         * strings and adding each of those strings to the localized map if we don't have them.
         */
        new File(path, project.xlf.xlfPath).eachFile { file ->
            if (file.name.endsWith('_en.xlf')) {
                def xlf = new XmlParser().parse(file)
                xlf.file.body.'trans-unit'.each { tu ->
                    if (words.contains(tu.attribute('id').toLowerCase())) {
                        throw new GradleScriptException('"' + tu.attribute('id') + '" in the file ' + file.name +
                                ' is a JavaScript keyword and may not be used as the id of an XLF string.', null);
                    }

                    if (enKeys.containsKey(tu.attribute('id'))) {
                        throw new GradleScriptException('The key ' + tu.attribute('id') + ' in the file ' + file.path + ' has already been used.', null)
                    }

                    if (!tu.attribute('id').equals(tu.attribute('resname'))) {
                        throw new GradleScriptException('The key ' + tu.attribute('id') +
                                ' and the resource name ' + tu.attribute('resname') + ' in the file ' + file.path +
                                ' were used for the same resource and did not match.', null)
                    }

                    enKeys.put(tu.attribute('id'), tu.text())
                }
            }
        }

        if (enKeys.size() == 0) {
            throw new GradleScriptException('An English language XLF file is required for the build.', null)
        }

        new File(path, project.xlf.xlfPath).eachFile { file ->
            if (file.name.startsWith('.')) {
                return;
            } else if (!file.name.endsWith('.xlf') || file.name.indexOf('_') == -1) {
                logger.warn('The file ' + file.name + ' was in the XLF directory and does not look like an XLF file.')
            } else {
                logger.info ('Generating Properties for XLF file: ' + file.name)
                def locale = file.name.substring(file.name.indexOf('_') + 1, file.name.indexOf('.xlf'))

                def f = new File(new File(path, project.xlf.l10nPath), 'ArRsrc_' + locale + '.properties')
                if (!f.exists() || file.lastModified() > f.lastModified()) {
                    f.createNewFile()
                    def out = new StringBuffer();
                    def xlf = new XmlParser().parse(file)
                    def keys = [:]
                    xlf.file.body.'trans-unit'.each { tu ->
                        keys.put(tu.attribute('id'), tu.text())
                    }

                    for (v in enKeys) {
                        if (keys[v.key] == null) {
                            keys.put(v.key, enKeys[v.key])
                        } else if (enKeys[v.key].indexOf('{0}') == -1 && keys[v.key].indexOf('{0}') > -1) {
                            /*
                             * This means the English string has no parameters, but the other language one
                             * does.  We want to use the English one.
                             */
                            keys.put(v.key, enKeys[v.key])
                        } else if (enKeys[v.key].indexOf('{0}') > -1 && keys[v.key].indexOf('{0}') == -1) {
                            /*
                             * This means the English string has no parameters and the other language one does
                             * have parameters.  We want to use the English one in that case.
                             */
                            keys.put(v.key, enKeys[v.key])
                        }
                    }

                    for (v in keys) {
                        out.append(v.key + '=')
                        out.append(keys[v.key].replaceAll('"', '\\"').replaceAll('\n', '').replaceAll('\r', ''))
                        out.append('\n')
                    }

                    f.write(out.toString())
                }
            }
        }
    }
}
