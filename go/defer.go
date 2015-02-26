package main

import "fmt"

func main() {
	i := 0
	for ; i < 5; i++ {
		defer fmt.Println("i = %d", i)
	}
}
