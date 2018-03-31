import urllib.request
import json 
import datetime 
import codecs
import requests
import re


comments = codecs.open("commentsurl.txt", 'r','utf-8')
f = codecs.open("realcomment1.txt", "w" ,"utf-8")

i=0
j=0
list=[]
for i in range (0, 562):
    list.append("%s" %comments.readline())
    i+=1

for j in range(0,58):
    url=list[j]
    u = urllib.request.urlopen(url) 
    data = u.read() 
    jd = json.loads(data)

    S=list[j]
    numbers=re.findall("\d+", S)
    f.write("%d. bug report number : %s\n" %(j, numbers))
    

    i=0
    for item in jd:
        if(jd[i]['id']):
            f.write("comment number: %s \n" %jd[i]['id'])
        if(jd[i]['body']):
            f.write("body: %s \n" %jd[i]['body'])
            
        i+=1
        f.write("\n")
  
    j+=1

comments.close()
f.close()

