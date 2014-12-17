#!/bin/bash

# ./date-walk.sh start-date end-date
#
# TODO: desc.
#

if [ "$#" -eq 0 ] || [[ "$1" == "-h" ]]; then
    echo
    echo "Usage: date-walk.sh start-date end-date"
    echo 
    echo "In each repository, will take these actions:"
    echo "1) TODO"
    echo
    echo "Example:"
    echo "   ./date-walk.sh TODO1 TODO2"
    echo
    exit 2
fi

startdate=$1
enddate=$2

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
    
    # Setup the date boundaries
    sDateTs=`date -j -f "%Y-%m-%d" $startdate "+%s"`
	eDateTs=`date -j -f "%Y-%m-%d" $enddate "+%s"`
	dateTs=$sDateTs
	offset=604800

	# Walk over each date and perform the needed
	while [ "$dateTs" -le "$eDateTs" ] ## FOR EACH DATE IN THE SKIP LIST (not an actual skip list)
	do
	  date=`date -j -f "%s" $dateTs "+%Y-%m-%d"`
	  echo Checkout out last committed version on: $date

	  git checkout `git rev-list -1 --before="$date" master`
	  git pull origin master

	  ## USE THE REPO AS NEEDED HERE

	  dateTs=$(($dateTs+$offset))
	done

    popd > /dev/null # Recover
done
echo "================================"
echo "Finished $0"

