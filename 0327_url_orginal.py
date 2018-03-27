import urllib.request 
import json 
import datetime 

 

url = 'https://api.github.com/repos/bitcoin/bitcoin/issues?state=closed&labels=bug&page=10' 

u = urllib.request.urlopen(url) 

data = u.read() 
 

j = json.loads(data) 

#print (json.dumps(j, indent=4, sort_keys=True))
 
print(j[1]['labels'][0]['id'])
