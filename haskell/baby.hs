doubleMe x = x + x
doubleUs x y = doubleMe x + doubleMe y
isFirstLarger x y = if x > y then True else False

reverseList [] = []
reverseList (l:ls) = (last ls) ++ (l:(tail ls))


