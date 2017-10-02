package models

import (
	"time"
)

type SimulateData struct {
	Id    string `json:"id"`
	Class string `json:"class"`
	Datas string `json:"datas"`
}

type JobOrders struct {
	Data []*JobOrderData `json:"data"`
}

type JobOrderData struct {
	CPU       float32   `json:"cpu"`
	MEN       float32   `json:"men"`
	Min       float32   `json:"min"`
	Max       float32   `json:"max"`
	ReqServer int       `json:"reqServer"`
	Change    int       `json:"Change"`
	Event     int       `json:"event"`
	Time      time.Time `json:"time"`
	TimeStr   string    `json:"timeStr"`
	TimesTemp int64     `json:"timesTemp"`
}

type DataSetField struct {
	Id          string `json:"id"`
	DataSetId   string `json:"dataSetId"`
	FieldName   string `json:"fieldName"`
	DisplayName string `json:"displayName"`
	Index       int    `json:"index"`
	DataType    string `json:"dataType"`
	Total       int    `json:"total"`
	IsEnable    bool   `json:"isCheck"` //默认是启用，值为1
}

type GetDataSetFieldByIdQuery struct {
	DataSetId string
	Result    []*DataSetField
}
