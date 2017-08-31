package strings

import (
	"fmt"
	"reflect"
	"strconv"
	"strings"
	"unsafe"
)

func SqlUrlCheck(sqlUrl string) bool {
	var sqlStrs = []string{"drop", "delete", "update", "insert", "create", "set", "grant", "shutdown", "stop", "sleep", "alert", "truncate", "remove", "revoke"}
	lowerSqlUrl := strings.ToLower(sqlUrl)
	if !strings.HasPrefix(lowerSqlUrl, "select") {
		log.Error(3, "lowerSqlUrl not Contains:%s err", "select")
		return false
	}

	//将lowerSqlUrl中的“,” “.” “;” “=” “>” “<” “'”变为" "
	lowerSqlUrl = strings.Map(Slash, lowerSqlUrl)
	//以空格切割字符串，变为字符串数组
	ss := strings.SplitN(lowerSqlUrl, " ", -1)
	log.Info("lowerSqlUrl  Contains:%s ", ss)
	for _, s := range ss {
		if strings.EqualFold(s, "") {
			continue
		}
		log.Info("lowerSqlUrl  Contains:%s ", s)
		for _, str := range sqlStrs {
			if strings.EqualFold(s, str) {
				log.Error(3, "lowerSqlUrl Contains:%s err", str)
				return false
			}
		}
	}

	return true
}

//替换字符串,将","替换成空格
func Slash(r rune) rune {
	var ss = []rune{',', '.', ';', '\'', '`', '=', '>', '<', '(', ')'}
	for _, s := range ss {
		if r == s {
			return ' '
		}
	}
	return r
}

func StringsFallback2(val1 string, val2 string) string {
	if val1 != "" {
		return val1
	}
	return val2
}

func StringsFallback3(val1 string, val2 string, val3 string) string {
	if val1 != "" {
		return val1
	}
	if val2 != "" {
		return val2
	}
	return val3
}

func UnicToString(text string) string {
	sUnicodev := strings.Split(text, "\\u")
	var context string
	for _, v := range sUnicodev {
		if len(v) < 1 {
			continue
		}
		temp, err := strconv.ParseInt(v, 16, 32)
		if err != nil {
			panic(err)
		}
		context += fmt.Sprintf("%c", temp)
	}
	return context

}

func UnicodeToString(text string) string {
	if strings.Contains(text, "\\u") {
		str, err := strconv.Unquote(`"` + text + `"`)
		if err != nil {
			return text
		}
		return str
	}
	return text
}

//字符串删除前后空格，包括tab
func DelBlank(s string) string {
	rs := []rune(s)
	var t int
	//从头开始循环一直到第一个符合的字段，记录那个字段的位置,截取字符串
	for k, r := range rs {
		rint := int(r)
		if rint != 32 {
			if rint != 9 {
				t = k
				break
			}
		}
	}
	delFront := CutString(s, t, len(s))
	rsF := []rune(delFront)
	//从末尾开始循环
	for tt := len(rsF); tt > 0; tt-- {
		if int(rsF[tt-1]) != 32 {
			if int(rsF[tt-1]) != 9 {
				t = tt
				break
			}
		}
	}
	delLast := CutString(delFront, 0, t)
	return delLast
}

//截取字符创的操作
func CutString(str string, start int, length int) string {
	rs := []rune(str)
	rl := len(rs)
	end := 0

	if start < 0 {
		start = rl - 1 + start
	}
	end = start + length
	if start > end {
		start, end = end, start
	}
	if start < 0 {
		start = 0
	}
	if start > rl {
		start = rl
	}
	if end < 0 {
		end = 0
	}
	if end > rl {
		end = rl
	}
	return string(rs[start:end])
}

//将一个中文的长度也算作1
func Show_strlen(s string) int {
	sl := 0
	rs := []rune(s)
	for _, r := range rs {
		rint := int(r)
		if rint < 128 {
			sl++
		} else {
			sl += 1
		}
	}
	return sl
}

func BytesToString(b []byte) string {
	bh := (*reflect.SliceHeader)(unsafe.Pointer(&b))
	sh := reflect.StringHeader{bh.Data, bh.Len}
	return *(*string)(unsafe.Pointer(&sh))
}
