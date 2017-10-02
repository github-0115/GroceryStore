package datas

import (
	"encoding/json"
	"fmt"
	model "simulate/models"
)

func StructToString(st interface{}) string {
	bytes, err := json.Marshal(st)
	if err != nil {
		fmt.Println(3, "%s", "----json.marshal----"+err.Error())
		return ""
	}
	return string(bytes)
}

func StringToStruct(str string) []*model.JobOrderData {
	var data []*model.JobOrderData
	err := json.Unmarshal([]byte(str), &data)
	if err != nil {
		fmt.Println(3, "%s", "----json.Unmarshal----"+err.Error())
		return nil
	}
	return data
}
