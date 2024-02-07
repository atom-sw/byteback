#!/bin/sh

find . -name ".settings" | xargs rm -r
find . -name ".project" | xargs rm
find . -name ".classpath" | xargs rm
