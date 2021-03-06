# Easily extract all compressed file types
extract () {
   if [ -f "$1" ] ; then
       case $1 in
           *.tar.bz2)   tar xvjf -- "$1"    ;;
           *.tar.gz)    tar xvzf -- "$1"    ;;
           *.bz2)       bunzip2 -- "$1"     ;;
           *.rar)       unrar x -- "$1"     ;;
           *.gz)        gunzip -- "$1"      ;;
           *.tar)       tar xvf -- "$1"     ;;
           *.tbz2)      tar xvjf -- "$1"    ;;
           *.tgz)       tar xvzf -- "$1"    ;;
           *.zip)       unzip -- "$1"       ;;
           *.Z)         uncompress -- "$1"  ;;
           *.7z)        7z x -- "$1"        ;;
           *)           echo "don't know how to extract '$1'..." ;;
       esac
   else
       echo "'$1' is not a valid file"
   fi
}

# Get weather data for Bristol
weather() {
    echo BRISTOL:
    curl -s "http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query=${@:-BS8+1JG}" | perl -ne 's/&amp;deg;/°/g;/<title>([^<]+)/&&printf "%s: ",$1;/<fcttext>([^<]+)/&&print $1,"\n"';
}

# Define a word using collinsdictionary.com
define() {
  curl -s "http://www.collinsdictionary.com/dictionary/english/$*" | sed -n '/class="def"/p' | awk '{gsub(/.*<span class="def">|<\/span>.*/,"");print}' | sed "s/<[^>]\+>//g";
}

# Epoch time conversion
epoch() {
  TESTREG="[\d{10}]"
  if [[ "$1" =~ $TESTREG ]]; then
    # is epoch
    date -d @$*
  else
    # is date
    if [ $# -gt 0 ]; then
      date +%s --date="$*"
    else
      date +%s
    fi
  fi
}

