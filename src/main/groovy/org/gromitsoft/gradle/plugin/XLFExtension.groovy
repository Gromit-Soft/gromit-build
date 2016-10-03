/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.plugin

class XLFExtension {
    public static final String NAME = 'xlf'

    String xlfFile = 'src/main/xlf/ArRsrc_en.xlf'
    String i18nPath = 'src/main/app/js/services/i18n/'
    String appName
    String jsKeywords
    String xlfPath
    String l10nPath
}