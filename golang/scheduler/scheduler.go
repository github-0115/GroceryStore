package main

import (
	"fmt"
	"runtime"
	"time"
)

func main() {
	SchedulerTicketInner()
	time.Sleep(35 * time.Second)
}

func SchedulerTicketInner() {

	ticker := time.NewTicker(1 * time.Second)
	go func() {
		defer func() {
			if r := recover(); r != nil {
				fmt.Println("--!!-- SchedulerTicker_inner recover : %v", r)
			}
		}()

		i := 1
		fmt.Println("--!!-- SchedulerTicker_inner start : %d Ticker.C : %s  runtime NumGoroutine : %d !!!", i, time.Now(), runtime.NumGoroutine())
		for t := range ticker.C {
			fmt.Println("--!!-- SchedulerTicker_inner ticker :", time.Now())
			select {
			//			case "":
			//				fmt.Println("--!!-- SchedulerTicker_inner Ticker.C : %s runtime NumGoroutine : %d stop !!!", t.String(), runtime.NumGoroutine())

			default:
				fmt.Println(i, "--!!-- SchedulerTicker_inner : ", t.String(), "  NumGoroutine : ", runtime.NumGoroutine())
				go DDD()
				if i == 10 {
					return
				}
				i++
			}
		}
	}()

	fmt.Println("--!!-- SchedulerTicker end runtime NumGoroutine : %d", runtime.NumGoroutine(), "  NumGoroutine : ", runtime.NumGoroutine())
}

func DDD() {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("--!!-- DDD recover : %v", r)
		}
	}()
	var user *User
	time.Sleep(10 * time.Second)
	fmt.Println("--!!-- --!!-- --!!-- --!!-- --!!-- --!!-- --!!-- --!!-- ", user)
}

type User struct {
	Name string
}
