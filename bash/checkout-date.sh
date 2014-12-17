#!/bin/bash

# ./checkout-date.sh DATE
#
# Checkout the last commit on the given date for all git repositories 
# found underneath the current working directory.
#

if [ "$#" -eq 0 ] || [[ "$1" == "-h" ]]; then
    echo
    echo "Usage: checkout-date.sh DATE"
    echo 
    echo "In each repository, the last commit on the specified"
    echo "date will be pulled from the master branch"
    echo
    echo "Example:"
    echo "   ./checkout-date.sh 2014-12-10"
    echo
    exit 2
fi

targetDate=$1

# Compute a list of sub directories that have a .git folder in them
# and use that as the list to update
GITS=`find . -type d -mindepth 2 -name .git`

# remove all the .git comopnents.  Note the double "//" which means to bash that
# this is a global replace
DIRS=${GITS//.git/}

for d in $DIRS; do ## FOR EACH REPO
    echo "================================"
    echo "Updating repository $d"
    pushd $d > /dev/null # redirect out to /dev/null
    
    date=`date -j -f "%Y-%m-%d" $targetDate "+%s"`
    git checkout `git rev-list -1 --before="$date" master`
    git pull origin master

    popd > /dev/null # Recover
done
echo "================================"
echo "Finished $0"

