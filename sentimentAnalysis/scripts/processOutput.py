#!/usr/bin/python
import csv 
import sys, os
import re
import time
import glob
import datetime
import glob

outerDict={}

def listFiles():
        fileContentList = []
        global fileNameList
        count = 0
        for filePath in glob.iglob('/Users/bhavdeepsethi/Downloads/sentimentOutput/*'):
                fileName = os.path.basename(filePath).split(".")[0]             
                '''print fileName'''
                readFileContent(filePath) 
        return fileContentList

def readFileContent(filePath):
        # Open a file
        global outerDict
        fileContentList = []               
        with open(filePath, "r") as fp:
            for line in fp:
               newLine = line.split(",")
               if newLine[1] in outerDict:
                  innerDict = outerDict[newLine[1]]
               else:
                  innerDict={}               
               sentimentScore = newLine[2].split("\t")
               if(sentimentScore[0]=="positive"):
                  score = int(sentimentScore[1])
               else:
                  score = -int(sentimentScore[1])
               if newLine[0] in innerDict:
                  finalScore = int(innerDict[newLine[0]])+score
               else:
                  finalScore = score
               innerDict[newLine[0]]=finalScore
               outerDict[newLine[1]]=innerDict
               #fileContentList.append([newLine[1], newLine[0], score])        
        return

listFiles()
with open('/Users/bhavdeepsethi/Downloads/finalOutput.csv','w') as fp:
   fp.write("Topic,Date,Sentiment\n")
   for outerKey,outerVal in outerDict.iteritems():
      for newKey in sorted(outerVal):
         if len(newKey)==10:
            fp.write(outerKey+","+newKey+","+str(outerVal[newKey])+"\n")
print outerDict
