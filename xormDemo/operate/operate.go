package operate

import (
	"fmt"
	"os"

	_ "github.com/go-sql-driver/mysql"
	"github.com/go-xorm/core"
	"github.com/go-xorm/xorm"
)

func CreateEngine() (engine *xorm.Engine, err error) {

	//	dbUser := "root"
	//	dbPass := "root_123"
	//	dbHost := "10.1.200.10"
	//	dbPort := 3306
	//	dbName := "uyun_show"
	dbUser := "root"
	dbPass := "root"
	dbHost := "127.0.0.1"
	dbPort := 3306
	dbName := "test"
	dsn := fmt.Sprintf("%s:%s@tcp(%s:%d)/%s", dbUser, dbPass, dbHost, dbPort, dbName)

	engine, err = xorm.NewEngine("mysql", dsn)
	if err != nil {
		fmt.Println("xorm new engine err :%s", err.Error())
		return engine, err
	}
	fmt.Println("db ping err : %v", engine.Ping())
	ShowTables(engine)

	//日志输出重定向
	f, err := os.Open("sql.log")
	if err != nil {
		f, err = os.Create("sql.log")
		if err != nil {
			fmt.Println("create sql.log err :%s", err.Error())
			return engine, err
		}
	}

	engine.ShowSQL(true)
	engine.SetLogger(xorm.NewSimpleLogger(f))
	engine.SetMapper(core.SnakeMapper{})

	return engine, nil
}

func ImportFile(fpath string, engine *xorm.Engine) error {
	res, err := engine.ImportFile(fpath)
	if err != nil {
		fmt.Println("import inner data err:", err.Error())
		return err
	}
	fmt.Println(res)
	return nil
}

func Insert(table interface{}, engine *xorm.Engine) error {
	_, err := engine.Insert(table)
	if err != nil {
		fmt.Println("insert table data err:", err.Error())
		return err
	}
	return nil
}

func IsTableEmpty(table interface{}, engine *xorm.Engine) {
	isEmpty, err := engine.IsTableEmpty(table)
	if err != nil {
		fmt.Println("check table%v isempty err:%s", table, err.Error())
	}
	fmt.Println("table : ", table, "isempty :", isEmpty)
}

func CreateTable(table interface{}, engine *xorm.Engine) error {
	isExist, err := engine.IsTableExist(table)
	if err != nil {
		fmt.Println("create table check isexist err:%s", err.Error())
		return nil
	}
	if isExist {
		return nil
	}

	err = engine.CreateTables(table)
	if err != nil {
		fmt.Println("create table err:%s", err.Error())
		return err
	}
	return nil
}

func Sync(engine *xorm.Engine, tables ...interface{}) error {
	err := engine.Sync2(tables)
	if err != nil {
		fmt.Println("sync table err:", err.Error())
		return err
	}
	return nil
}

func ShowTables(engine *xorm.Engine) {
	tables, err := engine.DBMetas()
	if err != nil {
		fmt.Println("get db info err :%s", err.Error())
		return
	}

	for _, table := range tables {
		IsTableEmpty(table.Name, engine)
	}

}
