#!/bin/sh

showUsage () 
{ 
	echo "usage: ./check-xcode.sh [h?v]"
	echo "    -h/-?   display this menu"
	echo "    -v      enable verbose mode to display all build errors"
}

verbose=0

while getopts "h?v" opt; do
    case "$opt" in
    h|\?)
        showUsage
        exit 0
        ;;
    v)  verbose=1
        ;;
    esac
done

PROJECTLIST=$(find `pwd` . -type d -name '*.xcodeproj' -print | sed -e 's/^/,/g' -e 's/$/,/g')

echo $PROJECTLIST | sed -n 1'p' | tr ',' '\n' | while read file; do
	if [ ${#file} -gt 0 ]; then
		echo Building: $file
		xcodebuild -alltargets -project "$file" 2>> .errout 1>> /dev/null
		if [ $? -ne 0 ]; then
			echo "Build failed:"
			if [ $verbose -eq 1 ]; then	
				cat .errout
			fi
		fi
		rm -f .errout
	fi
done
