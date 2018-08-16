package main

import (
	zip "GroceryStore/golang/zip-tar/zip"
	"fmt"
	"os"
)

func main() {
	//
	//	TestCompress()

	TestDeCompress()
}

func TestCompress() {
	f1, err := os.Open("C:\\Users\\weixy\\Desktop\\zip\\citymap\aaa.txt")
	if err != nil {
		fmt.Println(err.Error())
	}
	defer f1.Close()

	//	f1, err := os.Open("C:\\Users\\weixy\\Desktop\\zip\\citymap\aaa.json")
	//	if err != nil {
	//		fmt.Println(err.Error())
	//	}
	//	defer f1.Close()
	//	f2, err := os.Open("C:\\Users\\weixy\\Desktop\\zip\\citymap\aaa.hdr")
	//	if err != nil {
	//		fmt.Println(err.Error())
	//	}
	//	defer f2.Close()
	//	f3, err := os.Open("C:\\Users\\weixy\\Desktop\\zip\\citymap\aaa.dbf")
	//	if err != nil {
	//		fmt.Println(err.Error())
	//	}
	//	defer f3.Close()
	//	f4, err := os.Open("C:\\Users\\weixy\\Desktop\\zip\\citymap\aaa.shp")
	//	if err != nil {
	//		fmt.Println(err.Error())
	//	}
	//	defer f4.Close()
	var files = []*os.File{f1}
	//	var files = []*os.File{f1, f2, f3, f4}
	dest := "C:\\Users\\weixy\\Desktop\\zip\\citymap.zip"
	err = zip.Compress(files, dest)
	if err != nil {
		fmt.Println(err.Error())
	}
}

func TestDeCompress() {
	err := zip.DeCompress("C:\\Users\\weixy\\Desktop\\zip\\citymap.zip", "C:\\Users\\weixy\\Desktop\\zip\\test\\")
	if err != nil {
		fmt.Println(err.Error())
	}
}
