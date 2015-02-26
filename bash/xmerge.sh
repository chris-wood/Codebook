echo $1 $2 
plutil --convert xm1 -- $1 > .f1
plutil --convert xm1 -- $2 > .f2

opendiff .f1 .f2 -merge .f3

if [[ $(plutil -lint .f3) != *OK* ]] then
	mv .f3 $1.new
fi

rm .f1
rm .f2
