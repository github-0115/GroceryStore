package http

import (
	"bytes"
	"crypto/tls"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/url"
	"strings"
	"time"
)

func GetClassMoldsData(dataSourceId string, ip string, tenantId string, classLayerCode string, classCode string) (string, error) {

	v := url.Values{}
	v.Set("tenantId", tenantId)
	v.Add("moldDataSource", dataSourceId)
	v.Add("classLayerCode", classLayerCode)
	v.Add("classCode", classCode)

	u, err := url.Parse(ip + "?")
	if err != nil {
		fmt.Println("url parse err:%s", err.Error())
		return "", err
	}
	u.RawQuery = v.Encode()

	res, err := GetRequest(u.String())
	if err != nil {
		fmt.Println(" get %s Molds res err %s", classCode, err.Error())

		return "", err
	}

	return res, nil
}

func GetRequest(url string) (string, error) {
	if !strings.Contains(url, "http://") {
		url = "http://" + url
	}

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		fmt.Println("get url :%s request err:%s", url, err.Error())
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
		fmt.Println("get data path :%s err:%s", url, err.Error())
		return "", err
	}

	result, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println("ioutil read data err:%s", err.Error())
		return "", err
	}

	if resp.StatusCode != 200 {
		fmt.Println("get conn status resp.StatusCode :%d ;err:%s", resp.StatusCode, string(result))
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
		fmt.Println("get post url :%s request err:%s", url, err.Error())
		return "", err
	}
	req.Header.Add("Content-Type", "application/json")

	client := GetHttpClient()
	resp, err := client.Do(req)
	if resp != nil {
		defer resp.Body.Close()
	}
	if err != nil {
		fmt.Println("post data err:%s", err.Error())
		return "", err
	}

	result, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println("ioutil read data err:%s", err.Error())
		return "", err
	}

	if resp.StatusCode != 200 {
		fmt.Println("post url :%s data resp.StatusCode :%d ;err:%s", url, resp.StatusCode, string(result))
		return "", errors.New(string(result))
	}
	fmt.Println("----result:%s", string(result))
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
