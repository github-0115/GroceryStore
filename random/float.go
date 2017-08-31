package random

import (
	"encoding/json"
	"fmt"
	"strconv"
)

/**
*float64保留小数点后3位怎么弄
 */

func baoliuxiaoshu() {

	f1 := "0.35678"
	f3, _ := strconv.ParseFloat(f1, 64)
	fmt.Println(f3)

	//保留位数
	f2, _ := fmt.Printf("%.2f", f3)
	fmt.Println(f2)

	//保留位数，四舍五入
	a := strconv.FormatFloat(f3, 'f', -1, 64)
	fmt.Println(a)
	a = strconv.FormatFloat(f3, 'f', 1, 64)
	fmt.Println(a)
	a = strconv.FormatFloat(f3, 'f', 2, 64)
	fmt.Println(a)
	a = strconv.FormatFloat(f3, 'f', 3, 64)
	fmt.Println(a)
	a = strconv.FormatFloat(f3, 'f', 4, 64)
	fmt.Println(a)
}

func InterfaceToString(i interface{}, prec int) string {

	b, err := json.Marshal(i)
	if err != nil {
		fmt.Println("interface to string err:%s", err.Error())
		return ""
	}

	f3, err := strconv.ParseFloat(string(b), 64)
	if err != nil {
		fmt.Println("string %s to float err:%s", string(b), err.Error())
		return ""
	}

	return strconv.FormatFloat(f3, 'f', prec, 64)
}
