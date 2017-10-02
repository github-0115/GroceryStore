package main

import (
	//	model "demo/xormDemo/models"
	operate "demo/xormDemo/operate"
	"fmt"

	//	"github.com/satori/go.uuid"
)

//var engine *xorm.Engine

func main() {

	engine, err := operate.CreateEngine()
	if err != nil {
		fmt.Println("xorm new engine err :%s", err.Error())
		return
	}

	//	resource := new(model.InnerDataResource)
	//	err = operate.CreateTable(resource, engine)
	//	if err != nil {
	//		fmt.Println("create resource table err:%s", err.Error())
	//	}
	operate.ImportFile("./file/built-in-data_zh.sql", engine)
	operate.ShowTables(engine)
	//	engine.Sync2(resource)
	//	set := new(model.InnerDataSet)
	//	resources := make([]*model.InnerDataResource, 0)
	//	resource.DateResourceId = uuid.NewV4().String()
	//	resource.DateResourceCode = "innerData"
	//	resource.DateResourceName = "UYUN内置数据源"
	//	resources = append(resources, resource)

	//	resource1 := &model.InnerDataResource{
	//		DateResourceId:   uuid.NewV4().String(),
	//		DateResourceCode: "innerData1",
	//		DateResourceName: "UYUN内置数据源1",
	//	}
	//	resources = append(resources, resource1)
	//	err = operate.Insert(resources, engine)
	//	if err != nil {
	//		fmt.Println("insert resource table  data err:%s", err.Error())
	//	}

	//	resource2 := &model.InnerDataResource{
	//		DateResourceCode: "innerData",
	//	}
	//	has, err := engine.Get(&resource2)
	//	if err != nil {
	//		fmt.Println("get rescoure :has :", has, "err:", err.Error())
	//	}
	//	fmt.Println(resource2)

	//	resources := make([]*model.InnerDataResource, 0)
	//	//	err = engine.Where("date_resource_code = ?", "innerData").Find(&resources)
	//	err = engine.Find(&resources)
	//	if err != nil {
	//		fmt.Println("find rescoure err:", err.Error())
	//	}
	//	for _, resource := range resources {
	//		fmt.Println(resource)
	//	}

}
