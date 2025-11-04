#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Start Up Batch script
#
# Required ENV vars:
# JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
# M2_HOME - location of maven2's installed home dir
# MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
# MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
# MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
# set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
# MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ] ; then

  if [ -f /etc/mavenrc ] ; then
    . /etc/mavenrc
  fi

  if [ -f "$HOME/.mavenrc" ] ; then
    . "$HOME/.mavenrc"
  fi

fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
mingw=false
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  MINGW*) mingw=true;;
  Darwin*) darwin=true
    # Use /usr/libexec/java_home if available, otherwise fall back to /Library/Java/Home
    # See https://developer.apple.com/library/mac/qa/qa1170/_index.html
    if [ -z "$JAVA_HOME" ]; then
      if [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="`/usr/libexec/java_home`"
      else
        export JAVA_HOME="/Library/Java/Home"
      fi
    fi
    ;;
esac

export MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-`pwd`}
if [ -z "$MAVEN_PROJECTBASEDIR" ] ; then
  echo "Unable to find maven project base directory"
  exit 1;
fi

##########################################################################################
# Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
# This allows using the maven wrapper in projects that prohibit checking in binary data.
##########################################################################################
if [ -r "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" ] ; then
    if [ "$MVNW_VERBOSE" = true ] ; then
      echo "Found $MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
    fi
else
    if [ "$MVNW_VERBOSE" = true ] ; then
      echo "Couldn't find $MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar, downloading it ..."
    fi
    if command -v wget > /dev/null; then
        ASYNC_DOWNLOADER="wget"
    elif command -v curl > /dev/null; then
        ASYNC_DOWNLOADER="curl"
    else
        echo "Error: Maven Wrapper downloaded requires either wget or curl." 1>&2
        exit 1
    fi
    MVNW_REPOURL="${MVNW_REPOURL:-https://repo.maven.apache.org/maven2}"
    MVNW_ARTIFACT_PATH="org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    if [ "$ASYNC_DOWNLOADER" = "wget" ] ; then
        wget "$MVNW_REPOURL/$MVNW_ARTIFACT_PATH" -O "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" || exit 1
    else
        curl -o "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" "$MVNW_REPOURL/$MVNW_ARTIFACT_PATH" || exit 1
    fi
fi
##########################################################################################
# End of extension
##########################################################################################

export WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
export WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

export DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

MAVEN_CMD_LINE_ARGS="$*"

exec "$JAVA_HOME/bin/java" \
  $MAVEN_OPTS \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
  $WRAPPER_LAUNCHER $MAVEN_CMD_LINE_ARGS


