# -*- coding: utf-8 -*-
"""
Created on Tue Oct  9 01:11:23 2018

@author: Kaumudi
"""


# importing csv module 
import csv 
  
# csv file name 
filename = "aapl.csv"
  
# initializing the titles and rows list 
fields = [] 
rows = [] 
date= []
text=[]

filename="output_got_sp500.csv"
with open(filename,'r', encoding="utf8") as csvfile: 
    # creating a csv reader object 
    csvreader = csv.reader(csvfile) 
      
    # extracting field names through first row 
    fields = next(csvreader)
  
    # extracting each data row one by one 
    for row in csvreader: 
        s=''.join(row)
        data=s.split(";")
        date.append(data[0])
        text.append(data[1])
        
    # get total number of rows 
    print("Total no. of rows: %d"%(csvreader.line_num)) 
# printing the field names 
print('Field names are:' + ', '.join(field for field in fields)) 
  
#  printing first 5 rows 
print('\nFirst 5 rows are:\n') 
for row in text[:5]: 
    # parsing each column of a row 
    print(row)