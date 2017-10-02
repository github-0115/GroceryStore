package main

import (
	"fmt"
	datas "simulate/datas"
	model "simulate/models"
	operate "simulate/operate"

	"github.com/satori/go.uuid"
)

func main() {
	//1 * * * * cd /home/uyun/gopath/src/simulate/ && ./simulate &>/home/uyun/gopath/src/simulate/logs/`date+\%Y\%m\%d.log`

	engine, err := operate.CreateEngine()
	if err != nil {
		fmt.Println("xorm new engine err :%s", err.Error())
		return
	}

	simulateData := new(model.SimulateData)
	err = operate.CreateTable(simulateData, engine)
	if err != nil {
		fmt.Println("create simulateData table err:%s", err.Error())
	}

	engine.Sync2(simulateData)

	simulateData = &model.SimulateData{
		Class: "broken_line",
	}

	has, err := engine.Get(simulateData)
	if err != nil {
		fmt.Println("get simulateData :has :", has, "err:", err.Error())
	}

	fmt.Println(simulateData)

	if !has {
		simulateData.Id = uuid.NewV4().String()
		data := datas.StructToString(datas.JobOrderTemp(nil))
		simulateData.Datas = data

		err = operate.Insert(simulateData, engine)
		if err != nil {
			fmt.Println("insert simulateData table  data err:%s", err.Error())
		}
	} else {

		jobOrderDatas := datas.StringToStruct(simulateData.Datas)
		simulateData.Datas = datas.StructToString(datas.JobOrderTemp(jobOrderDatas))

		err = operate.Update(engine, simulateData.Id, simulateData, "class", "datas")
		if err != nil {
			fmt.Println("Update simulateData table  data err:%s", err.Error())
		}
	}

	//	simulateDatas := make([]*model.SimulateData, 0, 0)
	//	err = engine.Find(&simulateDatas)
	//	if err != nil {
	//		fmt.Println("find simulateDatas err:", err.Error())
	//	}
	//	for _, simulateData := range simulateDatas {
	//		fmt.Println(simulateData)
	//	}

}
