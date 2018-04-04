import urllib.request 
import json 
import datetime 
import codecs

bugreport = 0
f=codecs.open("lol.txt","w","utf-8")
#url = 'https://api.github.com/repos/bitcoin/bitcoin/issues?state=closed&labels=bug&page=10'

for x in range (1,20):
    url='https://api.github.com/repos/bitcoin/bitcoin/issues?state=closed&labels=bug&page=%s' %x
    u = urllib.request.urlopen(url) 
    data = u.read() 

    j = json.loads(data)

    #print(j[0]['labels'][0]['id'])

    i=0
    for item in j:
        html_url=j[i]['html_url']
        if "issues" in html_url:
            f.write("Reportkyk kyknumber: %s\n" %j[i]['number'])
            f.write("kyktitle: %s\n" %j[i]['title'])
            f.write("kykbody: %s\n" %j[i]['body'])
        i+=1
        f.write("\n")
  
f.close()
