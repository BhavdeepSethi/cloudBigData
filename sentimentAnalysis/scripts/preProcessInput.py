#!/usr/bin/python
import MySQLdb as mdb
import sys, os
import re
import time
import glob
import datetime


def roundTime(dt=None, roundTo=60):
   """Round a datetime object to any time laps in seconds
   dt : datetime.datetime object, default now.
   roundTo : Closest number of seconds to round to, default 1 minute.
   Author: Thierry Husson 2012 - Use it as you want but don't blame me.
   """
   if dt == None : dt = datetime.datetime.now()
   seconds = (dt - dt.min).seconds
   # // is a floor division, not a comment on following line:
   rounding = (seconds+roundTo/2) // roundTo * roundTo
   return dt + datetime.timedelta(0,rounding-seconds,-dt.microsecond)



fromVar=1398382980000
currVar=fromVar
inc=338
with open('/Users/bhavdeepsethi/Downloads/newData.txt','w') as newF:
	with open('/Users/bhavdeepsethi/Downloads/Assignment3Tweets-2') as fp:
		for line in fp:
			newTime = roundTime(datetime.datetime.fromtimestamp(float(currVar)/1000), roundTo=60*60).strftime("%Y%m%d%H")
			newF.write(str(newTime)+":"+line)
			currVar=currVar+inc

