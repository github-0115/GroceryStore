package http

import (
	"bytes"
	"crypto/tls"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/url"
)

func GetClassMoldsData(dataSourceId string, ip string, tenantId string, classLayerCode string, classCode string) (string, error) {

	v := url.Values{}
	v.Set("tenantId", tenantId)
	v.Add("moldDataSource", dataSourceId)
	v.Add("classLayerCode", classLayerCode)
	v.Add("classCode", classCode)

	u, err := url.Parse(ip + setting.ModelResouce + "?")
	if err != nil {
		log.Error(3, "url parse err:%s", err.Error())
		return "", err
	}
	u.RawQuery = v.Encode()

	//	urls = urls + setting.ModelResouce + "?tenantId=" + tenantId + "&moldDataSource=" + dataSourceId + "&classCode=" + classCode + "&classLayerCode=" + classLayerCode
	res, err := GetRequest(u.String())
	if err != nil {
		log.Error(3, " get %s Molds res err %s", classCode, err.Error())
		return "", err
	}

	return res, nil
}


func GetRequest(url string) (string, error) {
	if !strings.Contains(url, "http://") {
		url = "http://" + url
	}
	req, err := http.NewRequest("GET", setting.HTTPSTR+url, nil)
	if err != nil {
		log.Error(3, "get url :%s request err:%s", url, err.Error())
		return "", err
	}
	req.Header.Add("Accept-Encoding", "identity")
	req.Header.Add("Accept-Charset", "utf-8")

	client := GetHttpClient()
	resp, err := client.Do(req)
	if resp != nil {
		defer resp.Body.Close()
	}
	if err != nil {
		log.Error(3, "get data path :%s err:%s", setting.HTTPSTR+url, err.Error())
		return "", err
	}

	result, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Error(3, "ioutil read data err:%s", err.Error())
		return "", err
	}

	if resp.StatusCode != 200 {
		log.Error(3, "get conn status resp.StatusCode :%d ;err:%s", resp.StatusCode, string(result))
		return "", errors.New("get conn status err")
	}

	return string(result), nil
}


func PostData(url string, bodyByte []byte) (string, error) {
	if !strings.Contains(url, "http://") {
		url = "http://" + url
	}
	body := bytes.NewBuffer(bodyByte)

	req, err := http.NewRequest("POST", url, body)
	if err != nil {
		log.Error(3, "get post url :%s request err:%s", url, err.Error())
		return "", err
	}
	req.Header.Add("Content-Type", "application/json")

	client := GetHttpClient()
	resp, err := client.Do(req)
	if resp != nil {
		defer resp.Body.Close()
	}
	if err != nil {
		log.Error(3, "post data err:%s", err.Error())
		return "", err
	}

	result, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Error(3, "ioutil read data err:%s", err.Error())
		return "", err
	}

	if resp.StatusCode != 200 {
		log.Error(3, "post url :%s data resp.StatusCode :%d ;err:%s", url, resp.StatusCode, string(result))
		return "", errors.New(string(result))
	}
	log.Info("----result:%s", string(result))
	return string(result), nil
}

func GetHttpClient() *http.Client {

	tr := &http.Transport{
		TLSClientConfig:   &tls.Config{InsecureSkipVerify: true},
		DisableKeepAlives: true,
	}
	client := &http.Client{
		Transport: tr,
		Timeout:   60 * time.Second,
	}
	return client
}