package datas

import (
	model "simulate/models"
	util "simulate/util"
	"time"
)

func JobOrderTemp(JobDatasTemp []*model.JobOrderData) []*model.JobOrderData {

	data1 := util.GenerateRepeatRandomNumber(0, 100, 8)
	data2 := util.GenerateRandomNumber(9, 61, 8)
	data3 := util.GenerateRandomNumber(5, 42, 8)
	data4 := util.GenerateFloatRandomNumber(0, 100, 8)
	if JobDatasTemp == nil {

		JobDatasTemp = make([]*model.JobOrderData, 0)

		timeNow := time.Now()
		for i := 7; i >= 0; i-- {
			t := timeNow.Add(time.Duration(i) * (-10) * time.Second)
			value := &model.JobOrderData{
				Time:      t,
				TimeStr:   t.Format("15:04:05"),
				TimesTemp: t.Unix() * 1000,
				ReqServer: data1[i],
				Change:    data2[i],
				Event:     data3[i],
				CPU:       data4[i],
				MEN:       data4[i],
				Min:       data4[i],
				Max:       data4[i],
			}
			JobDatasTemp = append(JobDatasTemp, value)
		}

		return JobDatasTemp

	} else { //第二次访问

		fields := make([]*model.JobOrderData, 0)
		for i := 1; i < len(JobDatasTemp); i++ {
			fields = append(fields, JobDatasTemp[i])
		}

		timeNow := fields[len(fields)-1].Time.Add(10 * time.Second)
		data := util.GenerateRepeatRandomNumber(0, 100, 3)
		dataf := util.GenerateFloatRandomNumber(0, 100, 4)
		value := &model.JobOrderData{
			Time:      timeNow,
			TimeStr:   timeNow.Format("15:04:05"),
			TimesTemp: timeNow.Unix() * 1000,
			ReqServer: data[0],
			Change:    data[1],
			Event:     data[2],
			CPU:       dataf[0],
			MEN:       dataf[1],
			Min:       dataf[2],
			Max:       dataf[3],
		}
		fields = append(fields, value)
		return fields
	}

	return JobDatasTemp
}
