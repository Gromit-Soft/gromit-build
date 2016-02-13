/*******************************************************************************
 * 
 * MIT License
 * Copyright (c) 2015-2016 NetIQ Corporation, a Micro Focus company
 *
 ******************************************************************************/

package org.gromitsoft.gradle.plugin

import org.gradle.api.file.FileCollection

class UnusedImagesExtension {
    public static final String NAME = 'findUnusedImages'

    FileCollection imageFiles
    FileCollection cssFiles
    String cssPath = '/src/main/app/css'
}