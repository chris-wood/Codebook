def print_progress_bar(finished_percent)
  fixed_space = 9 # for braces and number

  width = `tput cols`.to_f - fixed_space

  finished_count = ((finished_percent*width)/100).ceil
  empty_count    = width - finished_count

  finished = "#" * finished_count
  empty    = "-" * empty_count

  print "\r[ #{finished}#{empty} ] #{finished_percent}% "
end

(0..100).each do |count|
  print_progress_bar(count)
  sleep 1
end

