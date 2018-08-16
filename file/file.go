package main

import (
	"fmt"
	"io"
	"io/ioutil"
	"os"
	"path/filepath"
	"strings"
)

var target_path string
var source_path string

func main() {

	source_path = "/home/uyun/桌面/test/omp/"
	target_path = "/home/uyun/桌面/test1/"

	//	err := copy_folder(source_path, target_path)
	//	if err != nil {
	//		log.Fatal(err)
	//	} else {
	//		fmt.Print("copy finish")
	//	}
	ss, _ := ListDir(source_path, "")
	for _, s := range ss {
		fmt.Print(s + "\n")
	}
	fmt.Print("copy finish\n")
	fmt.Print("copy finish\n")
	ss, _ = WalkDir("./", "")
	for _, s := range ss {
		fmt.Print(s + "\n")
	}
}

//获取指定目录下的所有文件，不进入下一级目录搜索，可以匹配后缀过滤。
func ListDir(dirPth string, suffix string) (files []string, err error) {
	files = make([]string, 0, 10)
	dir, err := ioutil.ReadDir(dirPth)
	if err != nil {
		return nil, err
	}
	PthSep := string(os.PathSeparator)
	suffix = strings.ToUpper(suffix) //忽略后缀匹配的大小写
	for _, fi := range dir {
		if fi.IsDir() { // 忽略目录
			continue
		}
		if strings.HasSuffix(strings.ToUpper(fi.Name()), suffix) { //匹配文件
			files = append(files, dirPth+PthSep+fi.Name())
		}
	}
	return files, nil
}

//获取指定目录及所有子目录下的所有文件，可以匹配后缀过滤。
func WalkDir(dirPth, suffix string) (files []string, err error) {
	files = make([]string, 0, 30)
	suffix = strings.ToUpper(suffix)                                                     //忽略后缀匹配的大小写
	err = filepath.Walk(dirPth, func(filename string, fi os.FileInfo, err error) error { //遍历目录
		//if err != nil { //忽略错误
		// return err
		//}
		if fi.IsDir() { // 忽略目录
			return nil
		}
		if strings.HasSuffix(strings.ToUpper(fi.Name()), suffix) {
			files = append(files, filename)
		}
		return nil
	})
	return files, err
}

func copy_folder(source string, dest string) (err error) {

	sourceinfo, err := os.Stat(source)
	if err != nil {
		return err
	}

	err = os.MkdirAll(dest, sourceinfo.Mode())
	if err != nil {
		return err
	}

	directory, _ := os.Open(source)

	objects, err := directory.Readdir(-1)

	for _, obj := range objects {

		sourcefilepointer := source + "/" + obj.Name()

		destinationfilepointer := dest + "/" + obj.Name()

		if obj.IsDir() {
			err = copy_folder(sourcefilepointer, destinationfilepointer)
			if err != nil {
				fmt.Println(err)
			}
		} else {
			err = copy_file(sourcefilepointer, destinationfilepointer)
			if err != nil {
				fmt.Println(err)
			}
		}

	}
	return
}

func copy_file(source string, dest string) (err error) {
	sourcefile, err := os.Open(source)
	if err != nil {
		return err
	}

	defer sourcefile.Close()

	destfile, err := os.Create(dest)
	if err != nil {
		return err
	}

	defer destfile.Close()

	_, err = io.Copy(destfile, sourcefile)
	if err == nil {
		sourceinfo, err := os.Stat(source)
		if err != nil {
			err = os.Chmod(dest, sourceinfo.Mode())
		}

	}

	return
}

//获取指定目录下的所有文件和目录
func GetFilesAndDirs(dirPth string) (files []string, dirs []string, err error) {
    dir, err := ioutil.ReadDir(dirPth)
    if err != nil {
        return nil, nil, err
    }

    PthSep := string(os.PathSeparator)
    //suffix = strings.ToUpper(suffix) //忽略后缀匹配的大小写

    for _, fi := range dir {
        if fi.IsDir() { // 目录, 递归遍历
            dirs = append(dirs, dirPth+PthSep+fi.Name())
            GetFilesAndDirs(dirPth + PthSep + fi.Name())
        } else {
            // 过滤指定格式
            ok := strings.HasSuffix(fi.Name(), ".go")
            if ok {
                files = append(files, dirPth+PthSep+fi.Name())
            }
        }
    }

    return files, dirs, nil
}

//获取指定目录下的所有文件,包含子目录下的文件
func GetAllFiles(dirPth string) (files []string, err error) {
    var dirs []string
    dir, err := ioutil.ReadDir(dirPth)
    if err != nil {
        return nil, err
    }

    PthSep := string(os.PathSeparator)
    //suffix = strings.ToUpper(suffix) //忽略后缀匹配的大小写

    for _, fi := range dir {
        if fi.IsDir() { // 目录, 递归遍历
            dirs = append(dirs, dirPth+PthSep+fi.Name())
            GetAllFiles(dirPth + PthSep + fi.Name())
        } else {
            // 过滤指定格式
            ok := strings.HasSuffix(fi.Name(), ".go")
            if ok {
                files = append(files, dirPth+PthSep+fi.Name())
            }
        }
    }

    // 读取子目录下文件
    for _, table := range dirs {
        temp, _ := GetAllFiles(table)
        for _, temp1 := range temp {
            files = append(files, temp1)
        }
    }

    return files, nil
}
