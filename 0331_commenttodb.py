import pymysql
import codecs
import string

conn=pymysql.connect(host='localhost', user='root', password='err',db='bug',charset='utf8')
curs=conn.cursor()

for k in range(2,12):
    f=open("realcomment%d.txt"%k, "r",encoding="utf-8",errors='ignore')
    
    j=0
    s=f.read()
    splits=s.split('. bug report number')

    locate_rn=0


    bugreport_num=0


    for i in range (1, len(splits)):
        string=splits[i]
        locate_rn=string.find(": ['")
        locate=string.find("']\n")
        bugreport_num=string[locate_rn+4:locate]

        count=string.count("comment number")
        locate_cn=0
        locate_body=0
        comment_num=0
        comment_body=''
        splitss=string.split('comment num')

        #각 버그 리포트마다 다수의 comment
        for j in range(1,count+1):
            
            instring=splitss[j]
            locate_cn=instring.find("ber: ")
            locate_body=instring.find("body: ")
            
            comment_num=instring[locate_cn+5:locate_body-1]
            if(j==count):
                comment_body=instring[locate_body+6:len(instring)-2]
            else:
                comment_body=instring[locate_body+6:]
            
            sql="""insert into comment(bugreport_num,comment_num,comment_body) values (%s, %s, %s)"""
            curs.execute(sql,(bugreport_num,comment_num,comment_body))
            conn.commit()
   

    f.close()

conn.close()
