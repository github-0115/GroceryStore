package models

type InnerDataResource struct {
	DateResourceId   string
	DateResourceCode string `json:"dateResourceCode"`
	DateResourceName string `json:"dateResourceName"`
}

type InnerDataSet struct {
	DatasetId   string
	DatasetCode string `json:"dataSetCode"`
	DatasetName string `json:"dataSetName"`
}

type InnerDataSetField struct {
	DatasetId   string
	FieldName   string `json:"fieldName"`
	DisplayName string `json:"displayName"`
	IsCheck     bool   `json:"isCheck"`
	DataType    string `json:"dataType"`
}
