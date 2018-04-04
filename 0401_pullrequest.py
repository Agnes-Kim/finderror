import urllib.request 
import json 
import datetime 
import codecs

#~287page
f=codecs.open("pull6.txt","w","utf-8")
#url = https://api.github.com/repos/bitcoin/bitcoin/pulls?state=closed&labels=bug&page=287
for x in range (250,288):
    url='https://api.github.com/repos/bitcoin/bitcoin/pulls?state=closed&labels=bug&page==%s' %x
    u = urllib.request.urlopen(url) 
    data = u.read() 

    j = json.loads(data)

    #print(j[0]['labels'][0]['id'])

    i=0
    for item in j:
        f.write("%s\n" %j[i])
        i+=1
        f.write("\n")
    x+=1

f.close()
