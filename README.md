Gromit-build
==================================================

Build artifacts for the Gromit project

Building Gromit-build
--------------------------------------

Build gromit-plugin and install it to the lcal maven repository:

<pre><code>gradle build
gradle install
</code></pre>

Using Gromit-build
--------------------------------------

Gradle gromit-plugin plugin contains tasks:
  
  - checkCopyright
  
  - generateBuildInfo
  
  - checkForbiddenFunctions
  
  - findUnusedImages
  
  - unusedStrings
  
  - xlf
  
See pluginExample.gradle file for more details.



  
