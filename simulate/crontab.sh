#!/bin/bash  
      
step=10 #间隔的秒数，不能大于60  

for (( i = 0; ; i++ ));
do  
	echo $i
    cd /home/uyun/gopath/src/simulate/ && ./simulate &> /home/uyun/gopath/src/simulate/logs/simulate.log  
    sleep $step  
done  
      
exit 0 