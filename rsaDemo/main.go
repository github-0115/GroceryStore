package main

import (
	tool "GroceryStore/rsaDemo/rsa_tool"
	"flag"
	"fmt"
)

var msgStr string

func init() {
	flag.StringVar(&msgStr, "msg", "Content to be encrypted", "加密解密的数据")
	flag.Parse()
}

func main() {
	msgStr = `{"window":{"id":"6272e7790c1d42ec94a9056b30560c7c","Slug":"wxy","TenantId":"e10adc3949ba59abbe56e057f20f88dd","Version":1,"IsDelete":0,"Created":"2017-10-25T14:40:53+08:00","Updated":"2017-10-25T14:50:46+08:00","title":"wxy","theme":"default","bg":"/file/upload/background/e8a3ac80dc5802a514d163ce158badee_1508914241.jpg?theme=default","bgColor":"","bgType":2,"bgColorOpacity":0,"icon":"/upload/-6272e7790c1d42ec94a9056b30560c7c.png","screenRatio":"16:9","keepScreenRatio":true,"height":0,"width":0,"isTemplate":false,"innerTtemplate":false,"Data":{"bg":"/file/upload/background/e8a3ac80dc5802a514d163ce158badee_1508914241.jpg?theme=default","bgColor":"","bgColorOpacity":0,"bgType":2,"id":"6272e7790c1d42ec94a9056b30560c7c","keepScreenRatio":true,"screenRatio":"16:9","theme":"default","title":"wxy","version":1}},"widgets":[{"widget":{"id":"0a5148b1729b4e8da3ac747a826cf111","windowId":"6272e7790c1d42ec94a9056b30560c7c","fuuid":"0543361c8a424aebc7f1d2a0632aab9f","chart":{"imgClassName":"image-basic","klass":"image","option":{"background":"","borderStyle":{"color":"#fff","width":1},"type":"image"},"theme":"basic","title":"图片","type":"image"},"dataSetting":{},"setting":{"backgroundColor":"","backgroundImage":"","backgroundOpacity":100,"backgroundType":1,"borderColor":"","borderColorOpacity":100,"borderSize":1,"fontSize":20,"textColor":"#ffffff","textColorOpacity":100,"title":"","zIndex":3},"styleSetting":{"background":"/file/upload/background/4075a32d6880d8c51784b36f35a283fe_1508914277.jpg","borderStyle_color":"#fff","borderStyle_width":0},"sizex":22,"sizey":12,"col":99.95,"row":49.84,"x":0,"y":0,"height":0,"width":0},"chartConfigs":[]},{"widget":{"id":"69952667ff2f4c209fafa9553c446500","windowId":"6272e7790c1d42ec94a9056b30560c7c","fuuid":"270cfb6e592a4f84c8c98df9c4d1069e","chart":{"imgClassName":"image-basic","klass":"image","option":{"background":"","borderStyle":{"color":"#fff","width":1},"type":"image"},"theme":"basic","title":"图片","type":"image"},"dataSetting":{},"setting":{"backgroundColor":"","backgroundImage":"","backgroundOpacity":100,"backgroundType":1,"borderColor":"","borderColorOpacity":100,"borderSize":1,"fontSize":20,"textColor":"#ffffff","textColorOpacity":100,"title":"","zIndex":1},"styleSetting":{"background":"/file/upload/background/abc_1508914266.jpg","borderStyle_color":"#fff","borderStyle_width":0},"sizex":22,"sizey":12,"col":7.2,"row":4.92,"x":0,"y":0,"height":0,"width":0},"chartConfigs":[]},{"widget":{"id":"7b986acc17644d23aa454d23618cd872","windowId":"6272e7790c1d42ec94a9056b30560c7c","fuuid":"b0e43b6a06174bcfdd1420cc41b3ff93","chart":{"imgClassName":"image-basic","klass":"image","option":{"background":"","borderStyle":{"color":"#fff","width":1},"type":"image"},"theme":"basic","title":"图片","type":"image"},"dataSetting":{},"setting":{"backgroundColor":"","backgroundImage":"","backgroundOpacity":100,"backgroundType":1,"borderColor":"","borderColorOpacity":100,"borderSize":1,"fontSize":20,"textColor":"#ffffff","textColorOpacity":100,"title":"","zIndex":2},"styleSetting":{"background":"/file/upload/background/IMG_0174_1508914272.jpg","borderStyle_color":"#fff","borderStyle_width":0},"sizex":28.07,"sizey":43.25,"col":1,"row":28.96,"x":0,"y":0,"height":0,"width":0},"chartConfigs":[]},{"widget":{"id":"996f0556aa024ea08b883b17d04a7a99","windowId":"6272e7790c1d42ec94a9056b30560c7c","fuuid":"342f79a9c90f41d0eb366ed1ed8f40b3","chart":{"imgClassName":"image-basic","klass":"image","option":{"background":"","borderStyle":{"color":"#fff","width":1},"type":"image"},"theme":"basic","title":"图片","type":"image"},"dataSetting":{},"setting":{"backgroundColor":"","backgroundImage":"","backgroundOpacity":100,"backgroundType":1,"borderColor":"","borderColorOpacity":100,"borderSize":1,"fontSize":20,"textColor":"#ffffff","textColorOpacity":100,"title":"","zIndex":4},"styleSetting":{"background":"/file/upload/background/d9785fe5b1fcd130831748a68222fcfc_1508914289.jpg","borderStyle_color":"#fff","borderStyle_width":0},"sizex":22,"sizey":12,"col":100.59,"row":6.06,"x":0,"y":0,"height":0,"width":0},"chartConfigs":[]}],"datasources":[],"backgrounds":["/file/upload/background/4075a32d6880d8c51784b36f35a283fe_1508914277.jpg","/file/upload/background/abc_1508914266.jpg","/file/upload/background/IMG_0174_1508914272.jpg","/file/upload/background/d9785fe5b1fcd130831748a68222fcfc_1508914289.jpg","/file/upload/background/e8a3ac80dc5802a514d163ce158badee_1508914241.jpg?theme=default","/file/upload/-6272e7790c1d42ec94a9056b30560c7c.png"],"version":100}`
	fmt.Println(msgStr + "\n")
	//把数据转换成base64
	base64Str := tool.BaseEncodeToString([]byte(msgStr))
	fmt.Println("string to base64 :" + base64Str + "\n")
	//如果解密base64类型要先把数据转换
	msg := tool.BaseDecodeString(base64Str)
	if msg == nil {
		fmt.Println("base64 DecodeString err")
	}
	fmt.Println("base64 to string :" + string(msg) + "\n")

	// 解码公钥
	pubKey := tool.ParsePublicKey(tool.PublicKey)
	if pubKey == nil {
		fmt.Println("Parse PublicKey err")
	}

	// 加密数
	encryptPKCS15 := tool.EncryptPKCS1v15(pubKey, msg)
	if encryptPKCS15 == nil {
		fmt.Println("rsa EncryptPKCS1v15 err")
	}
	fmt.Println("EncryptPKCS1v15 string:" + string(encryptPKCS15) + "\n")

	encryptOAEP := tool.EncryptOAEP(pubKey, msg)
	if encryptOAEP == nil {
		fmt.Println("rsa EncryptOAEP err")
	}
	fmt.Println("EncryptOAEP string :" + string(encryptOAEP) + "\n")

	// 解析出私钥
	priKey := tool.ParsePrivateKey(tool.PrivateKey)
	if priKey == nil {
		fmt.Println("Parse PrivateKey err")
	}

	// 解密PKCS1v15加密的内容
	decryptPKCS := tool.DecryptPKCS1v15(priKey, encryptPKCS15)
	if decryptPKCS == nil {
		fmt.Println("rsa DecryptPKCS1v15 err")
	}
	fmt.Println("DecryptPKCS1v15 string:" + string(decryptPKCS) + "\n")

	// 解密RSA-OAEP方式加密后的内容
	decryptOAEP := tool.DecryptOAEP(priKey, encryptOAEP)
	if decryptOAEP == nil {
		fmt.Println("rsa DecryptOAEP err")
	}
	fmt.Println("DecryptOAEP string:" + string(decryptOAEP) + "\n")

}
