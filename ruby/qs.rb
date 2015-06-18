# Enter your code here. Read input from STDIN. Print output to STDOUT

n = gets.to_i
ar = gets.chomp.split(" ").map{|s| s.to_i}

#puts n
#puts ar.join(" ")

def quicksort(ar, left, right)
    if (right - left == 0)
        return # already sorted, length of 0 or 1
    else
        pivot = ar[right]
        low = left
        
        index = left
        while index < right
            if ar[index] < pivot
                ar[left], ar[index] = ar[index], ar[left]
                left = left + 1
            end
            index = index + 1
        end
        
        ar[left], ar[right] = ar[right], ar[left]
        
        puts ar.join(" ")
        
        quicksort(ar, low, left - 1)
        quicksort(ar, left + 1, right)
        
    end
end

quicksort(ar, 0, ar.length() - 1)
