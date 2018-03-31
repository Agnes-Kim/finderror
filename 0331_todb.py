import pymysql
import codecs
import string

f=codecs.open("lol.txt","r","utf-8")
conn=pymysql.connect(host='localhost', user='root', password='err',db='bug',charset='utf8')
curs=conn.cursor()

bugno=1
title='this is title'
body='this is body'

s=f.read()
splits=s.split('Reportkyk ') #리포트마다 list splits에 저장

i=1
for i in range (1,len(splits)):
    string=splits[i]
    locate_number=string.find('kyknumber:')
    locate_title=string.find('kyktitle:')
    locate_body=string.find('kykbody:')
    bugno=string[locate_number+11:locate_title-1]
    title=string[locate_title+10:locate_body-1]
    body=string[locate_body+9:]
#    print(bugno)
    
    sql="""insert into bugreport(bugno,title,body) values (%s, %s, %s)"""
    curs.execute(sql,(bugno,title,body))
    conn.commit()
    i+=1





conn.close()
f.close()
