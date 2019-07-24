using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace
{

    public enum SecurityProtocolType
    {
        Ssl3 = 0x30,
        Tls = 0xc0,
        Tls11 = 0x300,
        Tls12 = 0xc00
    }


    class ProgramTest
        {
            static void Main(string[] args)
            {
                string url = "https://www.test.com";
                string result = PostUrl(url, "grant_type=password&key=123"); // key=4da4193e-384b-44d8-8a7f-2dd8b076d784
                Console.WriteLine(result);
                Console.WriteLine("OVER");
                Console.ReadLine();
            }

            private static string PostUrl(string url, string postData)
            {
                HttpWebRequest request = null;
                if (url.StartsWith("https", StringComparison.OrdinalIgnoreCase))
                {
                    request = WebRequest.Create(url) as HttpWebRequest;
                    ServicePointManager.ServerCertificateValidationCallback = new RemoteCertificateValidationCallback(CheckValidationResult);
                    request.ProtocolVersion = HttpVersion.Version11;
    　　　　　　　　　// 这里设置了协议类型。
                    ServicePointManager.SecurityProtocol = (SecurityProtocolType)3072;// SecurityProtocolType.Tls1.2;
                    request.KeepAlive = false;
                    ServicePointManager.CheckCertificateRevocationList = true;
                    ServicePointManager.DefaultConnectionLimit = 100;
                    ServicePointManager.Expect100Continue = false;
                }
                else
                {
                    request = (HttpWebRequest)WebRequest.Create(url);
                }

                request.Method = "POST";    //使用get方式发送数据
                request.ContentType = "application/x-www-form-urlencoded";
                request.Referer = null;
                request.AllowAutoRedirect = true;
                request.UserAgent = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727)";
                request.Accept = "*/*";

                byte[] data = Encoding.UTF8.GetBytes(postData);
                Stream newStream = request.GetRequestStream();
                newStream.Write(data, 0, data.Length);
                newStream.Close();

                //获取网页响应结果
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                Stream stream = response.GetResponseStream();
                //client.Headers.Add("Content-Type", "application/x-www-form-urlencoded");
                string result = string.Empty;
                using (StreamReader sr = new StreamReader(stream))
                {
                    result = sr.ReadToEnd();
                }

                return result;
            }

            private static bool CheckValidationResult(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors errors)
            {
                return true; //总是接受
            }
        }
}