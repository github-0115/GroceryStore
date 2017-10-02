package operate

import (
	"fmt"
	"os"
	m "simulate/models"

	_ "github.com/go-sql-driver/mysql"
	"github.com/go-xorm/core"
	"github.com/go-xorm/xorm"
)

func CreateEngine() (engine *xorm.Engine, err error) {
	dbUser := "dbuser"
	dbPass := "DBuser123!"
	dbHost := "10.1.200.10"
	dbPort := 3306
	dbName := "wxy_test"

	//	dbUser := "root"
	//	dbPass := "root"
	//	dbHost := "127.0.0.1"
	//	dbPort := 3306
	//	dbName := "test"

	//	dbUser := "dbuser"
	//	dbPass := "DBuser123!"
	//	dbHost := "10.1.60.238"
	//	dbPort := 3306
	//	dbName := "uyun_show"

	//	dbUser := "dbuser"
	//	dbPass := "DBuser123!"
	//	dbHost := "10.1.53.45"
	//	dbPort := 3306
	//	dbName := "uyun_show"
	dsn := fmt.Sprintf("%s:%s@tcp(%s:%d)/%s?useSSL=true&verifyServerCertificate=false", dbUser, dbPass, dbHost, dbPort, dbName)

	engine, err = xorm.NewEngine("mysql", dsn)
	if err != nil {
		fmt.Println("xorm new engine err :%s", err.Error())
		return engine, err
	}
	fmt.Println("db ping err : %v", engine.Ping())
	//	ShowTables(engine)

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

func GetField(dataSetId string, engine *xorm.Engine) ([]*m.DataSetField, error) {
	fields := make([]*m.DataSetField, 0)
	sess := engine.Where(" data_set_id=?", dataSetId)
	err := sess.Find(&fields)
	if err != nil {
		fmt.Println("data_set_id data err:", err.Error())
		return fields, err
	}
	return fields, err
}

func Insert(table interface{}, engine *xorm.Engine) error {
	_, err := engine.Insert(table)
	if err != nil {
		fmt.Println("insert table data err:", err.Error())
		return err
	}
	return nil
}

func Delete(engine *xorm.Engine, id string, table interface{}) error {
	_, err := engine.Where("id=?", id).Delete(table)
	//	_, err := engine.Id(id).Delete(table)
	if err != nil {
		fmt.Println("delete table data err:", err.Error())
		return err
	}
	return nil
}

func Update(engine *xorm.Engine, id string, table interface{}, condiBeans ...interface{}) error {
	_, err := engine.Where("id=?", id).Update(table)
	//	_, err := engine.Id(id).Update(table, condiBeans)
	if err != nil {
		fmt.Println("update table data err:", err.Error())
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
